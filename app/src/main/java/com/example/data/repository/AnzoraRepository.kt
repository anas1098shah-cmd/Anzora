package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.AnzoraCatalogDefaults
import com.example.data.model.CartItem
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.StoreConfig
import com.example.data.model.UserProfile
import com.example.data.util.OperationType
import com.example.data.util.WhatsAppHelper
import com.example.data.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class AnzoraRepository(private val db: FirebaseFirestore) {

    // CRITICAL: Always initialize with R.string.firestore_database_id
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth
        get() = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun isCurrentUserAdmin(): Boolean {
        val user = auth.currentUser ?: return false
        return user.email == BOOTSTRAPPED_ADMIN_EMAIL && user.isEmailVerified
    }

    // 1. Observe Active Storefront Products
    fun observeActiveProducts(): Flow<List<Product>> = flow {
        requireUserId()
        val path = "products"
        emitAll(
            db.collection(path)
                .whereEqualTo("active", true)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        Product::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    // 2. Observe All Products (For Admin Dashboard)
    fun observeAllProductsForAdmin(): Flow<List<Product>> = flow {
        requireUserId()
        val path = "products"
        emitAll(
            db.collection(path)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        Product::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    // 3. Observe Store Configuration (Delivery Fee, Free Delivery Threshold, WhatsApp Number, Banner)
    fun observeStoreConfig(): Flow<StoreConfig> = flow {
        requireUserId()
        val path = "store_config/global"
        emitAll(
            db.collection("store_config").document("global")
                .snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(
                            StoreConfig::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        ) ?: StoreConfig()
                    } else {
                        StoreConfig()
                    }
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.GET, path)
                    emit(StoreConfig())
                }
        )
    }

    // 4. Observe Customer's Own Orders (Scoped strictly to userId)
    fun observeCustomerOrders(userId: String): Flow<List<Order>> = flow {
        requireUserId()
        val path = "orders"
        emitAll(
            db.collection(path)
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        Order::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    // 5. Observe All Orders (Admin Only)
    fun observeAllOrdersForAdmin(): Flow<List<Order>> = flow {
        requireUserId()
        val path = "orders"
        emitAll(
            db.collection(path)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        Order::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    // 6. Get Single Order By ID
    suspend fun getOrderById(orderId: String): Result<Order> {
        requireUserId()
        val docRef = db.collection("orders").document(orderId)
        return try {
            val snapshot = docRef.get().await()
            val order = snapshot.toObject(
                Order::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            ) ?: throw NoSuchElementException("Order not found")
            Result.success(order)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            Result.failure(e)
        }
    }

    // 7. Observe User Saved Delivery Profile
    fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
        requireUserId()
        val path = "users/$userId"
        emitAll(
            db.collection("users").document(userId)
                .snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(
                            UserProfile::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                    } else {
                        null
                    }
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.GET, path)
                    throw error
                }
        )
    }

    // 8. Save Customer Delivery Profile
    suspend fun saveUserProfile(
        fullName: String,
        phone: String,
        city: String,
        address: String
    ): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        val payload = mapOf(
            "userId" to uid,
            "fullName" to fullName.trim().take(100),
            "phone" to phone.trim().take(25),
            "city" to city.trim().take(80),
            "address" to address.trim().take(400),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    // 9. Place Order with Backend Stock Verification & Atomic Batch Write
    suspend fun placeOrder(
        customerName: String,
        customerPhone: String,
        customerCity: String,
        customerAddress: String,
        orderNotes: String,
        cartItems: List<CartItem>,
        deliveryFeePkr: Long,
        paymentMethod: String
    ): Result<Order> {
        val uid = requireUserId()
        if (cartItems.isEmpty()) {
            return Result.failure(IllegalArgumentException("Your cart is empty."))
        }
        val orderId = "ord_${UUID.randomUUID().toString().replace("-", "").take(12)}"
        val orderRef = db.collection("orders").document(orderId)

        return try {
            val subtotal = cartItems.sumOf { it.lineTotalPkr }
            val totalUnits = cartItems.sumOf { it.quantity }.toLong()
            val safeDeliveryFee = deliveryFeePkr.coerceIn(0L, 10000L)
            val totalPkr = subtotal + safeDeliveryFee
            val summaryText = WhatsAppHelper.buildOrderItemsSummary(cartItems).take(3000)

            val batch = db.batch()

            // Deduct stock for products that exist in Firestore
            for (cartItem in cartItems) {
                val prodId = cartItem.product.id
                if (prodId.isNotBlank()) {
                    val prodRef = db.collection("products").document(prodId)
                    val snap = prodRef.get().await()
                    if (snap.exists()) {
                        val currentStock = snap.getLong("stock") ?: cartItem.product.stock
                        val newStock = (currentStock - cartItem.quantity).coerceAtLeast(0L)
                        if (newStock < currentStock) {
                            batch.update(
                                prodRef,
                                mapOf(
                                    "stock" to newStock,
                                    "updatedAt" to FieldValue.serverTimestamp()
                                )
                            )
                        }
                    }
                }
            }

            val orderPayload = mapOf(
                "userId" to uid,
                "customerName" to customerName.trim().take(100),
                "customerPhone" to customerPhone.trim().take(25),
                "customerCity" to customerCity.trim().take(80),
                "customerAddress" to customerAddress.trim().take(400),
                "orderNotes" to orderNotes.trim().take(500),
                "itemsSummary" to summaryText,
                "itemCount" to totalUnits.coerceIn(1L, 500L),
                "subtotalPkr" to subtotal.coerceAtLeast(50L),
                "deliveryFeePkr" to safeDeliveryFee,
                "totalPkr" to (subtotal.coerceAtLeast(50L) + safeDeliveryFee),
                "paymentMethod" to if (paymentMethod == "COD_WHATSAPP") "COD_WHATSAPP" else "COD",
                "status" to "Pending",
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )

            batch.set(orderRef, orderPayload)
            batch.commit().await()

            val createdOrder = Order(
                id = orderId,
                userId = uid,
                customerName = customerName.trim(),
                customerPhone = customerPhone.trim(),
                customerCity = customerCity.trim(),
                customerAddress = customerAddress.trim(),
                orderNotes = orderNotes.trim(),
                itemsSummary = summaryText,
                itemCount = totalUnits,
                subtotalPkr = subtotal,
                deliveryFeePkr = safeDeliveryFee,
                totalPkr = totalPkr,
                paymentMethod = if (paymentMethod == "COD_WHATSAPP") "COD_WHATSAPP" else "COD",
                status = "Pending"
            )
            Result.success(createdOrder)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, orderRef.path)
            Result.failure(e)
        }
    }

    // 10. Customer Cancel Pending Order
    suspend fun cancelCustomerOrder(orderId: String): Result<Unit> {
        requireUserId()
        val docRef = db.collection("orders").document(orderId)
        return try {
            docRef.update(
                mapOf(
                    "status" to "Cancelled",
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    // 11. Admin: Create or Update Product
    suspend fun saveProductAsAdmin(product: Product, isNew: Boolean): Result<String> {
        requireUserId()
        val cleanId = if (product.id.isNotBlank()) {
            product.id.replace("[^a-zA-Z0-9_\\-]".toRegex(), "_")
        } else {
            "prod_${UUID.randomUUID().toString().replace("-", "").take(10)}"
        }
        val docRef = db.collection("products").document(cleanId)
        val safePrice = product.pricePkr.coerceIn(50L, 1000000L)
        val safeOriginalPrice = product.originalPricePkr.coerceAtLeast(safePrice).coerceAtMost(1000000L)
        val safeImages = product.imageUrls.filter { it.isNotBlank() }.take(6).ifEmpty { listOf("preset:earbuds") }

        return try {
            if (isNew) {
                val createPayload = mapOf(
                    "name" to product.name.trim().take(120),
                    "category" to product.category,
                    "pricePkr" to safePrice,
                    "originalPricePkr" to safeOriginalPrice,
                    "stock" to product.stock.coerceIn(0L, 100000L),
                    "sku" to product.sku.trim().take(40).ifBlank { "ANZ-SKU" },
                    "description" to product.description.trim().take(2000).ifBlank { "Premium ANZORA accessory." },
                    "specs" to product.specs.trim().take(1500).ifBlank { "Standard ANZORA specifications." },
                    "imageUrls" to safeImages,
                    "featured" to product.featured,
                    "active" to product.active,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(createPayload).await()
            } else {
                val updatePayload = mapOf(
                    "name" to product.name.trim().take(120),
                    "category" to product.category,
                    "pricePkr" to safePrice,
                    "originalPricePkr" to safeOriginalPrice,
                    "stock" to product.stock.coerceIn(0L, 100000L),
                    "sku" to product.sku.trim().take(40).ifBlank { "ANZ-SKU" },
                    "description" to product.description.trim().take(2000).ifBlank { "Premium ANZORA accessory." },
                    "specs" to product.specs.trim().take(1500).ifBlank { "Standard ANZORA specifications." },
                    "imageUrls" to safeImages,
                    "featured" to product.featured,
                    "active" to product.active,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.update(updatePayload).await()
            }
            Result.success(cleanId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(
                e,
                if (isNew) OperationType.CREATE else OperationType.UPDATE,
                docRef.path
            )
            Result.failure(e)
        }
    }

    // 12. Admin: Delete Product
    suspend fun deleteProductAsAdmin(productId: String): Result<Unit> {
        requireUserId()
        val docRef = db.collection("products").document(productId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    // 13. Admin: Seed Default ANZORA Catalog to Cloud Firestore
    suspend fun seedInitialCatalogAsAdmin(): Result<Int> {
        requireUserId()
        return try {
            val batch = db.batch()
            for (prod in AnzoraCatalogDefaults.INITIAL_PRODUCTS) {
                val docRef = db.collection("products").document(prod.id)
                val payload = mapOf(
                    "name" to prod.name,
                    "category" to prod.category,
                    "pricePkr" to prod.pricePkr,
                    "originalPricePkr" to prod.originalPricePkr,
                    "stock" to prod.stock,
                    "sku" to prod.sku,
                    "description" to prod.description,
                    "specs" to prod.specs,
                    "imageUrls" to prod.imageUrls,
                    "featured" to prod.featured,
                    "active" to prod.active,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                batch.set(docRef, payload)
            }
            batch.commit().await()
            Result.success(AnzoraCatalogDefaults.INITIAL_PRODUCTS.size)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, "products")
            Result.failure(e)
        }
    }

    // 14. Admin: Update Order Status
    suspend fun updateOrderStatusAsAdmin(orderId: String, newStatus: String): Result<Unit> {
        requireUserId()
        val docRef = db.collection("orders").document(orderId)
        return try {
            docRef.update(
                mapOf(
                    "status" to newStatus,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    // 15. Admin: Save Store Config (Delivery Fee, Free Shipping Threshold, Banner)
    suspend fun saveStoreConfigAsAdmin(config: StoreConfig): Result<Unit> {
        requireUserId()
        val docRef = db.collection("store_config").document("global")
        val payload = mapOf(
            "deliveryFeePkr" to config.deliveryFeePkr.coerceIn(0L, 10000L),
            "freeDeliveryThresholdPkr" to config.freeDeliveryThresholdPkr.coerceIn(0L, 500000L),
            "whatsappNumber" to config.whatsappNumber.trim().take(25).ifBlank { "+923712047636" },
            "announcementBanner" to config.announcementBanner.trim().take(250).ifBlank {
                "Nationwide Cash on Delivery | ANZORA Pakistan"
            },
            "supportEmail" to config.supportEmail.trim().take(120).ifBlank { "support@anzora.pk" },
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    companion object {
        const val BOOTSTRAPPED_ADMIN_EMAIL = "anas1098shah@gmail.com"
    }
}

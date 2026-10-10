package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AnzoraDao {

    // Categories
    @Query("SELECT * FROM categories ORDER BY displayOrder ASC")
    fun observeCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCategory(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :categoryId")
    suspend fun deleteCategory(categoryId: String)

    // Products
    @Query("SELECT * FROM products ORDER BY isFeatured DESC, updatedAt DESC")
    fun observeAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    suspend fun getProductById(productId: String): ProductEntity?

    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    fun observeProductById(productId: String): Flow<ProductEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProduct(product: ProductEntity)

    @Query("UPDATE products SET stockQuantity = :newStock, updatedAt = :timestamp WHERE id = :productId")
    suspend fun updateProductStock(productId: String, newStock: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE products SET pricePkr = :newPricePkr, compareAtPricePkr = :compareAtPkr, updatedAt = :timestamp WHERE id = :productId")
    suspend fun updateProductPrice(productId: String, newPricePkr: Long, compareAtPkr: Long?, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProduct(productId: String)

    // Cart
    @Query("SELECT * FROM cart_items ORDER BY addedAt DESC")
    fun observeCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items")
    suspend fun getCartItemsSnapshot(): List<CartItemEntity>

    @Query("SELECT * FROM cart_items WHERE productId = :productId LIMIT 1")
    suspend fun getCartItem(productId: String): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCartItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun removeCartItem(productId: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    // Customer Accounts
    @Query("SELECT * FROM customer_accounts WHERE isLoggedIn = 1 LIMIT 1")
    fun observeLoggedInCustomer(): Flow<CustomerAccountEntity?>

    @Query("SELECT * FROM customer_accounts WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getLoggedInCustomer(): CustomerAccountEntity?

    @Query("SELECT * FROM customer_accounts WHERE LOWER(email) = LOWER(:email) OR phone = :phone LIMIT 1")
    suspend fun findCustomerByEmailOrPhone(email: String, phone: String): CustomerAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: CustomerAccountEntity): Long

    @Update
    suspend fun updateCustomer(customer: CustomerAccountEntity)

    @Query("UPDATE customer_accounts SET isLoggedIn = 0")
    suspend fun logoutAllCustomers()

    @Query("UPDATE customer_accounts SET isLoggedIn = 1 WHERE id = :customerId")
    suspend fun setCustomerLoggedIn(customerId: Long)

    // Orders
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun observeAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE customerAccountId = :accountId OR customerPhone = :phone ORDER BY createdAt DESC")
    fun observeCustomerOrders(accountId: Long, phone: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE orderNumber = :orderNumber LIMIT 1")
    suspend fun getOrderByNumber(orderNumber: String): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :newStatus, updatedAt = :timestamp WHERE orderNumber = :orderNumber")
    suspend fun updateOrderStatus(orderNumber: String, newStatus: String, timestamp: Long = System.currentTimeMillis())

    // Store Settings
    @Query("SELECT * FROM store_settings WHERE id = 1 LIMIT 1")
    fun observeStoreSettings(): Flow<StoreSettingsEntity?>

    @Query("SELECT * FROM store_settings WHERE id = 1 LIMIT 1")
    suspend fun getStoreSettings(): StoreSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStoreSettings(settings: StoreSettingsEntity)
}

package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AnzoraCatalogDefaults
import com.example.data.model.AnzoraCategories
import com.example.data.model.CartItem
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.StoreConfig
import com.example.data.model.UserProfile
import com.example.data.repository.AnzoraRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

enum class SortOption(val label: String) {
    FEATURED("Featured"),
    PRICE_LOW_TO_HIGH("Price: Low to High"),
    PRICE_HIGH_TO_LOW("Price: High to Low"),
    NEWEST("Newest Arrivals")
}

class AnzoraViewModel(
    private val repository: AnzoraRepository,
    val currentUserId: String
) : ViewModel() {

    val isAdmin: Boolean
        get() = repository.isCurrentUserAdmin()

    // Search, Category Filter & Sort State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(AnzoraCategories.ALL)
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedSort = MutableStateFlow(SortOption.FEATURED)
    val selectedSort: StateFlow<SortOption> = _selectedSort.asStateFlow()

    private val _inStockOnly = MutableStateFlow(false)
    val inStockOnly: StateFlow<Boolean> = _inStockOnly.asStateFlow()

    // Shopping Cart State
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // Status Banner / Feedback Toast
    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    private val _isSubmittingOrder = MutableStateFlow(false)
    val isSubmittingOrder: StateFlow<Boolean> = _isSubmittingOrder.asStateFlow()

    private val _lastPlacedOrder = MutableStateFlow<Order?>(null)
    val lastPlacedOrder: StateFlow<Order?> = _lastPlacedOrder.asStateFlow()

    // Store Configuration Stream
    val storeConfig: StateFlow<StoreConfig> = repository.observeStoreConfig()
        .catch { e ->
            Log.w(TAG, "Error observing store config, falling back to default", e)
            emit(StoreConfig())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = StoreConfig()
        )

    // Storefront Products Stream (with seamless fallback to initial ANZORA catalog when cloud collection is empty)
    val catalogState: StateFlow<UiState<List<Product>>> = repository.observeActiveProducts()
        .map<List<Product>, UiState<List<Product>>> { cloudList ->
            val effectiveList = if (cloudList.isEmpty()) {
                AnzoraCatalogDefaults.INITIAL_PRODUCTS
            } else {
                cloudList
            }
            UiState.Success(effectiveList)
        }
        .catch { error ->
            Log.w(TAG, "Error observing active products", error)
            emit(UiState.Error(error.message ?: "Failed to load ANZORA catalog"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    // Filtered & Sorted Products
    val filteredProductsState: StateFlow<UiState<List<Product>>> = combine(
        catalogState,
        _searchQuery,
        _selectedCategory,
        _selectedSort,
        _inStockOnly
    ) { state, query, category, sort, stockOnly ->
        when (state) {
            is UiState.Loading -> UiState.Loading
            is UiState.Error -> state
            is UiState.Success -> {
                val filtered = state.data.filter { product ->
                    val matchesCategory = category == AnzoraCategories.ALL ||
                        product.category.equals(category, ignoreCase = true)
                    val matchesQuery = query.isBlank() ||
                        product.name.contains(query, ignoreCase = true) ||
                        product.description.contains(query, ignoreCase = true) ||
                        product.category.contains(query, ignoreCase = true) ||
                        product.sku.contains(query, ignoreCase = true)
                    val matchesStock = !stockOnly || product.stock > 0L
                    matchesCategory && matchesQuery && matchesStock
                }
                val sorted = when (sort) {
                    SortOption.FEATURED -> filtered.sortedWith(
                        compareByDescending<Product> { it.featured }.thenBy { it.name }
                    )
                    SortOption.PRICE_LOW_TO_HIGH -> filtered.sortedBy { it.pricePkr }
                    SortOption.PRICE_HIGH_TO_LOW -> filtered.sortedByDescending { it.pricePkr }
                    SortOption.NEWEST -> filtered.sortedByDescending {
                        it.createdAt?.seconds ?: 0L
                    }
                }
                UiState.Success(sorted)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = UiState.Loading
    )

    // Customer Order History Stream
    val customerOrdersState: StateFlow<UiState<List<Order>>> = repository.observeCustomerOrders(currentUserId)
        .map<List<Order>, UiState<List<Order>>> { list ->
            val sorted = list.sortedByDescending { it.createdAt?.seconds ?: 0L }
            UiState.Success(sorted)
        }
        .catch { error ->
            Log.w(TAG, "Error observing customer orders", error)
            emit(UiState.Error(error.message ?: "Failed to load order history"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    // Saved User Delivery Profile Stream
    val userProfile: StateFlow<UserProfile?> = repository.observeUserProfile(currentUserId)
        .catch { error ->
            Log.w(TAG, "Error observing user profile", error)
            emit(null)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = null
        )

    // Admin All Products Stream (Only collected when Admin opens Admin screen)
    val adminProductsState: StateFlow<UiState<List<Product>>> = repository.observeAllProductsForAdmin()
        .map<List<Product>, UiState<List<Product>>> { list ->
            UiState.Success(list.sortedBy { it.name })
        }
        .catch { error ->
            Log.w(TAG, "Error observing admin products", error)
            emit(UiState.Error(error.message ?: "Unauthorized or failed to load products"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    // Admin All Orders Stream (Only collected when Admin opens Admin screen)
    val adminOrdersState: StateFlow<UiState<List<Order>>> = repository.observeAllOrdersForAdmin()
        .map<List<Order>, UiState<List<Order>>> { list ->
            UiState.Success(list.sortedByDescending { it.createdAt?.seconds ?: 0L })
        }
        .catch { error ->
            Log.w(TAG, "Error observing admin orders", error)
            emit(UiState.Error(error.message ?: "Unauthorized or failed to load all orders"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    // Filter Actions
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun selectSort(sortOption: SortOption) {
        _selectedSort.value = sortOption
    }

    fun toggleInStockOnly(inStockOnly: Boolean) {
        _inStockOnly.value = inStockOnly
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedCategory.value = AnzoraCategories.ALL
        _selectedSort.value = SortOption.FEATURED
        _inStockOnly.value = false
    }

    // Cart Actions
    fun addToCart(product: Product, quantity: Int = 1) {
        if (product.stock <= 0L) {
            showFeedback("${product.name} is currently out of stock.")
            return
        }
        _cartItems.update { current ->
            val existingIndex = current.indexOfFirst { it.product.id == product.id && it.product.name == product.name }
            if (existingIndex >= 0) {
                val existing = current[existingIndex]
                val newQty = (existing.quantity + quantity).coerceAtMost(product.stock.toInt().coerceAtLeast(1))
                current.toMutableList().apply {
                    this[existingIndex] = existing.copy(product = product, quantity = newQty)
                }
            } else {
                val safeQty = quantity.coerceIn(1, product.stock.toInt().coerceAtLeast(1))
                current + CartItem(product = product, quantity = safeQty)
            }
        }
        showFeedback("Added ${product.name} to cart")
    }

    fun updateCartQuantity(product: Product, newQuantity: Int) {
        _cartItems.update { current ->
            if (newQuantity <= 0) {
                current.filterNot { it.product.id == product.id && it.product.name == product.name }
            } else {
                val maxStock = product.stock.toInt().coerceAtLeast(1)
                current.map { item ->
                    if (item.product.id == product.id && item.product.name == product.name) {
                        item.copy(quantity = newQuantity.coerceAtMost(maxStock))
                    } else {
                        item
                    }
                }
            }
        }
    }

    fun removeFromCart(product: Product) {
        _cartItems.update { current ->
            current.filterNot { it.product.id == product.id && it.product.name == product.name }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun calculateDeliveryFee(subtotalPkr: Long, config: StoreConfig): Long {
        if (subtotalPkr <= 0L) return 0L
        return if (config.freeDeliveryThresholdPkr > 0L && subtotalPkr >= config.freeDeliveryThresholdPkr) {
            0L
        } else {
            config.deliveryFeePkr
        }
    }

    // Checkout & Profile Actions
    fun submitOrder(
        customerName: String,
        customerPhone: String,
        customerCity: String,
        customerAddress: String,
        orderNotes: String,
        paymentMethod: String,
        saveAddressToProfile: Boolean,
        onOrderCreated: (Order) -> Unit
    ) {
        val items = _cartItems.value
        if (items.isEmpty()) {
            showFeedback("Your cart is empty.")
            return
        }
        val config = storeConfig.value
        val subtotal = items.sumOf { it.lineTotalPkr }
        val deliveryFee = calculateDeliveryFee(subtotal, config)

        _isSubmittingOrder.value = true
        viewModelScope.launch {
            if (saveAddressToProfile) {
                repository.saveUserProfile(
                    fullName = customerName,
                    phone = customerPhone,
                    city = customerCity,
                    address = customerAddress
                )
            }

            // Check if products are already in Cloud Firestore; if using default catalog, don't pass non-existent IDs to batch.update
            val cloudProducts = (adminProductsState.value as? UiState.Success)?.data
                ?: (catalogState.value as? UiState.Success)?.data
                ?: emptyList()
            val normalizedItems = items.map { item ->
                val existsInCloud = cloudProducts.any { it.id == item.product.id && it.createdAt != null }
                if (existsInCloud) item else item.copy(product = item.product.copy(id = ""))
            }

            val result = repository.placeOrder(
                customerName = customerName,
                customerPhone = customerPhone,
                customerCity = customerCity,
                customerAddress = customerAddress,
                orderNotes = orderNotes,
                cartItems = normalizedItems,
                deliveryFeePkr = deliveryFee,
                paymentMethod = paymentMethod
            )
            _isSubmittingOrder.value = false
            result.fold(
                onSuccess = { order ->
                    _lastPlacedOrder.value = order
                    clearCart()
                    showFeedback("Order #${order.id.takeLast(6).uppercase()} placed!")
                    onOrderCreated(order)
                },
                onFailure = { error ->
                    showFeedback(error.localizedMessage ?: "Failed to place order. Please check your details.")
                }
            )
        }
    }

    fun saveCustomerProfile(
        fullName: String,
        phone: String,
        city: String,
        address: String
    ) {
        viewModelScope.launch {
            val res = repository.saveUserProfile(fullName, phone, city, address)
            res.fold(
                onSuccess = { showFeedback("Saved delivery profile") },
                onFailure = { showFeedback(it.localizedMessage ?: "Failed to save profile") }
            )
        }
    }

    fun cancelOrderByCustomer(orderId: String) {
        viewModelScope.launch {
            val res = repository.cancelCustomerOrder(orderId)
            res.fold(
                onSuccess = { showFeedback("Order cancelled") },
                onFailure = { showFeedback(it.localizedMessage ?: "Unable to cancel order") }
            )
        }
    }

    // Admin Actions
    fun seedDefaultProductsAsAdmin() {
        viewModelScope.launch {
            val res = repository.seedInitialCatalogAsAdmin()
            res.fold(
                onSuccess = { count -> showFeedback("Synced $count official ANZORA products to Cloud Firestore") },
                onFailure = { showFeedback(it.localizedMessage ?: "Admin authorization required to seed catalog") }
            )
        }
    }

    fun saveProductAsAdmin(product: Product, isNew: Boolean, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            val res = repository.saveProductAsAdmin(product, isNew)
            res.fold(
                onSuccess = {
                    showFeedback(if (isNew) "Product added to catalog" else "Product updated")
                    onDone()
                },
                onFailure = {
                    showFeedback(it.localizedMessage ?: "Failed to save product (Admin permission required)")
                }
            )
        }
    }

    fun deleteProductAsAdmin(productId: String) {
        viewModelScope.launch {
            val res = repository.deleteProductAsAdmin(productId)
            res.fold(
                onSuccess = { showFeedback("Product removed from catalog") },
                onFailure = { showFeedback(it.localizedMessage ?: "Failed to delete product") }
            )
        }
    }

    fun updateOrderStatusAsAdmin(orderId: String, newStatus: String) {
        viewModelScope.launch {
            val res = repository.updateOrderStatusAsAdmin(orderId, newStatus)
            res.fold(
                onSuccess = { showFeedback("Order status updated to $newStatus") },
                onFailure = { showFeedback(it.localizedMessage ?: "Failed to update order status") }
            )
        }
    }

    fun saveStoreConfigAsAdmin(config: StoreConfig) {
        viewModelScope.launch {
            val res = repository.saveStoreConfigAsAdmin(config)
            res.fold(
                onSuccess = { showFeedback("Store delivery fee & settings updated") },
                onFailure = { showFeedback(it.localizedMessage ?: "Failed to update store configuration") }
            )
        }
    }

    fun showFeedback(message: String) {
        _feedbackMessage.value = message
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }

    private companion object {
        const val TAG = "AnzoraViewModel"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}

package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AnzoraCatalogSeeder
import com.example.data.local.CartItemWithProduct
import com.example.data.local.CategoryEntity
import com.example.data.local.CustomerAccountEntity
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.data.local.StoreSettingsEntity
import com.example.data.repository.AnzoraRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PrimaryTab {
    SHOP,
    CATEGORIES,
    CART,
    ACCOUNT,
    INFO_ADMIN
}

enum class SortOption(val label: String) {
    FEATURED("Featured"),
    PRICE_LOW_TO_HIGH("Price: Low to High"),
    PRICE_HIGH_TO_LOW("Price: High to Low"),
    NEWEST("New Arrivals")
}

enum class LegalPolicyTopic(val title: String, val subtitle: String) {
    ABOUT_US("About ANZORA Pakistan", "Engineering premium audio, GaN power & creator gear for Pakistan"),
    CONTACT_US("Contact Us & Support", "Reach our Karachi & Lahore teams or chat on WhatsApp +92 371 2047636"),
    SHIPPING_POLICY("Shipping & Delivery Policy", "Nationwide Cash on Delivery timelines, couriers & tracking"),
    RETURNS_POLICY("7-Day Replacement & Warranty", "Hassle-free replacement for defective or damaged items"),
    PRIVACY_POLICY("Privacy & Data Protection", "How ANZORA protects your personal and delivery information"),
    TERMS_CONDITIONS("Terms & Conditions", "Terms of service for ANZORA Pakistan Cash on Delivery orders"),
    PLATFORM_ARCHITECTURE("Web, Backend & APK Guide", "Firebase rules, Capacitor/Web sync & Android build architecture")
}

data class FilterState(
    val searchQuery: String = "",
    val selectedCategoryId: String? = null,
    val inStockOnly: Boolean = false,
    val maxPricePkr: Long? = null,
    val sortOption: SortOption = SortOption.FEATURED
)

class AnzoraViewModel(
    private val repository: AnzoraRepository
) : ViewModel() {

    private val _currentTab = MutableStateFlow(PrimaryTab.SHOP)
    val currentTab: StateFlow<PrimaryTab> = _currentTab.asStateFlow()

    private val _selectedProductId = MutableStateFlow<String?>(null)
    val selectedProductId: StateFlow<String?> = _selectedProductId.asStateFlow()

    private val _isCheckoutOpen = MutableStateFlow(false)
    val isCheckoutOpen: StateFlow<Boolean> = _isCheckoutOpen.asStateFlow()

    private val _lastConfirmedOrder = MutableStateFlow<OrderEntity?>(null)
    val lastConfirmedOrder: StateFlow<OrderEntity?> = _lastConfirmedOrder.asStateFlow()

    private val _activePolicyTopic = MutableStateFlow<LegalPolicyTopic?>(null)
    val activePolicyTopic: StateFlow<LegalPolicyTopic?> = _activePolicyTopic.asStateFlow()

    private val _isAdminPortalOpen = MutableStateFlow(false)
    val isAdminPortalOpen: StateFlow<Boolean> = _isAdminPortalOpen.asStateFlow()

    private val _isAdminAuthenticated = MutableStateFlow(false)
    val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _guestLookupPhone = MutableStateFlow("")
    val guestLookupPhone: StateFlow<String> = _guestLookupPhone.asStateFlow()

    val categories: StateFlow<List<CategoryEntity>> = repository.categories.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnzoraCatalogSeeder.initialCategories
    )

    val allProducts: StateFlow<List<ProductEntity>> = repository.products.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnzoraCatalogSeeder.initialProducts
    )

    val filteredProducts: StateFlow<List<ProductEntity>> = combine(
        allProducts,
        _filterState
    ) { products, filter ->
        products.filter { product ->
            val matchesQuery = if (filter.searchQuery.isBlank()) {
                true
            } else {
                val q = filter.searchQuery.trim().lowercase()
                product.title.lowercase().contains(q) ||
                    product.subtitle.lowercase().contains(q) ||
                    product.categoryName.lowercase().contains(q) ||
                    product.sku.lowercase().contains(q) ||
                    product.description.lowercase().contains(q)
            }
            val matchesCategory = filter.selectedCategoryId == null || product.categoryId == filter.selectedCategoryId
            val matchesStock = !filter.inStockOnly || product.stockQuantity > 0
            val matchesPrice = filter.maxPricePkr == null || product.pricePkr <= filter.maxPricePkr
            matchesQuery && matchesCategory && matchesStock && matchesPrice
        }.let { list ->
            when (filter.sortOption) {
                SortOption.FEATURED -> list.sortedWith(
                    compareByDescending<ProductEntity> { it.isFeatured }.thenByDescending { it.updatedAt }
                )
                SortOption.PRICE_LOW_TO_HIGH -> list.sortedBy { it.pricePkr }
                SortOption.PRICE_HIGH_TO_LOW -> list.sortedByDescending { it.pricePkr }
                SortOption.NEWEST -> list.sortedWith(
                    compareByDescending<ProductEntity> { it.isNewArrival }.thenByDescending { it.updatedAt }
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnzoraCatalogSeeder.initialProducts
    )

    val cartItems: StateFlow<List<CartItemWithProduct>> = repository.cartItemsWithProducts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val storeSettings: StateFlow<StoreSettingsEntity> = repository.storeSettings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StoreSettingsEntity()
    )

    val loggedInCustomer: StateFlow<CustomerAccountEntity?> = repository.loggedInCustomer.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val customerOrders: StateFlow<List<OrderEntity>> = combine(
        loggedInCustomer,
        _guestLookupPhone
    ) { account, lookupPhone ->
        account?.id to (account?.phone?.ifBlank { lookupPhone } ?: lookupPhone)
    }.flatMapLatest { (accountId, phone) ->
        repository.observeCustomerOrders(accountId, phone)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allOrdersForAdmin: StateFlow<List<OrderEntity>> = repository.allOrders.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    // Navigation & Sub-Screen Handlers
    fun selectTab(tab: PrimaryTab) {
        _selectedProductId.value = null
        _isCheckoutOpen.value = false
        _lastConfirmedOrder.value = null
        _activePolicyTopic.value = null
        _isAdminPortalOpen.value = false
        _currentTab.value = tab
    }

    fun openProductDetail(productId: String) {
        _selectedProductId.value = productId
    }

    fun closeProductDetail() {
        _selectedProductId.value = null
    }

    fun openCategoryProducts(categoryId: String?) {
        _filterState.update { it.copy(selectedCategoryId = categoryId) }
        _selectedProductId.value = null
        _isCheckoutOpen.value = false
        _currentTab.value = PrimaryTab.SHOP
    }

    fun openCheckout() {
        _selectedProductId.value = null
        _isCheckoutOpen.value = true
    }

    fun closeCheckout() {
        _isCheckoutOpen.value = false
    }

    fun dismissOrderConfirmation() {
        _lastConfirmedOrder.value = null
        _isCheckoutOpen.value = false
        _currentTab.value = PrimaryTab.ACCOUNT
    }

    fun openPolicyTopic(topic: LegalPolicyTopic) {
        _activePolicyTopic.value = topic
    }

    fun closePolicyTopic() {
        _activePolicyTopic.value = null
    }

    fun openAdminPortal() {
        _activePolicyTopic.value = null
        _isAdminPortalOpen.value = true
    }

    fun closeAdminPortal() {
        _isAdminPortalOpen.value = false
    }

    // Search & Filters
    fun updateSearchQuery(query: String) {
        _filterState.update { it.copy(searchQuery = query) }
    }

    fun selectCategoryFilter(categoryId: String?) {
        _filterState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun toggleInStockOnly(inStock: Boolean) {
        _filterState.update { it.copy(inStockOnly = inStock) }
    }

    fun updateMaxPriceFilter(maxPrice: Long?) {
        _filterState.update { it.copy(maxPricePkr = maxPrice) }
    }

    fun updateSortOption(option: SortOption) {
        _filterState.update { it.copy(sortOption = option) }
    }

    fun resetFilters() {
        _filterState.value = FilterState()
    }

    // Cart Actions
    fun addToCart(productId: String, quantity: Int = 1) {
        viewModelScope.launch {
            val result = repository.addToCart(productId, quantity)
            result.fold(
                onSuccess = { newQty ->
                    showMessage("Added to cart ($newQty in cart)")
                },
                onFailure = { err ->
                    showMessage(err.message ?: "Unable to add item")
                }
            )
        }
    }

    fun setCartQuantity(productId: String, newQty: Int) {
        viewModelScope.launch {
            val result = repository.setCartQuantity(productId, newQty)
            result.onFailure { err ->
                showMessage(err.message ?: "Stock limit reached")
            }
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
            showMessage("Removed from cart")
        }
    }

    // Checkout Action
    fun submitOrder(
        customerName: String,
        customerPhone: String,
        customerCity: String,
        customerAddress: String,
        orderNotes: String,
        orderedViaWhatsApp: Boolean,
        onOrderCreated: (OrderEntity) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.placeOrder(
                customerName = customerName,
                customerPhone = customerPhone,
                customerCity = customerCity,
                customerAddress = customerAddress,
                orderNotes = orderNotes,
                orderedViaWhatsApp = orderedViaWhatsApp
            )
            result.fold(
                onSuccess = { order ->
                    _guestLookupPhone.value = order.customerPhone
                    _isCheckoutOpen.value = false
                    _lastConfirmedOrder.value = order
                    showMessage("Order ${order.orderNumber} placed successfully!")
                    onOrderCreated(order)
                },
                onFailure = { err ->
                    showMessage(err.message ?: "Order validation failed.")
                }
            )
        }
    }

    // Customer Account Actions
    fun setGuestLookupPhone(phone: String) {
        _guestLookupPhone.value = AnzoraRepository.normalizePakistaniPhone(phone)
    }

    fun registerCustomer(
        fullName: String,
        phone: String,
        email: String,
        city: String,
        completeAddress: String,
        passwordPlain: String
    ) {
        viewModelScope.launch {
            val res = repository.registerCustomerAccount(
                fullName = fullName,
                phone = phone,
                email = email,
                city = city,
                completeAddress = completeAddress,
                passwordPlain = passwordPlain
            )
            res.fold(
                onSuccess = { acc ->
                    _guestLookupPhone.value = acc.phone
                    showMessage("Welcome to ANZORA, ${acc.fullName}!")
                },
                onFailure = { err ->
                    showMessage(err.message ?: "Registration failed.")
                }
            )
        }
    }

    fun loginCustomer(emailOrPhone: String, passwordPlain: String) {
        viewModelScope.launch {
            val res = repository.loginCustomerAccount(emailOrPhone, passwordPlain)
            res.fold(
                onSuccess = { acc ->
                    _guestLookupPhone.value = acc.phone
                    showMessage("Signed in as ${acc.fullName}")
                },
                onFailure = { err ->
                    showMessage(err.message ?: "Sign in failed.")
                }
            )
        }
    }

    fun updateCustomerProfile(
        account: CustomerAccountEntity,
        fullName: String,
        phone: String,
        city: String,
        completeAddress: String
    ) {
        viewModelScope.launch {
            repository.updateCustomerProfile(account, fullName, phone, city, completeAddress)
            showMessage("Delivery profile saved.")
        }
    }

    fun logoutCustomer() {
        viewModelScope.launch {
            repository.logoutCustomer()
            showMessage("Signed out of customer account.")
        }
    }

    // Protected Admin Actions
    fun loginAdmin(email: String, passwordPlain: String) {
        viewModelScope.launch {
            val res = repository.authenticateAdmin(email, passwordPlain)
            res.fold(
                onSuccess = {
                    _isAdminAuthenticated.value = true
                    showMessage("Admin session unlocked.")
                },
                onFailure = { err ->
                    showMessage(err.message ?: "Invalid admin credentials.")
                }
            )
        }
    }

    fun logoutAdmin() {
        _isAdminAuthenticated.value = false
        showMessage("Admin session locked.")
    }

    fun adminSaveProduct(product: ProductEntity, onSuccess: () -> Unit = {}) {
        if (!_isAdminAuthenticated.value) {
            showMessage("Unauthorized: Admin session required.")
            return
        }
        viewModelScope.launch {
            val res = repository.adminSaveProduct(product)
            res.fold(
                onSuccess = {
                    showMessage("Product '${product.title}' saved.")
                    onSuccess()
                },
                onFailure = { err ->
                    showMessage(err.message ?: "Failed to save product.")
                }
            )
        }
    }

    fun adminQuickUpdateStockAndPrice(
        productId: String,
        pricePkr: Long,
        compareAtPricePkr: Long?,
        stockQuantity: Int
    ) {
        if (!_isAdminAuthenticated.value) {
            showMessage("Unauthorized: Admin session required.")
            return
        }
        viewModelScope.launch {
            val res = repository.adminUpdateStockAndPrice(productId, pricePkr, compareAtPricePkr, stockQuantity)
            res.fold(
                onSuccess = { showMessage("Inventory & price updated.") },
                onFailure = { err -> showMessage(err.message ?: "Update failed.") }
            )
        }
    }

    fun adminDeleteProduct(productId: String) {
        if (!_isAdminAuthenticated.value) return
        viewModelScope.launch {
            repository.adminDeleteProduct(productId)
            showMessage("Product deleted from catalog.")
        }
    }

    fun adminSaveCategory(category: CategoryEntity) {
        if (!_isAdminAuthenticated.value) return
        viewModelScope.launch {
            val res = repository.adminSaveCategory(category)
            res.fold(
                onSuccess = { showMessage("Category '${category.name}' saved.") },
                onFailure = { err -> showMessage(err.message ?: "Failed to save category.") }
            )
        }
    }

    fun adminUpdateOrderStatus(orderNumber: String, newStatus: String) {
        if (!_isAdminAuthenticated.value) return
        viewModelScope.launch {
            val res = repository.adminUpdateOrderStatus(orderNumber, newStatus)
            res.fold(
                onSuccess = { showMessage("Order $orderNumber marked as $newStatus.") },
                onFailure = { err -> showMessage(err.message ?: "Failed to update status.") }
            )
        }
    }

    fun adminUpdateSettings(
        standardDeliveryFeePkr: Long,
        freeDeliveryThresholdPkr: Long,
        enableFreeDeliveryThreshold: Boolean,
        promoHeadline: String,
        promoSubtext: String,
        newAdminPasswordPlain: String?
    ) {
        if (!_isAdminAuthenticated.value) return
        viewModelScope.launch {
            val res = repository.adminUpdateStoreSettings(
                standardDeliveryFeePkr = standardDeliveryFeePkr,
                freeDeliveryThresholdPkr = freeDeliveryThresholdPkr,
                enableFreeDeliveryThreshold = enableFreeDeliveryThreshold,
                promoHeadline = promoHeadline,
                promoSubtext = promoSubtext,
                newAdminPasswordPlain = newAdminPasswordPlain
            )
            res.fold(
                onSuccess = { showMessage("Store delivery & banner settings updated.") },
                onFailure = { err -> showMessage(err.message ?: "Could not save settings.") }
            )
        }
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    class Factory(private val repository: AnzoraRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AnzoraViewModel(repository) as T
        }
    }
}

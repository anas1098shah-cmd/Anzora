package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.repository.AnzoraRepository
import com.example.data.util.WhatsAppHelper
import com.example.ui.auth.AnzoraAuthScreen
import com.example.ui.auth.signOutAnzora
import com.example.ui.screens.AnzoraAdminDashboardScreen
import com.example.ui.screens.AnzoraCartScreen
import com.example.ui.screens.AnzoraCheckoutScreen
import com.example.ui.screens.AnzoraOrderSuccessDialog
import com.example.ui.screens.AnzoraOrdersAndAccountScreen
import com.example.ui.screens.AnzoraProductDetailScreen
import com.example.ui.screens.AnzoraStorefrontScreen
import com.example.ui.theme.AnzoraElectricBlue
import com.example.ui.theme.AnzoraNavyPrimary
import com.example.ui.theme.AnzoraTheme
import com.example.ui.theme.AnzoraWhatsAppGreen
import com.example.ui.viewmodel.AnzoraViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnzoraTheme(darkTheme = true) {
                AnzoraAppRoot()
            }
        }
    }
}

internal fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

enum class AnzoraNavDestination(val label: String, val icon: ImageVector, val testTag: String) {
    STOREFRONT("Shop", Icons.Default.Storefront, "nav_shop"),
    CART("Cart", Icons.Default.ShoppingCart, "nav_cart"),
    ACCOUNT("Account & Orders", Icons.Default.Person, "nav_account"),
    ADMIN("Admin", Icons.Default.AdminPanelSettings, "nav_admin")
}

@Composable
fun AnzoraAppRoot(auth: FirebaseAuth = Firebase.auth) {
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    val user = currentUser

    if (user == null) {
        AnzoraAuthScreen(
            onAuthSuccess = { /* AuthStateListener updates currentUser automatically */ }
        )
    } else {
        AnzoraAuthenticatedApp(
            currentUserId = user.uid,
            userEmail = user.email ?: "",
            userDisplayName = user.displayName ?: "ANZORA Customer"
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnzoraAuthenticatedApp(
    currentUserId: String,
    userEmail: String,
    userDisplayName: String
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    val viewModel: AnzoraViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                AnzoraViewModel(AnzoraRepository(db), currentUserId)
            }
        }
    )

    val filteredProductsState by viewModel.filteredProductsState.collectAsStateWithLifecycle()
    val storeConfig by viewModel.storeConfig.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedSort by viewModel.selectedSort.collectAsStateWithLifecycle()
    val inStockOnly by viewModel.inStockOnly.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val customerOrdersState by viewModel.customerOrdersState.collectAsStateWithLifecycle()
    val savedProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val adminProductsState by viewModel.adminProductsState.collectAsStateWithLifecycle()
    val adminOrdersState by viewModel.adminOrdersState.collectAsStateWithLifecycle()
    val isSubmittingOrder by viewModel.isSubmittingOrder.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.feedbackMessage.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(AnzoraNavDestination.STOREFRONT) }
    var selectedProductForDetail by remember { mutableStateOf<Product?>(null) }
    var isInCheckout by remember { mutableStateOf(false) }
    var completedOrderModal by remember { mutableStateOf<Order?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(feedbackMessage) {
        val msg = feedbackMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedback()
        }
    }

    fun openExternalUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            viewModel.showFeedback("Unable to open external application")
        }
    }

    val totalCartUnits = cartItems.sumOf { it.quantity }
    val cartSubtotal = cartItems.sumOf { it.lineTotalPkr }
    val deliveryFee = viewModel.calculateDeliveryFee(cartSubtotal, storeConfig)

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 680.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AnzoraElectricBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Headphones,
                                    contentDescription = "ANZORA Logo",
                                    tint = AnzoraNavyPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "ANZORA",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "PAKISTAN • ELECTRONICS ACCESSORIES",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AnzoraElectricBlue
                                )
                            }
                        }
                    },
                    actions = {
                        // Direct WhatsApp Customer Support Shortcut
                        IconButton(
                            onClick = {
                                val url = WhatsAppHelper.buildWhatsAppUrl(
                                    storeConfig.whatsappNumber,
                                    "Assalam-o-Alaikum ANZORA! I need help choosing an accessory."
                                )
                                openExternalUrl(url)
                            },
                            modifier = Modifier.testTag("topbar_whatsapp_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SupportAgent,
                                contentDescription = "WhatsApp Support",
                                tint = AnzoraWhatsAppGreen
                            )
                        }

                        // Cart Shortcut with Badge
                        IconButton(
                            onClick = {
                                selectedProductForDetail = null
                                isInCheckout = false
                                currentTab = AnzoraNavDestination.CART
                            },
                            modifier = Modifier.testTag("topbar_cart_button")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (totalCartUnits > 0) {
                                        Badge(
                                            containerColor = AnzoraElectricBlue,
                                            contentColor = AnzoraNavyPrimary
                                        ) {
                                            Text(totalCartUnits.toString())
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Shopping Cart"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            bottomBar = {
                if (!isWideScreen) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        AnzoraNavDestination.entries.forEach { dest ->
                            val selected = currentTab == dest && selectedProductForDetail == null
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    selectedProductForDetail = null
                                    isInCheckout = false
                                    currentTab = dest
                                },
                                icon = {
                                    if (dest == AnzoraNavDestination.CART && totalCartUnits > 0) {
                                        BadgedBox(
                                            badge = {
                                                Badge(
                                                    containerColor = AnzoraElectricBlue,
                                                    contentColor = AnzoraNavyPrimary
                                                ) {
                                                    Text(totalCartUnits.toString())
                                                }
                                            }
                                        ) {
                                            Icon(dest.icon, contentDescription = dest.label)
                                        }
                                    } else {
                                        Icon(dest.icon, contentDescription = dest.label)
                                    }
                                },
                                label = { Text(dest.label) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = AnzoraNavyPrimary,
                                    selectedTextColor = AnzoraElectricBlue,
                                    indicatorColor = AnzoraElectricBlue
                                ),
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isWideScreen) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        AnzoraNavDestination.entries.forEach { dest ->
                            val selected = currentTab == dest && selectedProductForDetail == null
                            NavigationRailItem(
                                selected = selected,
                                onClick = {
                                    selectedProductForDetail = null
                                    isInCheckout = false
                                    currentTab = dest
                                },
                                icon = {
                                    if (dest == AnzoraNavDestination.CART && totalCartUnits > 0) {
                                        BadgedBox(
                                            badge = {
                                                Badge(
                                                    containerColor = AnzoraElectricBlue,
                                                    contentColor = AnzoraNavyPrimary
                                                ) {
                                                    Text(totalCartUnits.toString())
                                                }
                                            }
                                        ) {
                                            Icon(dest.icon, contentDescription = dest.label)
                                        }
                                    } else {
                                        Icon(dest.icon, contentDescription = dest.label)
                                    }
                                },
                                label = { Text(dest.label) },
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                    when {
                        selectedProductForDetail != null -> {
                            AnzoraProductDetailScreen(
                                product = selectedProductForDetail!!,
                                storeConfig = storeConfig,
                                onBack = { selectedProductForDetail = null },
                                onAddToCart = { prod, qty ->
                                    viewModel.addToCart(prod, qty)
                                    selectedProductForDetail = null
                                },
                                onBuyNowWhatsApp = { url -> openExternalUrl(url) }
                            )
                        }

                        currentTab == AnzoraNavDestination.CART && isInCheckout -> {
                            AnzoraCheckoutScreen(
                                cartItems = cartItems,
                                storeConfig = storeConfig,
                                deliveryFeePkr = deliveryFee,
                                savedProfile = savedProfile,
                                isSubmitting = isSubmittingOrder,
                                onBackToCart = { isInCheckout = false },
                                onPlaceOrder = { name, phone, city, address, notes, method, saveProfile, launchWhatsApp ->
                                    viewModel.submitOrder(
                                        customerName = name,
                                        customerPhone = phone,
                                        customerCity = city,
                                        customerAddress = address,
                                        orderNotes = notes,
                                        paymentMethod = method,
                                        saveAddressToProfile = saveProfile,
                                        onOrderCreated = { createdOrder ->
                                            isInCheckout = false
                                            completedOrderModal = createdOrder
                                            if (launchWhatsApp) {
                                                val msg = WhatsAppHelper.buildWhatsAppOrderMessageFromOrder(createdOrder)
                                                val waUrl = WhatsAppHelper.buildWhatsAppUrl(storeConfig.whatsappNumber, msg)
                                                openExternalUrl(waUrl)
                                            }
                                        }
                                    )
                                }
                            )
                        }

                        currentTab == AnzoraNavDestination.STOREFRONT -> {
                            AnzoraStorefrontScreen(
                                productsState = filteredProductsState,
                                storeConfig = storeConfig,
                                searchQuery = searchQuery,
                                selectedCategory = selectedCategory,
                                selectedSort = selectedSort,
                                inStockOnly = inStockOnly,
                                onSearchQueryChange = viewModel::updateSearchQuery,
                                onCategorySelect = viewModel::selectCategory,
                                onSortSelect = viewModel::selectSort,
                                onToggleInStockOnly = viewModel::toggleInStockOnly,
                                onClearFilters = viewModel::clearFilters,
                                onProductClick = { prod -> selectedProductForDetail = prod },
                                onAddToCart = { prod -> viewModel.addToCart(prod, 1) },
                                onOpenWhatsAppDirect = { url -> openExternalUrl(url) }
                            )
                        }

                        currentTab == AnzoraNavDestination.CART -> {
                            AnzoraCartScreen(
                                cartItems = cartItems,
                                storeConfig = storeConfig,
                                deliveryFeePkr = deliveryFee,
                                onUpdateQuantity = viewModel::updateCartQuantity,
                                onRemoveItem = viewModel::removeFromCart,
                                onContinueShopping = { currentTab = AnzoraNavDestination.STOREFRONT },
                                onProceedToCheckout = { isInCheckout = true }
                            )
                        }

                        currentTab == AnzoraNavDestination.ACCOUNT -> {
                            AnzoraOrdersAndAccountScreen(
                                userEmail = userEmail,
                                userDisplayName = userDisplayName,
                                isAdmin = viewModel.isAdmin,
                                ordersState = customerOrdersState,
                                savedProfile = savedProfile,
                                storeConfig = storeConfig,
                                onSaveProfile = viewModel::saveCustomerProfile,
                                onCancelOrder = viewModel::cancelOrderByCustomer,
                                onResendWhatsApp = { url -> openExternalUrl(url) },
                                onSignOut = {
                                    signOutAnzora(
                                        credentialManager = credentialManager,
                                        onSignOutComplete = {},
                                        scope = coroutineScope
                                    )
                                }
                            )
                        }

                        currentTab == AnzoraNavDestination.ADMIN -> {
                            AnzoraAdminDashboardScreen(
                                isAdmin = viewModel.isAdmin,
                                userEmail = userEmail,
                                adminProductsState = adminProductsState,
                                adminOrdersState = adminOrdersState,
                                storeConfig = storeConfig,
                                onSeedDefaultCatalog = viewModel::seedDefaultProductsAsAdmin,
                                onSaveProduct = { prod, isNew ->
                                    viewModel.saveProductAsAdmin(prod, isNew)
                                },
                                onDeleteProduct = viewModel::deleteProductAsAdmin,
                                onUpdateOrderStatus = viewModel::updateOrderStatusAsAdmin,
                                onSaveStoreConfig = viewModel::saveStoreConfigAsAdmin,
                                onOpenWhatsApp = { url -> openExternalUrl(url) }
                            )
                        }
                    }
                }
            }
        }

        if (completedOrderModal != null) {
            Dialog(onDismissRequest = { completedOrderModal = null }) {
                AnzoraOrderSuccessDialog(
                    order = completedOrderModal!!,
                    whatsappNumber = storeConfig.whatsappNumber,
                    onDismissAndViewOrders = {
                        completedOrderModal = null
                        currentTab = AnzoraNavDestination.ACCOUNT
                    },
                    onSendWhatsApp = { url ->
                        openExternalUrl(url)
                    }
                )
            }
        }
    }
}

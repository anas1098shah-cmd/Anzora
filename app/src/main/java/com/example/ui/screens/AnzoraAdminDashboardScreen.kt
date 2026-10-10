package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.AnzoraCategories
import com.example.data.model.Order
import com.example.data.model.OrderStatuses
import com.example.data.model.Product
import com.example.data.model.StoreConfig
import com.example.data.util.WhatsAppHelper
import com.example.ui.components.ProductVisualBanner
import com.example.ui.theme.AnzoraElectricBlue
import com.example.ui.theme.AnzoraError
import com.example.ui.theme.AnzoraNavyPrimary
import com.example.ui.theme.AnzoraSuccess
import com.example.ui.theme.AnzoraWarning
import com.example.ui.theme.AnzoraWhatsAppGreen
import com.example.ui.viewmodel.UiState
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun AnzoraAdminDashboardScreen(
    isAdmin: Boolean,
    userEmail: String,
    adminProductsState: UiState<List<Product>>,
    adminOrdersState: UiState<List<Order>>,
    storeConfig: StoreConfig,
    onSeedDefaultCatalog: () -> Unit,
    onSaveProduct: (Product, Boolean) -> Unit,
    onDeleteProduct: (String) -> Unit,
    onUpdateOrderStatus: (String, String) -> Unit,
    onSaveStoreConfig: (StoreConfig) -> Unit,
    onOpenWhatsApp: (String) -> Unit
) {
    if (!isAdmin) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Restricted Access",
                        tint = AnzoraError,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Administrator Access Required",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Signed in as: $userEmail\n\nAdministrative functions are protected on the backend via Cloud Firestore security rules (isAdmin() verified email & /admins/{uid} collection). Client-only password checks are never used.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        return
    }

    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Inventory & Products, 1 = Orders Management, 2 = Delivery & Store Settings
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var isCreatingNewProduct by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Admin Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = "Admin Dashboard",
                    tint = AnzoraElectricBlue,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "ANZORA Admin Portal",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Verified Admin: $userEmail",
                        style = MaterialTheme.typography.labelSmall,
                        color = AnzoraSuccess
                    )
                }
            }

            if (selectedTab == 0) {
                Button(
                    onClick = {
                        isCreatingNewProduct = true
                        editingProduct = Product(
                            id = "",
                            name = "",
                            category = AnzoraCategories.LIST.first(),
                            pricePkr = 2500L,
                            originalPricePkr = 3200L,
                            stock = 20L,
                            sku = "ANZ-NEW-01",
                            description = "",
                            specs = "Warranty: 7-Day Checking | Genuine ANZORA Accessory",
                            imageUrls = listOf("preset:earbuds"),
                            featured = false,
                            active = true
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AnzoraElectricBlue,
                        contentColor = AnzoraNavyPrimary
                    ),
                    modifier = Modifier.testTag("admin_add_product_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Product", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Admin Section Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                label = { Text("Products & Inventory") },
                leadingIcon = { Icon(Icons.Default.Inventory, contentDescription = null, modifier = Modifier.size(16.dp)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AnzoraElectricBlue,
                    selectedLabelColor = AnzoraNavyPrimary,
                    selectedLeadingIconColor = AnzoraNavyPrimary
                )
            )
            FilterChip(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                label = { Text("Customer Orders") },
                leadingIcon = { Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AnzoraElectricBlue,
                    selectedLabelColor = AnzoraNavyPrimary,
                    selectedLeadingIconColor = AnzoraNavyPrimary
                )
            )
            FilterChip(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                label = { Text("Delivery Fee & Settings") },
                leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AnzoraElectricBlue,
                    selectedLabelColor = AnzoraNavyPrimary,
                    selectedLeadingIconColor = AnzoraNavyPrimary
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            0 -> AdminProductsTab(
                productsState = adminProductsState,
                onSeedDefaultCatalog = onSeedDefaultCatalog,
                onEditProduct = { product ->
                    isCreatingNewProduct = false
                    editingProduct = product
                },
                onDeleteProduct = onDeleteProduct
            )
            1 -> AdminOrdersTab(
                ordersState = adminOrdersState,
                onUpdateOrderStatus = onUpdateOrderStatus,
                onOpenWhatsApp = onOpenWhatsApp
            )
            2 -> AdminStoreSettingsTab(
                storeConfig = storeConfig,
                onSaveStoreConfig = onSaveStoreConfig
            )
        }
    }

    if (editingProduct != null) {
        AdminProductEditorDialog(
            initialProduct = editingProduct!!,
            isNew = isCreatingNewProduct,
            onDismiss = { editingProduct = null },
            onSave = { updated ->
                onSaveProduct(updated, isCreatingNewProduct)
                editingProduct = null
            }
        )
    }
}

@Composable
private fun AdminProductsTab(
    productsState: UiState<List<Product>>,
    onSeedDefaultCatalog: () -> Unit,
    onEditProduct: (Product) -> Unit,
    onDeleteProduct: (String) -> Unit
) {
    when (productsState) {
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AnzoraElectricBlue)
            }
        }
        is UiState.Error -> {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = productsState.message,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        is UiState.Success -> {
            val products = productsState.data
            Column(modifier = Modifier.fillMaxSize()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Cloud Firestore Catalog (${products.size} products)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Sync or restore the 8 default ANZORA category products to Cloud Firestore.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = onSeedDefaultCatalog,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("admin_seed_catalog_button")
                        ) {
                            Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sync 8 Defaults")
                        }
                    }
                }

                if (products.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No cloud products written yet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tap 'Sync 8 Defaults' above to populate your live Cloud Firestore database with the initial 8 ANZORA products so you can edit prices, stock, and images.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(products, key = { it.id }) { prod ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ProductVisualBanner(
                                        imageKeyOrUrl = prod.imageUrls.firstOrNull() ?: "preset:earbuds",
                                        category = prod.category,
                                        contentDescription = prod.name,
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = prod.name,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${prod.category} • SKU: ${prod.sku}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AnzoraElectricBlue
                                        )
                                        Text(
                                            text = "${WhatsAppHelper.formatPkr(prod.pricePkr)} • Stock: ${prod.stock} • ${if (prod.active) "Active" else "Hidden"}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    IconButton(onClick = { onEditProduct(prod) }) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit product",
                                            tint = AnzoraElectricBlue
                                        )
                                    }
                                    IconButton(onClick = { onDeleteProduct(prod.id) }) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Delete product",
                                            tint = AnzoraError
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminOrdersTab(
    ordersState: UiState<List<Order>>,
    onUpdateOrderStatus: (String, String) -> Unit,
    onOpenWhatsApp: (String) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US) }

    when (ordersState) {
        is UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AnzoraElectricBlue)
            }
        }
        is UiState.Error -> {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = ordersState.message,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        is UiState.Success -> {
            val orders = ordersState.data
            if (orders.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No Customer Orders Yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "All incoming Cash on Delivery and WhatsApp orders will appear here in real time.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(orders, key = { it.id }) { order ->
                        var statusDropdownExpanded by remember { mutableStateOf(false) }
                        val statusColor = when (order.status) {
                            "Delivered" -> AnzoraSuccess
                            "Cancelled" -> AnzoraError
                            "Shipped", "Confirmed", "Packed" -> AnzoraElectricBlue
                            else -> AnzoraWarning
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Order #${order.id.takeLast(6).uppercase()} • ${WhatsAppHelper.formatPkr(order.totalPkr)}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = AnzoraElectricBlue
                                        )
                                        val dateStr = order.createdAt?.toDate()?.let { dateFormat.format(it) } ?: "Recent"
                                        Text(
                                            text = "$dateStr • ${order.paymentMethod}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Box {
                                        OutlinedButton(
                                            onClick = { statusDropdownExpanded = true },
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "Status: ${order.status}",
                                                color = statusColor,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }
                                        DropdownMenu(
                                            expanded = statusDropdownExpanded,
                                            onDismissRequest = { statusDropdownExpanded = false }
                                        ) {
                                            OrderStatuses.LIST.forEach { st ->
                                                DropdownMenuItem(
                                                    text = { Text(st) },
                                                    onClick = {
                                                        statusDropdownExpanded = false
                                                        onUpdateOrderStatus(order.id, st)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                                Text(
                                    text = "Customer: ${order.customerName} | Phone: ${order.customerPhone}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Address: ${order.customerAddress}, ${order.customerCity}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                if (order.orderNotes.isNotBlank()) {
                                    Text(
                                        text = "Notes: ${order.orderNotes}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AnzoraWarning
                                    )
                                }

                                Text(
                                    text = order.itemsSummary,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            val customerMsg = "Assalam-o-Alaikum ${order.customerName}! Your ANZORA order #${order.id.takeLast(6).uppercase()} (${WhatsAppHelper.formatPkr(order.totalPkr)} COD) status is currently: *${order.status}*."
                                            onOpenWhatsApp(
                                                WhatsAppHelper.buildWhatsAppUrl(order.customerPhone, customerMsg)
                                            )
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Message Customer on WhatsApp",
                                            tint = AnzoraWhatsAppGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("WhatsApp Customer")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminStoreSettingsTab(
    storeConfig: StoreConfig,
    onSaveStoreConfig: (StoreConfig) -> Unit
) {
    var deliveryFeeText by remember { mutableStateOf(storeConfig.deliveryFeePkr.toString()) }
    var freeThresholdText by remember { mutableStateOf(storeConfig.freeDeliveryThresholdPkr.toString()) }
    var whatsappText by remember { mutableStateOf(storeConfig.whatsappNumber) }
    var bannerText by remember { mutableStateOf(storeConfig.announcementBanner) }
    var supportEmailText by remember { mutableStateOf(storeConfig.supportEmail) }

    LaunchedEffect(storeConfig) {
        deliveryFeeText = storeConfig.deliveryFeePkr.toString()
        freeThresholdText = storeConfig.freeDeliveryThresholdPkr.toString()
        whatsappText = storeConfig.whatsappNumber
        bannerText = storeConfig.announcementBanner
        supportEmailText = storeConfig.supportEmail
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = AnzoraElectricBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Configurable Delivery Charges & Store Settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedTextField(
                value = deliveryFeeText,
                onValueChange = { deliveryFeeText = it.filter { ch -> ch.isDigit() } },
                label = { Text("Standard Delivery Fee (PKR)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_delivery_fee_input")
            )

            OutlinedTextField(
                value = freeThresholdText,
                onValueChange = { freeThresholdText = it.filter { ch -> ch.isDigit() } },
                label = { Text("Free Delivery Order Subtotal Threshold (PKR)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = whatsappText,
                onValueChange = { whatsappText = it },
                label = { Text("Official ANZORA WhatsApp Number") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = bannerText,
                onValueChange = { bannerText = it },
                label = { Text("Top Promotional Announcement Banner") },
                minLines = 2,
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = supportEmailText,
                onValueChange = { supportEmailText = it },
                label = { Text("Support Email Address") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val fee = deliveryFeeText.toLongOrNull() ?: 250L
                    val threshold = freeThresholdText.toLongOrNull() ?: 5000L
                    onSaveStoreConfig(
                        StoreConfig(
                            id = "global",
                            deliveryFeePkr = fee,
                            freeDeliveryThresholdPkr = threshold,
                            whatsappNumber = whatsappText.ifBlank { "+923712047636" },
                            announcementBanner = bannerText,
                            supportEmail = supportEmailText
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("admin_save_config_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AnzoraElectricBlue,
                    contentColor = AnzoraNavyPrimary
                )
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Delivery Fee & Store Configuration", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AdminProductEditorDialog(
    initialProduct: Product,
    isNew: Boolean,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit
) {
    var name by remember { mutableStateOf(initialProduct.name) }
    var category by remember { mutableStateOf(initialProduct.category) }
    var priceText by remember { mutableStateOf(initialProduct.pricePkr.toString()) }
    var originalPriceText by remember { mutableStateOf(initialProduct.originalPricePkr.toString()) }
    var stockText by remember { mutableStateOf(initialProduct.stock.toString()) }
    var sku by remember { mutableStateOf(initialProduct.sku) }
    var description by remember { mutableStateOf(initialProduct.description) }
    var specs by remember { mutableStateOf(initialProduct.specs) }
    var imageUrlText by remember { mutableStateOf(initialProduct.imageUrls.joinToString(", ")) }
    var featured by remember { mutableStateOf(initialProduct.featured) }
    var active by remember { mutableStateOf(initialProduct.active) }
    var categoryExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isNew) "Add New ANZORA Product" else "Edit Product (${initialProduct.sku})",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category Selector
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { categoryExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Category: $category")
                    }
                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        AnzoraCategories.LIST.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Sale Price (PKR)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = originalPriceText,
                        onValueChange = { originalPriceText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Original PKR") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = stockText,
                        onValueChange = { stockText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Stock Units") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = sku,
                        onValueChange = { sku = it },
                        label = { Text("SKU Code") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = imageUrlText,
                    onValueChange = { imageUrlText = it },
                    label = { Text("Image URLs or Presets (comma-separated)") },
                    placeholder = { Text("https://... or preset:earbuds, preset:power") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Product Description") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = specs,
                    onValueChange = { specs = it },
                    label = { Text("Technical Specs (separated by | )") },
                    minLines = 2,
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = featured, onCheckedChange = { featured = it })
                    Text("Featured", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.width(12.dp))
                    Checkbox(checked = active, onCheckedChange = { active = it })
                    Text("Active in Storefront", style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = (priceText.toLongOrNull() ?: 1000L).coerceAtLeast(50L)
                    val orig = (originalPriceText.toLongOrNull() ?: price).coerceAtLeast(price)
                    val stock = (stockText.toLongOrNull() ?: 0L).coerceAtLeast(0L)
                    val parsedImages = imageUrlText.split(",")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                        .ifEmpty { listOf("preset:earbuds") }
                    onSave(
                        initialProduct.copy(
                            name = name.ifBlank { "ANZORA Accessory" },
                            category = category,
                            pricePkr = price,
                            originalPricePkr = orig,
                            stock = stock,
                            sku = sku.ifBlank { "ANZ-001" },
                            description = description.ifBlank { "Genuine ANZORA electronics accessory." },
                            specs = specs.ifBlank { "Standard ANZORA specifications" },
                            imageUrls = parsedImages,
                            featured = featured,
                            active = active
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AnzoraElectricBlue,
                    contentColor = AnzoraNavyPrimary
                )
            ) {
                Text("Save Product", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

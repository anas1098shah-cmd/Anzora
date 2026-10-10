package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.data.local.CategoryEntity
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.data.local.StoreSettingsEntity
import com.example.data.repository.AnzoraRepository
import com.example.ui.components.AnzoraProductVisual
import com.example.ui.components.launchExternalUrl
import com.example.ui.theme.AnzoraElectricBlue
import com.example.ui.theme.AnzoraElectricBlueDark
import com.example.ui.theme.AnzoraError
import com.example.ui.theme.AnzoraNavyDeep
import com.example.ui.theme.AnzoraNavySurface
import com.example.ui.theme.AnzoraSuccess
import com.example.ui.theme.AnzoraWarning
import com.example.ui.theme.AnzoraWhite
import com.example.ui.theme.WhatsAppGreen

@Composable
fun AdminDashboardScreen(
    isAdminAuthenticated: Boolean,
    categories: List<CategoryEntity>,
    products: List<ProductEntity>,
    allOrders: List<OrderEntity>,
    storeSettings: StoreSettingsEntity,
    onBack: () -> Unit,
    onLoginAdmin: (email: String, passwordPlain: String) -> Unit,
    onLogoutAdmin: () -> Unit,
    onSaveProduct: (ProductEntity) -> Unit,
    onQuickUpdateStockAndPrice: (productId: String, pricePkr: Long, compareAtPricePkr: Long?, stockQuantity: Int) -> Unit,
    onDeleteProduct: (String) -> Unit,
    onSaveCategory: (CategoryEntity) -> Unit,
    onUpdateOrderStatus: (orderNumber: String, newStatus: String) -> Unit,
    onUpdateSettings: (
        standardDeliveryFeePkr: Long,
        freeDeliveryThresholdPkr: Long,
        enableFreeDeliveryThreshold: Boolean,
        promoHeadline: String,
        promoSubtext: String,
        newAdminPasswordPlain: String?
    ) -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    if (!isAdminAuthenticated) {
        AdminLoginGate(
            defaultAdminEmail = storeSettings.adminEmail,
            onBack = onBack,
            onLoginAdmin = onLoginAdmin,
            modifier = modifier
        )
        return
    }

    var selectedAdminSection by remember { mutableIntStateOf(0) } // 0=Products, 1=Orders, 2=Categories, 3=Delivery & Settings
    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var isCreatingNewProduct by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_dashboard_screen")
    ) {
        // Protected Admin Header Bar
        Surface(
            color = AnzoraNavyDeep,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Exit Admin",
                                tint = AnzoraWhite
                            )
                        }
                        Column {
                            Text(
                                text = "ANZORA Admin Console",
                                style = MaterialTheme.typography.titleMedium,
                                color = AnzoraWhite,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Authenticated Session • ${storeSettings.adminEmail}",
                                style = MaterialTheme.typography.bodySmall,
                                color = AnzoraElectricBlue
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onLogoutAdmin,
                        modifier = Modifier.testTag("admin_logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            tint = AnzoraElectricBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Lock", color = AnzoraWhite)
                    }
                }

                // KPI Summary Strip
                val totalStockUnits = products.sumOf { it.stockQuantity }
                val pendingOrdersCount = allOrders.count { it.status == "Pending" }
                val totalRevenuePkr = allOrders.filter { it.status != "Cancelled" }.sumOf { it.totalPkr }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminKpiPill(
                        label = "Catalog Products",
                        value = "${products.size} (${totalStockUnits} units)",
                        modifier = Modifier.weight(1f)
                    )
                    AdminKpiPill(
                        label = "Orders (Pending)",
                        value = "${allOrders.size} ($pendingOrdersCount new)",
                        modifier = Modifier.weight(1f)
                    )
                    AdminKpiPill(
                        label = "Gross COD Volume",
                        value = AnzoraRepository.formatPkr(totalRevenuePkr),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Admin Sub-Navigation Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val tabs = listOf(
                        Triple(0, "Products & Stock", Icons.Filled.Inventory2),
                        Triple(1, "Orders (${allOrders.size})", Icons.Filled.ReceiptLong),
                        Triple(2, "Categories (${categories.size})", Icons.Filled.Category),
                        Triple(3, "Delivery & Security", Icons.Filled.Settings)
                    )
                    items(tabs) { (idx, title, icon) ->
                        FilterChip(
                            selected = selectedAdminSection == idx,
                            onClick = {
                                editingProduct = null
                                isCreatingNewProduct = false
                                selectedAdminSection = idx
                            },
                            label = {
                                Text(
                                    text = title,
                                    color = if (selectedAdminSection == idx) AnzoraNavyDeep else AnzoraWhite
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (selectedAdminSection == idx) AnzoraNavyDeep else AnzoraElectricBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            modifier = Modifier.testTag("admin_tab_$idx")
                        )
                    }
                }
            }
        }

        // Main Section Body
        when (selectedAdminSection) {
            0 -> {
                if (editingProduct != null || isCreatingNewProduct) {
                    AdminProductEditor(
                        initialProduct = editingProduct,
                        categories = categories,
                        onCancel = {
                            editingProduct = null
                            isCreatingNewProduct = false
                        },
                        onSave = { saved ->
                            onSaveProduct(saved)
                            editingProduct = null
                            isCreatingNewProduct = false
                        }
                    )
                } else {
                    AdminProductsListSection(
                        products = products,
                        onAddNewProduct = {
                            val defaultCat = categories.firstOrNull()
                            editingProduct = ProductEntity(
                                id = "prod_custom_${System.currentTimeMillis()}",
                                title = "",
                                subtitle = "",
                                categoryId = defaultCat?.id ?: "cat_earbuds",
                                categoryName = defaultCat?.name ?: "Wireless earbuds",
                                pricePkr = 2999L,
                                compareAtPricePkr = 3999L,
                                stockQuantity = 25,
                                sku = "ANZ-NEW-${(System.currentTimeMillis() % 900) + 100}",
                                description = "",
                                specsPipeSeparated = "Brand: ANZORA Pakistan|Warranty: 1 Year Replacement Warranty",
                                primaryImageResName = "img_cat_audio",
                                customImageUri = null,
                                galleryPipeSeparated = "Studio Front View|Side Profile View|Box Contents View",
                                isFeatured = true,
                                isNewArrival = true
                            )
                            isCreatingNewProduct = true
                        },
                        onEditProduct = { prod ->
                            editingProduct = prod
                            isCreatingNewProduct = false
                        },
                        onQuickUpdate = onQuickUpdateStockAndPrice,
                        onDeleteProduct = onDeleteProduct
                    )
                }
            }
            1 -> AdminOrdersManagementSection(
                orders = allOrders,
                onUpdateStatus = onUpdateOrderStatus,
                onShowMessage = onShowMessage
            )
            2 -> AdminCategoriesSection(
                categories = categories,
                onSaveCategory = onSaveCategory
            )
            3 -> AdminSettingsSection(
                storeSettings = storeSettings,
                onUpdateSettings = onUpdateSettings
            )
        }
    }
}

@Composable
private fun AdminKpiPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = AnzoraNavySurface,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = AnzoraWhite.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                color = AnzoraElectricBlue,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun AdminLoginGate(
    defaultAdminEmail: String,
    onBack: () -> Unit,
    onLoginAdmin: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var emailInput by remember { mutableStateOf(defaultAdminEmail) }
    var passwordInput by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
            .testTag("admin_login_gate"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AdminPanelSettings,
                        contentDescription = null,
                        tint = AnzoraElectricBlueDark,
                        modifier = Modifier.size(34.dp)
                    )
                    Column {
                        Text(
                            text = "Protected Admin Portal",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Repository-verified SHA-256 + Role Claim Authentication",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Default Administrator Credentials (Configurable after Sign-In):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "• Admin Email: admin@anzora.pk\n• Admin Password: Anzora@2026",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Note: In Cloud Firestore deployment, administrative writes are also strictly enforced via custom token claim (request.auth.token.admin == true) in firestore.rules.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text("Administrator Email") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_email_input")
                )

                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    label = { Text("Administrator Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_password_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { onLoginAdmin(emailInput, passwordInput) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_unlock_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AnzoraNavyDeep,
                            contentColor = AnzoraWhite
                        )
                    ) {
                        Icon(Icons.Filled.Security, contentDescription = null, tint = AnzoraElectricBlue)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Unlock Admin")
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminProductsListSection(
    products: List<ProductEntity>,
    onAddNewProduct: () -> Unit,
    onEditProduct: (ProductEntity) -> Unit,
    onQuickUpdate: (String, Long, Long?, Int) -> Unit,
    onDeleteProduct: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Button(
                onClick = onAddNewProduct,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("admin_add_new_product_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AnzoraElectricBlue,
                    contentColor = AnzoraNavyDeep
                )
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add New Product to ANZORA Catalog", fontWeight = FontWeight.ExtraBold)
            }
        }

        items(products, key = { it.id }) { product ->
            var priceText by remember(product.id, product.pricePkr) {
                mutableStateOf(product.pricePkr.toString())
            }
            var stockText by remember(product.id, product.stockQuantity) {
                mutableStateOf(product.stockQuantity.toString())
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AnzoraNavyDeep)
                        ) {
                            AnzoraProductVisual(
                                product = product,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${product.categoryName} • SKU: ${product.sku}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (!product.customImageUri.isNullOrBlank()) {
                                Text(
                                    text = "Custom Photo Active",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = AnzoraSuccess
                                )
                            }
                        }

                        IconButton(onClick = { onEditProduct(product) }) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit full product & upload image",
                                tint = AnzoraElectricBlueDark
                            )
                        }
                        IconButton(onClick = { onDeleteProduct(product.id) }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete product",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { priceText = it },
                            label = { Text("Price (PKR)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = stockText,
                            onValueChange = { stockText = it },
                            label = { Text("Stock Qty") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                val parsedPrice = priceText.toLongOrNull() ?: product.pricePkr
                                val parsedStock = stockText.toIntOrNull() ?: product.stockQuantity
                                onQuickUpdate(product.id, parsedPrice, product.compareAtPricePkr, parsedStock)
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AnzoraNavyDeep,
                                contentColor = AnzoraWhite
                            ),
                            modifier = Modifier.height(54.dp)
                        ) {
                            Icon(Icons.Filled.Save, contentDescription = "Update", tint = AnzoraElectricBlue)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminProductEditor(
    initialProduct: ProductEntity?,
    categories: List<CategoryEntity>,
    onCancel: () -> Unit,
    onSave: (ProductEntity) -> Unit
) {
    val context = LocalContext.current
    val base = initialProduct ?: return

    var title by remember(base) { mutableStateOf(base.title) }
    var subtitle by remember(base) { mutableStateOf(base.subtitle) }
    var selectedCategoryId by remember(base) { mutableStateOf(base.categoryId) }
    var pricePkr by remember(base) { mutableStateOf(base.pricePkr.toString()) }
    var compareAtPkr by remember(base) { mutableStateOf(base.compareAtPricePkr?.toString() ?: "") }
    var stockQty by remember(base) { mutableStateOf(base.stockQuantity.toString()) }
    var sku by remember(base) { mutableStateOf(base.sku) }
    var description by remember(base) { mutableStateOf(base.description) }
    var specsPipe by remember(base) { mutableStateOf(base.specsPipeSeparated) }
    var customImageUri by remember(base) { mutableStateOf(base.customImageUri ?: "") }
    var primaryDrawableName by remember(base) { mutableStateOf(base.primaryImageResName) }
    var isFeatured by remember(base) { mutableStateOf(base.isFeatured) }
    var isNewArrival by remember(base) { mutableStateOf(base.isNewArrival) }

    // Zero-permission Android Photo Picker (Google Play Policy compliant)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
            }
            customImageUri = uri.toString()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Edit / Upload Product Details & Pictures",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
        }

        // Image Upload / Selection Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Product Picture (Upload Real Photo or Select Studio Asset)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AnzoraNavyDeep,
                                contentColor = AnzoraWhite
                            )
                        ) {
                            Icon(Icons.Filled.PhotoLibrary, contentDescription = null, tint = AnzoraElectricBlue)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pick Photo from Device")
                        }

                        if (customImageUri.isNotBlank()) {
                            OutlinedButton(onClick = { customImageUri = "" }) {
                                Text("Revert to Studio Preset")
                            }
                        }
                    }

                    OutlinedTextField(
                        value = customImageUri,
                        onValueChange = { customImageUri = it },
                        label = { Text("Custom Image URI or HTTPS Image URL (Optional)") },
                        placeholder = { Text("https://... or content://...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "Or Select Built-in ANZORA Studio Preset:",
                        style = MaterialTheme.typography.labelMedium
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val presets = listOf(
                            "img_cat_audio" to "Audio Studio",
                            "img_cat_charging" to "GaN & Cables",
                            "img_cat_wearables" to "Watch & Ring Light",
                            "img_hero_banner" to "Flat Lay Collection"
                        )
                        items(presets) { (resKey, label) ->
                            FilterChip(
                                selected = primaryDrawableName == resKey && customImageUri.isBlank(),
                                onClick = {
                                    primaryDrawableName = resKey
                                    customImageUri = ""
                                },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }
        }

        // Category Selection
        item {
            Text(
                text = "Select Category:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories, key = { it.id }) { cat ->
                    FilterChip(
                        selected = selectedCategoryId == cat.id,
                        onClick = { selectedCategoryId = cat.id },
                        label = { Text(cat.name) }
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Product Title *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = subtitle,
                onValueChange = { subtitle = it },
                label = { Text("Short Subtitle / Key Highlights") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = pricePkr,
                    onValueChange = { pricePkr = it },
                    label = { Text("Sale Price (PKR) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = compareAtPkr,
                    onValueChange = { compareAtPkr = it },
                    label = { Text("Compare At (PKR)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = stockQty,
                    onValueChange = { stockQty = it },
                    label = { Text("Stock Quantity *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = sku,
                    onValueChange = { sku = it },
                    label = { Text("SKU Code *") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Product Description") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = specsPipe,
                onValueChange = { specsPipe = it },
                label = { Text("Technical Specs (Key: Value separated by |)") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isFeatured, onCheckedChange = { isFeatured = it })
                    Text("Featured on Home")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isNewArrival, onCheckedChange = { isNewArrival = it })
                    Text("New Arrival Badge")
                }
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        val catName = categories.find { it.id == selectedCategoryId }?.name ?: "Mobile accessories"
                        val updated = base.copy(
                            title = title.trim(),
                            subtitle = subtitle.trim(),
                            categoryId = selectedCategoryId,
                            categoryName = catName,
                            pricePkr = pricePkr.toLongOrNull() ?: 0L,
                            compareAtPricePkr = compareAtPkr.toLongOrNull(),
                            stockQuantity = stockQty.toIntOrNull() ?: 0,
                            sku = sku.trim().ifBlank { "ANZ-SKU" },
                            description = description.trim(),
                            specsPipeSeparated = specsPipe.trim(),
                            primaryImageResName = primaryDrawableName,
                            customImageUri = customImageUri.trim().ifBlank { null },
                            isFeatured = isFeatured,
                            isNewArrival = isNewArrival
                        )
                        onSave(updated)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AnzoraNavyDeep,
                        contentColor = AnzoraWhite
                    )
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null, tint = AnzoraElectricBlue)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Product")
                }
            }
        }
    }
}

@Composable
private fun AdminOrdersManagementSection(
    orders: List<OrderEntity>,
    onUpdateStatus: (String, String) -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (orders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "No Customer Orders Recorded Yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "New Cash on Delivery and WhatsApp orders placed from the storefront will appear here in real time.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(orders, key = { it.orderNumber }) { order ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${order.orderNumber} • ${order.customerName}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "${order.customerPhone} • ${order.customerCity} • ${AnzoraRepository.formatTimestamp(order.createdAt)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = AnzoraRepository.formatPkr(order.totalPkr),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = AnzoraElectricBlueDark
                            )
                        }

                        Text(
                            text = "Delivery Address: ${order.customerAddress}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        if (order.orderNotes.isNotBlank()) {
                            Text(
                                text = "Customer Note: ${order.orderNotes}",
                                style = MaterialTheme.typography.bodySmall,
                                color = AnzoraWarning
                            )
                        }

                        HorizontalDivider()

                        order.parsedItems.forEach { item ->
                            Text(
                                text = "• ${item.quantity}x ${item.title} (${item.sku}) — ${AnzoraRepository.formatPkr(item.lineTotalPkr)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Text(
                            text = "Update Order Status (Cancelling automatically restores inventory stock):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(AnzoraRepository.ORDER_STATUSES) { statusOption ->
                                val isCurrent = order.status == statusOption
                                FilterChip(
                                    selected = isCurrent,
                                    onClick = { onUpdateStatus(order.orderNumber, statusOption) },
                                    label = { Text(statusOption) }
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                val customerDigits = order.customerPhone.replace("+", "").replace(Regex("^0"), "92")
                                val msg = "Assalam-o-Alaikum ${order.customerName}, this is ANZORA Pakistan regarding your order *${order.orderNumber}* (${AnzoraRepository.formatPkr(order.totalPkr)}). Current status: *${order.status}*."
                                val url = "https://wa.me/$customerDigits?text=${java.net.URLEncoder.encode(msg, "UTF-8")}"
                                launchExternalUrl(context, url, onShowMessage)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = null, tint = WhatsAppGreen)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Message Customer on WhatsApp (${order.customerPhone})")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminCategoriesSection(
    categories: List<CategoryEntity>,
    onSaveCategory: (CategoryEntity) -> Unit
) {
    var newCategoryName by remember { mutableStateOf("") }
    var newCategoryDesc by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Add New Product Category",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        label = { Text("Category Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newCategoryDesc,
                        onValueChange = { newCategoryDesc = it },
                        label = { Text("Category Description") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            if (newCategoryName.isNotBlank()) {
                                val slug = newCategoryName.trim().lowercase().replace(" ", "-")
                                onSaveCategory(
                                    CategoryEntity(
                                        id = "cat_$slug",
                                        name = newCategoryName.trim(),
                                        slug = slug,
                                        description = newCategoryDesc.trim().ifBlank { "ANZORA Electronics Accessories" },
                                        iconName = "devices_other",
                                        displayOrder = categories.size + 1
                                    )
                                )
                                newCategoryName = ""
                                newCategoryDesc = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AnzoraNavyDeep,
                            contentColor = AnzoraWhite
                        )
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, tint = AnzoraElectricBlue)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Category")
                    }
                }
            }
        }

        items(categories, key = { it.id }) { cat ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${cat.displayOrder}. ${cat.name} (${cat.id})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = cat.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminSettingsSection(
    storeSettings: StoreSettingsEntity,
    onUpdateSettings: (Long, Long, Boolean, String, String, String?) -> Unit
) {
    var deliveryFeeInput by remember(storeSettings) {
        mutableStateOf(storeSettings.standardDeliveryFeePkr.toString())
    }
    var freeThresholdInput by remember(storeSettings) {
        mutableStateOf(storeSettings.freeDeliveryThresholdPkr.toString())
    }
    var enableFreeThreshold by remember(storeSettings) {
        mutableStateOf(storeSettings.enableFreeDeliveryThreshold)
    }
    var promoHeadline by remember(storeSettings) {
        mutableStateOf(storeSettings.promoBannerHeadline)
    }
    var promoSubtext by remember(storeSettings) {
        mutableStateOf(storeSettings.promoBannerSubtext)
    }
    var newAdminPassword by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Configurable Delivery Charges (PKR)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Configure nationwide Cash on Delivery shipping fees instead of assuming delivery is always free.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = deliveryFeeInput,
                        onValueChange = { deliveryFeeInput = it },
                        label = { Text("Standard Nationwide Delivery Fee (PKR)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_delivery_fee_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Enable Free Delivery Threshold",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Waive shipping fee when subtotal exceeds threshold",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = enableFreeThreshold,
                            onCheckedChange = { enableFreeThreshold = it }
                        )
                    }

                    if (enableFreeThreshold) {
                        OutlinedTextField(
                            value = freeThresholdInput,
                            onValueChange = { freeThresholdInput = it },
                            label = { Text("Free Delivery Minimum Order Subtotal (PKR)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    HorizontalDivider()

                    Text(
                        text = "Storefront Promotional Banner Copy",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )

                    OutlinedTextField(
                        value = promoHeadline,
                        onValueChange = { promoHeadline = it },
                        label = { Text("Hero Banner Headline") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = promoSubtext,
                        onValueChange = { promoSubtext = it },
                        label = { Text("Hero Banner Subtext") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider()

                    Text(
                        text = "Rotate Administrator Password (Optional)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    OutlinedTextField(
                        value = newAdminPassword,
                        onValueChange = { newAdminPassword = it },
                        label = { Text("New Admin Password (leave blank to keep current)") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val fee = deliveryFeeInput.toLongOrNull() ?: storeSettings.standardDeliveryFeePkr
                            val threshold = freeThresholdInput.toLongOrNull() ?: storeSettings.freeDeliveryThresholdPkr
                            onUpdateSettings(
                                fee,
                                threshold,
                                enableFreeThreshold,
                                promoHeadline,
                                promoSubtext,
                                newAdminPassword.takeIf { it.isNotBlank() }
                            )
                            newAdminPassword = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("admin_save_settings_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AnzoraNavyDeep,
                            contentColor = AnzoraWhite
                        )
                    ) {
                        Icon(Icons.Filled.Save, contentDescription = null, tint = AnzoraElectricBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Store & Delivery Settings")
                    }
                }
            }
        }
    }
}

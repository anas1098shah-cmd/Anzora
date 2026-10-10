package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AnzoraCategories
import com.example.data.model.Product
import com.example.data.model.StoreConfig
import com.example.data.util.WhatsAppHelper
import com.example.ui.components.ProductVisualBanner
import com.example.ui.components.categoryIcon
import com.example.ui.theme.AnzoraElectricBlue
import com.example.ui.theme.AnzoraError
import com.example.ui.theme.AnzoraNavyBorder
import com.example.ui.theme.AnzoraNavyCard
import com.example.ui.theme.AnzoraNavyDark
import com.example.ui.theme.AnzoraNavyPrimary
import com.example.ui.theme.AnzoraSlateLight
import com.example.ui.theme.AnzoraSuccess
import com.example.ui.theme.AnzoraWarning
import com.example.ui.theme.AnzoraWhatsAppGreen
import com.example.ui.theme.AnzoraWhite
import com.example.ui.viewmodel.SortOption
import com.example.ui.viewmodel.UiState

@Composable
fun AnzoraStorefrontScreen(
    productsState: UiState<List<Product>>,
    storeConfig: StoreConfig,
    searchQuery: String,
    selectedCategory: String,
    selectedSort: SortOption,
    inStockOnly: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onSortSelect: (SortOption) -> Unit,
    onToggleInStockOnly: (Boolean) -> Unit,
    onClearFilters: () -> Unit,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onOpenWhatsAppDirect: (String) -> Unit
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 165.dp),
        modifier = Modifier
            .fillMaxSize()
            .testTag("storefront_grid"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Top Announcement Banner
        item(span = { GridItemSpan(maxLineSpan) }) {
            Surface(
                color = AnzoraElectricBlue.copy(alpha = 0.14f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AnzoraElectricBlue.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = "Announcement",
                        tint = AnzoraElectricBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = storeConfig.announcementBanner,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // 2. Hero Promotional Banner
        item(span = { GridItemSpan(maxLineSpan) }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp),
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_anzora_hero_banner_1791593805771),
                        contentDescription = "ANZORA Flagship Electronics Accessories",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        AnzoraNavyDark.copy(alpha = 0.92f),
                                        AnzoraNavyDark.copy(alpha = 0.65f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(20.dp)
                            .fillMaxWidth(0.78f)
                    ) {
                        Surface(
                            color = AnzoraElectricBlue,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "CASH ON DELIVERY ACROSS PAKISTAN",
                                style = MaterialTheme.typography.labelSmall,
                                color = AnzoraNavyPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Elevate Your Everyday Tech",
                            style = MaterialTheme.typography.headlineMedium,
                            color = AnzoraWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ANC Earbuds • Studio Headphones • 65W GaN Chargers • Smartwatches • Creator Ring Lights",
                            style = MaterialTheme.typography.bodySmall,
                            color = AnzoraSlateLight
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AssistChip(
                                onClick = {
                                    onOpenWhatsAppDirect(
                                        WhatsAppHelper.buildWhatsAppUrl(
                                            storeConfig.whatsappNumber,
                                            "Assalam-o-Alaikum ANZORA! I would like to inquire about your electronics accessories catalog."
                                        )
                                    )
                                },
                                label = {
                                    Text(
                                        text = "WhatsApp: ${storeConfig.whatsappNumber}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AnzoraWhite
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.SupportAgent,
                                        contentDescription = "WhatsApp",
                                        tint = AnzoraWhatsAppGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = AnzoraNavyCard.copy(alpha = 0.85f)
                                )
                            )
                        }
                    }
                }
            }
        }

        // 3. Search Bar & Filter Controls
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("catalog_search_input"),
                    placeholder = {
                        Text("Search earbuds, GaN chargers, smartwatches, cables...")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search catalog",
                            tint = AnzoraElectricBlue
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnzoraElectricBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                // Horizontal Category Chips (All + 8 Mandatory ANZORA Categories)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val allCategories = listOf(AnzoraCategories.ALL) + AnzoraCategories.LIST
                    allCategories.forEach { category ->
                        val isSelected = selectedCategory.equals(category, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onCategorySelect(category) },
                            label = {
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            leadingIcon = if (category != AnzoraCategories.ALL) {
                                {
                                    Icon(
                                        imageVector = categoryIcon(category),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AnzoraElectricBlue,
                                selectedLabelColor = AnzoraNavyPrimary,
                                selectedLeadingIconColor = AnzoraNavyPrimary
                            ),
                            modifier = Modifier.testTag("category_chip_${category.lowercase().replace(" ", "_")}")
                        )
                    }
                }

                // Sort & In-Stock Filter Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = inStockOnly,
                        onClick = { onToggleInStockOnly(!inStockOnly) },
                        label = { Text("In Stock Only") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter in stock",
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AnzoraElectricBlue.copy(alpha = 0.2f),
                            selectedLabelColor = AnzoraElectricBlue
                        )
                    )

                    Box {
                        OutlinedButton(
                            onClick = { sortMenuExpanded = true },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("sort_dropdown_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = "Sort products",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = selectedSort.label,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        DropdownMenu(
                            expanded = sortMenuExpanded,
                            onDismissRequest = { sortMenuExpanded = false }
                        ) {
                            SortOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label) },
                                    onClick = {
                                        onSortSelect(option)
                                        sortMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Product Grid Content
        when (productsState) {
            is UiState.Loading -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AnzoraElectricBlue)
                    }
                }
            }

            is UiState.Error -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = productsState.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            is UiState.Success -> {
                val products = productsState.data
                if (products.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 28.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "No matching products",
                                    tint = AnzoraElectricBlue,
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "No matching accessories found",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Try clearing your search filter or exploring another ANZORA category.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = onClearFilters,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = AnzoraElectricBlue,
                                        contentColor = AnzoraNavyPrimary
                                    )
                                ) {
                                    Text("Reset Filters")
                                }
                            }
                        }
                    }
                } else {
                    items(products, key = { it.id.ifBlank { it.name } }) { product ->
                        AnzoraProductCard(
                            product = product,
                            onClick = { onProductClick(product) },
                            onAddToCart = { onAddToCart(product) }
                        )
                    }
                }
            }
        }

        // 5. Trust & Delivery Footer Banner
        item(span = { GridItemSpan(maxLineSpan) }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Quality Checked",
                            tint = AnzoraElectricBlue,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "100% Quality Tested by ANZORA Pakistan",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Standard Delivery: ${WhatsAppHelper.formatPkr(storeConfig.deliveryFeePkr)} • Free Shipping above ${WhatsAppHelper.formatPkr(storeConfig.freeDeliveryThresholdPkr)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnzoraProductCard(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    val inStock = product.stock > 0L
    val hasDiscount = product.originalPricePkr > product.pricePkr
    val discountPercent = if (hasDiscount && product.originalPricePkr > 0) {
        (((product.originalPricePkr - product.pricePkr) * 100) / product.originalPricePkr).toInt()
    } else 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("product_card_${product.id.ifBlank { product.sku }}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp)
            ) {
                ProductVisualBanner(
                    imageKeyOrUrl = product.imageUrls.firstOrNull() ?: "preset:earbuds",
                    category = product.category,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize()
                )

                if (discountPercent > 0) {
                    Surface(
                        color = AnzoraElectricBlue,
                        shape = RoundedCornerShape(bottomEnd = 10.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "-$discountPercent%",
                            style = MaterialTheme.typography.labelSmall,
                            color = AnzoraNavyPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Surface(
                    color = if (inStock) AnzoraSuccess.copy(alpha = 0.9f) else AnzoraError.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = if (inStock) "In Stock (${product.stock})" else "Sold Out",
                        style = MaterialTheme.typography.labelSmall,
                        color = AnzoraWhite,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = product.category.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = AnzoraElectricBlue,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = WhatsAppHelper.formatPkr(product.pricePkr),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (hasDiscount) {
                        Text(
                            text = WhatsAppHelper.formatPkr(product.originalPricePkr),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onAddToCart,
                    enabled = inStock,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("add_to_cart_${product.id.ifBlank { product.sku }}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AnzoraElectricBlue,
                        contentColor = AnzoraNavyPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = "Add to cart",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (inStock) "Add to Cart" else "Out of Stock",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
fun AnzoraProductDetailScreen(
    product: Product,
    storeConfig: StoreConfig,
    onBack: () -> Unit,
    onAddToCart: (Product, Int) -> Unit,
    onBuyNowWhatsApp: (String) -> Unit
) {
    BackHandler { onBack() }

    var selectedImageIndex by remember { mutableIntStateOf(0) }
    var quantity by remember { mutableIntStateOf(1) }
    val inStock = product.stock > 0L
    val images = product.imageUrls.ifEmpty { listOf("preset:earbuds") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("product_detail_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to catalog"
                )
            }
            Text(
                text = product.sku,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Main Image Gallery
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            shape = RoundedCornerShape(22.dp)
        ) {
            ProductVisualBanner(
                imageKeyOrUrl = images.getOrElse(selectedImageIndex) { images.first() },
                category = product.category,
                contentDescription = product.name,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Gallery Thumbnails if multiple images
        if (images.size > 1) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                images.forEachIndexed { idx, imgKey ->
                    Card(
                        modifier = Modifier
                            .size(68.dp)
                            .clickable { selectedImageIndex = idx }
                            .border(
                                width = if (selectedImageIndex == idx) 2.dp else 1.dp,
                                color = if (selectedImageIndex == idx) AnzoraElectricBlue else MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(12.dp)
                            ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        ProductVisualBanner(
                            imageKeyOrUrl = imgKey,
                            category = product.category,
                            contentDescription = "Gallery thumbnail ${idx + 1}",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Category & Stock Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = AnzoraElectricBlue.copy(alpha = 0.16f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = product.category,
                    style = MaterialTheme.typography.labelMedium,
                    color = AnzoraElectricBlue,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (inStock) AnzoraSuccess else AnzoraError,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (inStock) "${product.stock} units available in stock" else "Out of Stock",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (inStock) AnzoraSuccess else AnzoraError
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = product.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = WhatsAppHelper.formatPkr(product.pricePkr),
                style = MaterialTheme.typography.displayMedium,
                color = AnzoraElectricBlue,
                fontWeight = FontWeight.Bold
            )
            if (product.originalPricePkr > product.pricePkr) {
                Text(
                    text = WhatsAppHelper.formatPkr(product.originalPricePkr),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textDecoration = TextDecoration.LineThrough
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
        Spacer(modifier = Modifier.height(16.dp))

        // Product Description
        Text(
            text = "Product Overview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = product.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Technical Specs
        Text(
            text = "Key Specifications",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                product.specs.split("|").map { it.trim() }.filter { it.isNotEmpty() }.forEach { spec ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(AnzoraElectricBlue)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = spec,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Delivery & COD Policy Notice
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = AnzoraElectricBlue.copy(alpha = 0.08f)
            )
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Cash on Delivery & Shipping Info",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "• Nationwide Delivery Fee: ${WhatsAppHelper.formatPkr(storeConfig.deliveryFeePkr)} (Free above ${WhatsAppHelper.formatPkr(storeConfig.freeDeliveryThresholdPkr)})",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• Pay in PKR cash at your doorstep when receiving your parcel.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quantity Selector
        if (inStock) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Select Quantity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { if (quantity > 1) quantity-- },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease quantity")
                    }
                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    IconButton(
                        onClick = {
                            if (quantity < product.stock.toInt()) quantity++
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase quantity")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons: Add to Cart + Inquire on WhatsApp
        Button(
            onClick = { onAddToCart(product, quantity) },
            enabled = inStock,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("detail_add_to_cart_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AnzoraElectricBlue,
                contentColor = AnzoraNavyPrimary
            )
        ) {
            Icon(Icons.Default.AddShoppingCart, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Add $quantity to Cart • ${WhatsAppHelper.formatPkr(product.pricePkr * quantity)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = {
                val msg = "Assalam-o-Alaikum ANZORA! I want to inquire about / order:\n" +
                    "• ${quantity}x ${product.name} (${WhatsAppHelper.formatPkr(product.pricePkr)} each)\n" +
                    "SKU: ${product.sku}"
                onBuyNowWhatsApp(
                    WhatsAppHelper.buildWhatsAppUrl(storeConfig.whatsappNumber, msg)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("detail_whatsapp_inquiry_button"),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = AnzoraWhatsAppGreen
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Quick Order / Ask on WhatsApp",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

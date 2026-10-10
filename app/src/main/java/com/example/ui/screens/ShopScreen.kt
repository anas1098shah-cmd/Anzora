package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.example.R
import com.example.data.local.CategoryEntity
import com.example.data.local.ProductEntity
import com.example.data.local.StoreSettingsEntity
import com.example.data.repository.AnzoraRepository
import com.example.ui.FilterState
import com.example.ui.SortOption
import com.example.ui.components.AnzoraProductVisual
import com.example.ui.components.StockAvailabilityBadge
import com.example.ui.components.categoryIconFor
import com.example.ui.theme.AnzoraElectricBlue
import com.example.ui.theme.AnzoraElectricBlueDark
import com.example.ui.theme.AnzoraNavyCard
import com.example.ui.theme.AnzoraNavyDeep
import com.example.ui.theme.AnzoraNavySurface
import com.example.ui.theme.AnzoraWhite

@Composable
fun ShopScreen(
    categories: List<CategoryEntity>,
    products: List<ProductEntity>,
    filterState: FilterState,
    storeSettings: StoreSettingsEntity,
    onSearchQueryChange: (String) -> Unit,
    onSelectCategory: (String?) -> Unit,
    onToggleInStockOnly: (Boolean) -> Unit,
    onUpdateMaxPrice: (Long?) -> Unit,
    onUpdateSort: (SortOption) -> Unit,
    onResetFilters: () -> Unit,
    onOpenProductDetail: (String) -> Unit,
    onAddToCart: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAdvancedFilters by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val columns = when {
            maxWidth >= 900.dp -> 4
            maxWidth >= 600.dp -> 3
            else -> 2
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier
                .fillMaxSize()
                .testTag("shop_product_grid"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Hero Banner
            item(span = { GridItemSpan(maxLineSpan) }) {
                AnzoraHeroBanner(
                    headline = storeSettings.promoBannerHeadline,
                    subtext = storeSettings.promoBannerSubtext,
                    freeDeliveryThresholdPkr = if (storeSettings.enableFreeDeliveryThreshold) storeSettings.freeDeliveryThresholdPkr else null,
                    standardDeliveryFeePkr = storeSettings.standardDeliveryFeePkr
                )
            }

            // 2. Search Bar & Filter Toggle
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = filterState.searchQuery,
                            onValueChange = onSearchQueryChange,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("catalog_search_input"),
                            placeholder = {
                                Text(
                                    "Search earbuds, GaN chargers, smartwatches…",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = "Search catalog"
                                )
                            },
                            trailingIcon = {
                                if (filterState.searchQuery.isNotBlank()) {
                                    IconButton(
                                        onClick = { onSearchQueryChange("") },
                                        modifier = Modifier.testTag("clear_search_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Clear,
                                            contentDescription = "Clear search"
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AnzoraElectricBlue,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        Surface(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { showAdvancedFilters = !showAdvancedFilters }
                                .testTag("toggle_filters_button"),
                            color = if (showAdvancedFilters || filterState.inStockOnly || filterState.maxPricePkr != null) {
                                AnzoraElectricBlue
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.FilterList,
                                    contentDescription = "Filter & sort options",
                                    tint = if (showAdvancedFilters || filterState.inStockOnly || filterState.maxPricePkr != null) {
                                        AnzoraNavyDeep
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }

                    // Expandable Sort & Price/Stock Filter Drawer
                    AnimatedVisibility(visible = showAdvancedFilters) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Sort & Filter Catalog",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Reset All",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = AnzoraElectricBlueDark,
                                        modifier = Modifier
                                            .clickable { onResetFilters() }
                                            .padding(4.dp)
                                            .testTag("reset_filters_button")
                                    )
                                }

                                Text(
                                    text = "Sort By",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(SortOption.entries) { option ->
                                        val selected = filterState.sortOption == option
                                        FilterChip(
                                            selected = selected,
                                            onClick = { onUpdateSort(option) },
                                            label = { Text(option.label) },
                                            leadingIcon = if (selected) {
                                                {
                                                    Icon(
                                                        imageVector = Icons.Filled.Check,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            } else null
                                        )
                                    }
                                }

                                Text(
                                    text = "Availability & Price Range (PKR)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    item {
                                        FilterChip(
                                            selected = filterState.inStockOnly,
                                            onClick = { onToggleInStockOnly(!filterState.inStockOnly) },
                                            label = { Text("In Stock Only") }
                                        )
                                    }
                                    val priceCaps = listOf(2000L, 4000L, 7500L, 12000L)
                                    items(priceCaps) { cap ->
                                        val isSelected = filterState.maxPricePkr == cap
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = {
                                                onUpdateMaxPrice(if (isSelected) null else cap)
                                            },
                                            label = { Text("Under ${AnzoraRepository.formatPkr(cap)}") }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Category Pills Row (All 8 categories)
            item(span = { GridItemSpan(maxLineSpan) }) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.testTag("category_chips_row")
                ) {
                    item {
                        val allSelected = filterState.selectedCategoryId == null
                        FilterChip(
                            selected = allSelected,
                            onClick = { onSelectCategory(null) },
                            label = { Text("All Accessories") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AnzoraNavyDeep,
                                selectedLabelColor = AnzoraElectricBlue
                            ),
                            modifier = Modifier.testTag("category_chip_all")
                        )
                    }
                    items(categories, key = { it.id }) { category ->
                        val isSelected = filterState.selectedCategoryId == category.id
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                onSelectCategory(if (isSelected) null else category.id)
                            },
                            label = { Text(category.name) },
                            leadingIcon = {
                                Icon(
                                    imageVector = categoryIconFor(category.iconName),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AnzoraNavyDeep,
                                selectedLabelColor = AnzoraElectricBlue,
                                selectedLeadingIconColor = AnzoraElectricBlue
                            ),
                            modifier = Modifier.testTag("category_chip_${category.id}")
                        )
                    }
                }
            }

            // 4. Active Results Count
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val activeCategoryName = categories.find { it.id == filterState.selectedCategoryId }?.name
                        ?: "All ANZORA Products"
                    Text(
                        text = activeCategoryName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${products.size} item(s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 5. Empty State or Product Cards
            if (products.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                modifier = Modifier.size(44.dp),
                                tint = AnzoraElectricBlueDark
                            )
                            Text(
                                text = "No matching electronics accessories found",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Try clearing your search query or adjusting the PKR price filter.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            OutlinedButton(
                                onClick = onResetFilters,
                                modifier = Modifier.testTag("empty_state_reset_button")
                            ) {
                                Text("Show All Products")
                            }
                        }
                    }
                }
            } else {
                items(products, key = { it.id }) { product ->
                    ProductGridCard(
                        product = product,
                        onClick = { onOpenProductDetail(product.id) },
                        onAddToCart = { onAddToCart(product.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AnzoraHeroBanner(
    headline: String,
    subtext: String,
    freeDeliveryThresholdPkr: Long?,
    standardDeliveryFeePkr: Long
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("anzora_hero_banner"),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(AnzoraNavyDeep, AnzoraNavySurface, AnzoraNavyCard)
                    )
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_banner_1791591491584),
                contentDescription = "ANZORA featured electronics accessories",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(225.dp),
                contentScale = ContentScale.Crop,
                alpha = 0.38f
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(225.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                AnzoraNavyDeep.copy(alpha = 0.94f),
                                AnzoraNavyDeep.copy(alpha = 0.72f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = AnzoraElectricBlue.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = AnzoraElectricBlue.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(50)
                    )
                ) {
                    Text(
                        text = "OFFICIAL PAKISTAN STORE • CASH ON DELIVERY",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = AnzoraElectricBlue,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = headline,
                    style = MaterialTheme.typography.headlineMedium,
                    color = AnzoraWhite,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = subtext,
                    style = MaterialTheme.typography.bodySmall,
                    color = AnzoraWhite.copy(alpha = 0.85f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HeroTrustPill(
                        icon = Icons.Filled.Payments,
                        text = "Cash on Delivery"
                    )
                    HeroTrustPill(
                        icon = Icons.Filled.LocalShipping,
                        text = if (freeDeliveryThresholdPkr != null) {
                            "Free Delivery > ${AnzoraRepository.formatPkr(freeDeliveryThresholdPkr)}"
                        } else {
                            "Delivery: ${AnzoraRepository.formatPkr(standardDeliveryFeePkr)}"
                        }
                    )
                    HeroTrustPill(
                        icon = Icons.Filled.VerifiedUser,
                        text = "WhatsApp Support"
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroTrustPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AnzoraElectricBlue,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = AnzoraWhite,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ProductGridCard(
    product: ProductEntity,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp)
                    .background(AnzoraNavyDeep)
            ) {
                AnzoraProductVisual(
                    product = product,
                    modifier = Modifier.fillMaxSize()
                )

                // Top Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    if (product.isNewArrival) {
                        Surface(
                            color = AnzoraElectricBlue,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "NEW",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = AnzoraNavyDeep,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    val compareAt = product.compareAtPricePkr
                    if (compareAt != null && compareAt > product.pricePkr) {
                        val savePercent = (((compareAt - product.pricePkr) * 100) / compareAt).toInt()
                        Surface(
                            color = AnzoraNavyDeep.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "-$savePercent%",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = AnzoraElectricBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = product.categoryName.uppercase(),
                    style = MaterialTheme.typography.bodySmall,
                    color = AnzoraElectricBlueDark,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                StockAvailabilityBadge(stockQuantity = product.stockQuantity)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = AnzoraRepository.formatPkr(product.pricePkr),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        val compareAt = product.compareAtPricePkr
                        if (compareAt != null && compareAt > product.pricePkr) {
                            Text(
                                text = AnzoraRepository.formatPkr(compareAt),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Button(
                    onClick = onAddToCart,
                    enabled = product.stockQuantity > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_to_cart_${product.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AnzoraNavyDeep,
                        contentColor = AnzoraWhite
                    ),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = AnzoraElectricBlue
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (product.stockQuantity > 0) "Add to Cart" else "Out of Stock",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

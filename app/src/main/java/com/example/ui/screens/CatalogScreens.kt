package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.data.local.CategoryEntity
import com.example.data.local.ProductEntity
import com.example.data.local.StoreSettingsEntity
import com.example.data.repository.AnzoraRepository
import com.example.ui.components.AnzoraProductVisual
import com.example.ui.components.StockAvailabilityBadge
import com.example.ui.components.categoryIconFor
import com.example.ui.components.launchExternalUrl
import com.example.ui.theme.AnzoraElectricBlue
import com.example.ui.theme.AnzoraElectricBlueDark
import com.example.ui.theme.AnzoraNavyCard
import com.example.ui.theme.AnzoraNavyDeep
import com.example.ui.theme.AnzoraNavySurface
import com.example.ui.theme.AnzoraWhite
import com.example.ui.theme.WhatsAppGreen
import java.net.URLEncoder

@Composable
fun CategoriesDirectoryScreen(
    categories: List<CategoryEntity>,
    products: List<ProductEntity>,
    onSelectCategoryToBrowse: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val columns = when {
            maxWidth >= 840.dp -> 3
            maxWidth >= 520.dp -> 2
            else -> 1
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier
                .fillMaxSize()
                .testTag("categories_directory_grid"),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories, key = { it.id }) { category ->
                val categoryProducts = products.filter { it.categoryId == category.id }
                val inStockCount = categoryProducts.count { it.stockQuantity > 0 }
                val startingPrice = categoryProducts.minOfOrNull { it.pricePkr } ?: 0L

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectCategoryToBrowse(category.id) }
                        .testTag("category_directory_card_${category.id}"),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(AnzoraNavyDeep, AnzoraNavyCard)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = categoryIconFor(category.iconName),
                                contentDescription = category.name,
                                tint = AnzoraElectricBlue,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = category.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = category.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$inStockCount in stock",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = AnzoraElectricBlueDark,
                                    fontWeight = FontWeight.Bold
                                )
                                if (startingPrice > 0L) {
                                    Text(
                                        text = "From ${AnzoraRepository.formatPkr(startingPrice)}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Browse ${category.name}",
                            tint = AnzoraElectricBlueDark
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductDetailScreen(
    product: ProductEntity,
    storeSettings: StoreSettingsEntity,
    onBack: () -> Unit,
    onAddToCart: (String, Int) -> Unit,
    onBuyNowCheckout: (String, Int) -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    var selectedQty by remember(product.id) { mutableIntStateOf(1) }
    var selectedGalleryIndex by remember(product.id) { mutableIntStateOf(0) }
    val galleryViews = remember(product.galleryPipeSeparated) {
        val list = product.galleryAngles
        if (list.isEmpty()) listOf("Studio Front View", "Side Angle View", "Packaging & Accessories") else list
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("product_detail_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Back Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onBack,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("detail_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to catalog",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Back to Catalog")
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "SKU: ${product.sku}",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        // Interactive Image Gallery Viewer
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(275.dp)
                    .background(AnzoraNavyDeep)
            ) {
                AnzoraProductVisual(
                    product = product,
                    modifier = Modifier.fillMaxSize()
                )

                // Gallery Angle Caption Overlay
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    color = AnzoraNavyDeep.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "View ${selectedGalleryIndex + 1}/${galleryViews.size}: ${galleryViews.getOrElse(selectedGalleryIndex) { "Studio View" }}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = AnzoraElectricBlue
                    )
                }
            }
        }

        // Gallery Angle Selector Pills
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(galleryViews) { index, angleLabel ->
                val isSelected = selectedGalleryIndex == index
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { selectedGalleryIndex = index }
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) AnzoraElectricBlue else MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    color = if (isSelected) AnzoraNavyDeep else MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) AnzoraElectricBlue else MaterialTheme.colorScheme.surfaceVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) AnzoraNavyDeep else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = angleLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) AnzoraWhite else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Title, Category, Stock & Pricing
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = product.categoryName.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = AnzoraElectricBlueDark,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = product.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = product.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = AnzoraRepository.formatPkr(product.pricePkr),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    val compareAt = product.compareAtPricePkr
                    if (compareAt != null && compareAt > product.pricePkr) {
                        Text(
                            text = AnzoraRepository.formatPkr(compareAt),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }
                StockAvailabilityBadge(stockQuantity = product.stockQuantity)
            }
        }

        // Quantity Selector & Action Buttons
        if (product.stockQuantity > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Select Quantity",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconButton(
                                onClick = { if (selectedQty > 1) selectedQty-- },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .testTag("detail_qty_decrease")
                            ) {
                                Icon(Icons.Filled.Remove, contentDescription = "Decrease quantity")
                            }
                            Text(
                                text = "$selectedQty",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = {
                                    if (selectedQty < product.stockQuantity) {
                                        selectedQty++
                                    } else {
                                        onShowMessage("Maximum available stock is ${product.stockQuantity}")
                                    }
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .testTag("detail_qty_increase")
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "Increase quantity")
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onAddToCart(product.id, selectedQty) },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("detail_add_to_cart_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AnzoraNavyDeep,
                                contentColor = AnzoraWhite
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AddShoppingCart,
                                contentDescription = null,
                                tint = AnzoraElectricBlue
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add to Cart")
                        }

                        Button(
                            onClick = { onBuyNowCheckout(product.id, selectedQty) },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("detail_buy_now_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AnzoraElectricBlue,
                                contentColor = AnzoraNavyDeep
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingBag,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Buy Now (COD)", fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    // Direct WhatsApp Inquiry Button for this Product
                    OutlinedButton(
                        onClick = {
                            val inquiry = "Assalam-o-Alaikum ANZORA! I would like to inquire about / order:\n" +
                                "• *${product.title}* (SKU: ${product.sku})\n" +
                                "• Quantity: $selectedQty\n" +
                                "• Unit Price: ${AnzoraRepository.formatPkr(product.pricePkr)}\n" +
                                "Please share Cash on Delivery availability."
                            val url = "https://wa.me/${AnzoraRepository.OFFICIAL_WHATSAPP_DIGITS}?text=${URLEncoder.encode(inquiry, "UTF-8")}"
                            launchExternalUrl(context, url, onShowMessage)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("detail_whatsapp_inquiry_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = null,
                            tint = WhatsAppGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ask or Order via WhatsApp (+92 371 2047636)")
                    }
                }
            }
        }

        // Delivery & COD Transparency Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = AnzoraNavySurface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalShipping,
                        contentDescription = null,
                        tint = AnzoraElectricBlue
                    )
                    Text(
                        text = "Pakistan Nationwide Delivery & COD Details",
                        style = MaterialTheme.typography.titleSmall,
                        color = AnzoraWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "• Standard Nationwide Delivery Fee: ${AnzoraRepository.formatPkr(storeSettings.standardDeliveryFeePkr)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AnzoraWhite.copy(alpha = 0.9f)
                )
                if (storeSettings.enableFreeDeliveryThreshold) {
                    Text(
                        text = "• Free Shipping on orders of ${AnzoraRepository.formatPkr(storeSettings.freeDeliveryThresholdPkr)} or above.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AnzoraElectricBlue
                    )
                }
                Text(
                    text = "• Payment Method: Cash on Delivery (Pay in PKR at your doorstep in Karachi, Lahore, Islamabad & 150+ cities).",
                    style = MaterialTheme.typography.bodySmall,
                    color = AnzoraWhite.copy(alpha = 0.9f)
                )
            }
        }

        // Product Description & Technical Specifications
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Product Overview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Verified,
                        contentDescription = null,
                        tint = AnzoraElectricBlueDark
                    )
                    Text(
                        text = "Technical Specifications",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                product.specsList.forEach { (key, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = key,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(0.4f)
                        )
                        Text(
                            text = value,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(0.6f)
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                }
            }
        }
    }
}

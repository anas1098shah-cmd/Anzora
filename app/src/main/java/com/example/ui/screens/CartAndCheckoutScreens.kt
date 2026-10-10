package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.CartItemWithProduct
import com.example.data.local.CustomerAccountEntity
import com.example.data.local.OrderEntity
import com.example.data.local.StoreSettingsEntity
import com.example.data.repository.AnzoraRepository
import com.example.ui.components.AnzoraProductVisual
import com.example.ui.components.launchExternalUrl
import com.example.ui.theme.AnzoraElectricBlue
import com.example.ui.theme.AnzoraElectricBlueDark
import com.example.ui.theme.AnzoraNavyDeep
import com.example.ui.theme.AnzoraNavySurface
import com.example.ui.theme.AnzoraSuccess
import com.example.ui.theme.AnzoraWarning
import com.example.ui.theme.AnzoraWhite
import com.example.ui.theme.WhatsAppGreen

@Composable
fun CartScreen(
    cartItems: List<CartItemWithProduct>,
    storeSettings: StoreSettingsEntity,
    onUpdateQuantity: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onOpenProductDetail: (String) -> Unit,
    onProceedToCheckout: () -> Unit,
    onBrowseCatalog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subtotalPkr = cartItems.sumOf { it.lineTotalPkr }
    val deliveryFeePkr = if (subtotalPkr == 0L) {
        0L
    } else if (storeSettings.enableFreeDeliveryThreshold && subtotalPkr >= storeSettings.freeDeliveryThresholdPkr) {
        0L
    } else {
        storeSettings.standardDeliveryFeePkr
    }
    val grandTotalPkr = subtotalPkr + deliveryFeePkr

    if (cartItems.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp)
                .testTag("empty_cart_view"),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCartCheckout,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = AnzoraElectricBlueDark
                    )
                    Text(
                        text = stringResource(R.string.empty_cart_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.empty_cart_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = onBrowseCatalog,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AnzoraNavyDeep,
                            contentColor = AnzoraWhite
                        ),
                        modifier = Modifier.testTag("empty_cart_shop_now_button")
                    ) {
                        Text("Explore ANZORA Accessories")
                    }
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("cart_items_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Free Shipping Progress Banner
        if (storeSettings.enableFreeDeliveryThreshold) {
            item {
                val remainingForFree = (storeSettings.freeDeliveryThresholdPkr - subtotalPkr).coerceAtLeast(0L)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AnzoraNavySurface
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocalShipping,
                            contentDescription = null,
                            tint = AnzoraElectricBlue
                        )
                        Text(
                            text = if (remainingForFree == 0L) {
                                "Unlocked! Your order qualifies for FREE Nationwide Delivery in Pakistan."
                            } else {
                                "Add ${AnzoraRepository.formatPkr(remainingForFree)} more to unlock FREE Nationwide Delivery (Standard fee: ${AnzoraRepository.formatPkr(storeSettings.standardDeliveryFeePkr)})."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = AnzoraWhite
                        )
                    }
                }
            }
        }

        items(cartItems, key = { it.product.id }) { item ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cart_item_${item.product.id}"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(82.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(AnzoraNavyDeep)
                            .clickable { onOpenProductDetail(item.product.id) }
                    ) {
                        AnzoraProductVisual(
                            product = item.product,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = item.product.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Unit: ${AnzoraRepository.formatPkr(item.product.pricePkr)} • SKU: ${item.product.sku}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Line Total: ${AnzoraRepository.formatPkr(item.lineTotalPkr)}",
                            style = MaterialTheme.typography.titleSmall,
                            color = AnzoraElectricBlueDark,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(
                                    onClick = { onUpdateQuantity(item.product.id, item.quantity - 1) },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .testTag("cart_qty_minus_${item.product.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Remove,
                                        contentDescription = "Decrease quantity",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = "${item.quantity}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(
                                    onClick = { onUpdateQuantity(item.product.id, item.quantity + 1) },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .testTag("cart_qty_plus_${item.product.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "Increase quantity",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            IconButton(
                                onClick = { onRemoveItem(item.product.id) },
                                modifier = Modifier.testTag("cart_remove_${item.product.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.DeleteOutline,
                                    contentDescription = "Remove item",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }

        // Order Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Order Summary (PKR)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Items Subtotal (${cartItems.sumOf { it.quantity }} items)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = AnzoraRepository.formatPkr(subtotalPkr),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Delivery Charges (Admin Configurable)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (deliveryFeePkr == 0L) "FREE (PKR 0)" else AnzoraRepository.formatPkr(deliveryFeePkr),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (deliveryFeePkr == 0L) AnzoraSuccess else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Payment Method",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Cash on Delivery (COD)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Grand Total",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = AnzoraRepository.formatPkr(grandTotalPkr),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = AnzoraElectricBlueDark
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = onProceedToCheckout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("proceed_to_checkout_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AnzoraNavyDeep,
                            contentColor = AnzoraWhite
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCartCheckout,
                            contentDescription = null,
                            tint = AnzoraElectricBlue
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Proceed to Checkout • ${AnzoraRepository.formatPkr(grandTotalPkr)}",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CheckoutScreen(
    cartItems: List<CartItemWithProduct>,
    storeSettings: StoreSettingsEntity,
    loggedInCustomer: CustomerAccountEntity?,
    onBackToCart: () -> Unit,
    onPlaceOrder: (
        customerName: String,
        customerPhone: String,
        customerCity: String,
        customerAddress: String,
        orderNotes: String,
        orderedViaWhatsApp: Boolean,
        onOrderCreated: (OrderEntity) -> Unit
    ) -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackToCart)

    val context = LocalContext.current

    var customerName by remember(loggedInCustomer) {
        mutableStateOf(loggedInCustomer?.fullName ?: "")
    }
    var customerPhone by remember(loggedInCustomer) {
        mutableStateOf(loggedInCustomer?.phone ?: "")
    }
    var customerCity by remember(loggedInCustomer) {
        mutableStateOf(loggedInCustomer?.city?.ifBlank { "Karachi" } ?: "Karachi")
    }
    var customerAddress by remember(loggedInCustomer) {
        mutableStateOf(loggedInCustomer?.completeAddress ?: "")
    }
    var orderNotes by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    val subtotalPkr = cartItems.sumOf { it.lineTotalPkr }
    val deliveryFeePkr = if (subtotalPkr == 0L) {
        0L
    } else if (storeSettings.enableFreeDeliveryThreshold && subtotalPkr >= storeSettings.freeDeliveryThresholdPkr) {
        0L
    } else {
        storeSettings.standardDeliveryFeePkr
    }
    val grandTotalPkr = subtotalPkr + deliveryFeePkr

    fun validateInputs(): Boolean {
        if (customerName.trim().length < 3) {
            validationError = "Please enter your full name (minimum 3 characters)."
            return false
        }
        if (!AnzoraRepository.isValidPakistaniPhone(customerPhone)) {
            validationError = "Please enter a valid Pakistani mobile number (e.g., 03001234567 or +923001234567)."
            return false
        }
        if (customerCity.trim().length < 2) {
            validationError = "Please enter or select your city in Pakistan."
            return false
        }
        if (customerAddress.trim().length < 10) {
            validationError = "Please enter your complete delivery address (House/Flat #, Street, Block/Sector, and nearby Landmark)."
            return false
        }
        validationError = null
        return true
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("checkout_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onBackToCart,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("checkout_back_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Cart")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Back to Cart")
            }
            Text(
                text = "Secure COD Checkout",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }

        // Customer Details Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "1. Customer & Delivery Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = customerName,
                    onValueChange = {
                        customerName = it
                        validationError = null
                    },
                    label = { Text("Customer Full Name *") },
                    placeholder = { Text("e.g., Muhammad Anas Shah") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("checkout_name_input")
                )

                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = {
                        customerPhone = it
                        validationError = null
                    },
                    label = { Text("Pakistani Mobile Number (WhatsApp / Call) *") },
                    placeholder = { Text("03XX-XXXXXXX or +923XXXXXXXXX") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("checkout_phone_input")
                )

                Text(
                    text = "Quick Select City (or type below):",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(AnzoraRepository.PAKISTAN_CITIES.take(10)) { cityOption ->
                        FilterChip(
                            selected = customerCity.equals(cityOption, ignoreCase = true),
                            onClick = { customerCity = cityOption },
                            label = { Text(cityOption) }
                        )
                    }
                }

                OutlinedTextField(
                    value = customerCity,
                    onValueChange = {
                        customerCity = it
                        validationError = null
                    },
                    label = { Text("City *") },
                    placeholder = { Text("Karachi, Lahore, Islamabad, etc.") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("checkout_city_input")
                )

                OutlinedTextField(
                    value = customerAddress,
                    onValueChange = {
                        customerAddress = it
                        validationError = null
                    },
                    label = { Text("Complete Street Address & Landmark *") },
                    placeholder = { Text("House/Flat No, Street, Phase/Sector/Block, Nearest Landmark") },
                    minLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("checkout_address_input")
                )

                OutlinedTextField(
                    value = orderNotes,
                    onValueChange = { orderNotes = it },
                    label = { Text("Order Notes / Delivery Instructions (Optional)") },
                    placeholder = { Text("e.g., Call before delivery, leave at reception") },
                    minLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("checkout_notes_input")
                )
            }
        }

        // Payment Method & Delivery Breakdown Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Payments,
                        contentDescription = null,
                        tint = AnzoraElectricBlueDark
                    )
                    Text(
                        text = "2. Payment Method & Order Verification",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.5.dp,
                            color = AnzoraElectricBlue,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    shape = RoundedCornerShape(12.dp),
                    color = AnzoraNavyDeep
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Cash on Delivery (COD) — PKR",
                                style = MaterialTheme.typography.titleSmall,
                                color = AnzoraWhite,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Pay cash to the courier upon receiving your sealed ANZORA parcel.",
                                style = MaterialTheme.typography.bodySmall,
                                color = AnzoraWhite.copy(alpha = 0.8f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Selected",
                            tint = AnzoraElectricBlue
                        )
                    }
                }

                HorizontalDivider()

                cartItems.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${item.quantity}x ${item.product.title}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = AnzoraRepository.formatPkr(item.lineTotalPkr),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Subtotal", style = MaterialTheme.typography.bodyMedium)
                    Text(AnzoraRepository.formatPkr(subtotalPkr), fontWeight = FontWeight.SemiBold)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Delivery Fee", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = if (deliveryFeePkr == 0L) "FREE (PKR 0)" else AnzoraRepository.formatPkr(deliveryFeePkr),
                        fontWeight = FontWeight.Bold,
                        color = if (deliveryFeePkr == 0L) AnzoraSuccess else MaterialTheme.colorScheme.onSurface
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Payable on Delivery",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = AnzoraRepository.formatPkr(grandTotalPkr),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = AnzoraElectricBlueDark
                    )
                }
            }
        }

        // Validation Error Alert
        if (validationError != null) {
            Surface(
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.14f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = validationError!!,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // WhatsApp Customer Send Disclosure
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = WhatsAppGreen.copy(alpha = 0.12f)
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = WhatsAppGreen
                )
                Text(
                    text = stringResource(R.string.whatsapp_send_notice),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Primary Action 1: Place Cash on Delivery Order
        Button(
            onClick = {
                if (validateInputs()) {
                    onPlaceOrder(
                        customerName,
                        customerPhone,
                        customerCity,
                        customerAddress,
                        orderNotes,
                        false
                    ) { /* Handled by confirmation modal */ }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("confirm_cod_order_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AnzoraNavyDeep,
                contentColor = AnzoraWhite
            )
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = AnzoraElectricBlue
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Place Cash on Delivery Order (${AnzoraRepository.formatPkr(grandTotalPkr)})",
                style = MaterialTheme.typography.labelLarge
            )
        }

        // Primary Action 2: Place Order & Launch WhatsApp (+923712047636) with URL-encoded Summary
        Button(
            onClick = {
                if (validateInputs()) {
                    onPlaceOrder(
                        customerName,
                        customerPhone,
                        customerCity,
                        customerAddress,
                        orderNotes,
                        true
                    ) { createdOrder ->
                        val waUrl = AnzoraRepository.buildWhatsAppCheckoutUrl(createdOrder)
                        launchExternalUrl(context, waUrl, onShowMessage)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("confirm_whatsapp_order_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = WhatsAppGreen,
                contentColor = AnzoraNavyDeep
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = null,
                tint = AnzoraNavyDeep
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.btn_checkout_whatsapp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun OrderConfirmationScreen(
    order: OrderEntity,
    onDoneViewHistory: () -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onDoneViewHistory)

    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val formattedMessage = remember(order) { AnzoraRepository.buildWhatsAppOrderMessage(order) }
    val whatsappUrl = remember(order) { AnzoraRepository.buildWhatsAppCheckoutUrl(order) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("order_confirmation_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = AnzoraNavyDeep)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(AnzoraElectricBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = AnzoraElectricBlue,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Text(
                    text = "Order Confirmed!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AnzoraWhite,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Order ID: ${order.orderNumber} • Status: ${order.status}",
                    style = MaterialTheme.typography.titleMedium,
                    color = AnzoraElectricBlue,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Total Payable on Cash on Delivery: ${AnzoraRepository.formatPkr(order.totalPkr)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AnzoraWhite.copy(alpha = 0.9f)
                )
            }
        }

        // Mandatory Explanation about sending the WhatsApp message
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = AnzoraWarning.copy(alpha = 0.15f)
            )
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Important WhatsApp Order Step",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Clicking the WhatsApp button opens a chat with ANZORA (+92 371 2047636) with your complete order summary prefilled. You MUST tap the 'Send' button inside WhatsApp so our dispatch team receives your WhatsApp confirmation message.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Prefilled WhatsApp Message Preview & Copy
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
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
                    Text(
                        text = "Prefilled WhatsApp Order Summary",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedButton(
                        onClick = {
                            clipboard.setText(AnnotatedString(formattedMessage))
                            onShowMessage("Order summary copied to clipboard!")
                        },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Text", style = MaterialTheme.typography.labelMedium)
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = formattedMessage,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = {
                        launchExternalUrl(context, whatsappUrl, onShowMessage)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("send_whatsapp_confirmation_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WhatsAppGreen,
                        contentColor = AnzoraNavyDeep
                    )
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Send Order on WhatsApp (+92 371 2047636)",
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Button(
                    onClick = onDoneViewHistory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("view_order_history_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AnzoraNavyDeep,
                        contentColor = AnzoraWhite
                    )
                ) {
                    Text("View My Order History & Status")
                }
            }
        }
    }
}

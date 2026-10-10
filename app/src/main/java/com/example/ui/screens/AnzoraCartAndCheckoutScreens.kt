package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.CartItem
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.StoreConfig
import com.example.data.model.UserProfile
import com.example.data.util.WhatsAppHelper
import com.example.ui.components.ProductVisualBanner
import com.example.ui.theme.AnzoraElectricBlue
import com.example.ui.theme.AnzoraError
import com.example.ui.theme.AnzoraNavyPrimary
import com.example.ui.theme.AnzoraSuccess
import com.example.ui.theme.AnzoraWhatsAppGreen
import com.example.ui.theme.AnzoraWhite

@Composable
fun AnzoraCartScreen(
    cartItems: List<CartItem>,
    storeConfig: StoreConfig,
    deliveryFeePkr: Long,
    onUpdateQuantity: (Product, Int) -> Unit,
    onRemoveItem: (Product) -> Unit,
    onContinueShopping: () -> Unit,
    onProceedToCheckout: () -> Unit
) {
    val subtotal = cartItems.sumOf { it.lineTotalPkr }
    val grandTotal = if (cartItems.isEmpty()) 0L else subtotal + deliveryFeePkr

    if (cartItems.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Empty Cart",
                        tint = AnzoraElectricBlue,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Your ANZORA Cart is Empty",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Explore our wireless earbuds, studio headphones, 65W GaN chargers, smartwatches, and ring lights.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onContinueShopping,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("empty_cart_browse_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AnzoraElectricBlue,
                            contentColor = AnzoraNavyPrimary
                        )
                    ) {
                        Text(
                            text = "Browse ANZORA Catalog",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Shopping Cart (${cartItems.sumOf { it.quantity }} items)",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        val remainingForFree = (storeConfig.freeDeliveryThresholdPkr - subtotal).coerceAtLeast(0L)
        Text(
            text = if (remainingForFree == 0L) {
                "You unlocked FREE Nationwide Shipping!"
            } else {
                "Add ${WhatsAppHelper.formatPkr(remainingForFree)} more to unlock FREE delivery."
            },
            style = MaterialTheme.typography.bodySmall,
            color = if (remainingForFree == 0L) AnzoraSuccess else AnzoraElectricBlue
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(cartItems, key = { it.product.id.ifBlank { it.product.name } }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProductVisualBanner(
                            imageKeyOrUrl = item.product.imageUrls.firstOrNull() ?: "preset:earbuds",
                            category = item.product.category,
                            contentDescription = item.product.name,
                            modifier = Modifier
                                .size(76.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.product.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${WhatsAppHelper.formatPkr(item.product.pricePkr)} each",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    IconButton(
                                        onClick = { onUpdateQuantity(item.product, item.quantity - 1) },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Remove,
                                            contentDescription = "Decrease quantity",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = item.quantity.toString(),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                    IconButton(
                                        onClick = { onUpdateQuantity(item.product, item.quantity + 1) },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Increase quantity",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = WhatsAppHelper.formatPkr(item.lineTotalPkr),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AnzoraElectricBlue
                                )
                            }
                        }
                        IconButton(
                            onClick = { onRemoveItem(item.product) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Remove from cart",
                                tint = AnzoraError
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Subtotal", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        WhatsAppHelper.formatPkr(subtotal),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Delivery Charges", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        if (deliveryFeePkr == 0L) "FREE" else WhatsAppHelper.formatPkr(deliveryFeePkr),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (deliveryFeePkr == 0L) AnzoraSuccess else MaterialTheme.colorScheme.onSurface
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Total Payable (COD)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        WhatsAppHelper.formatPkr(grandTotal),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AnzoraElectricBlue
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = onProceedToCheckout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("proceed_to_checkout_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AnzoraElectricBlue,
                        contentColor = AnzoraNavyPrimary
                    )
                ) {
                    Icon(Icons.Default.ShoppingCartCheckout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Proceed to Checkout (${WhatsAppHelper.formatPkr(grandTotal)})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AnzoraCheckoutScreen(
    cartItems: List<CartItem>,
    storeConfig: StoreConfig,
    deliveryFeePkr: Long,
    savedProfile: UserProfile?,
    isSubmitting: Boolean,
    onBackToCart: () -> Unit,
    onPlaceOrder: (
        customerName: String,
        customerPhone: String,
        customerCity: String,
        customerAddress: String,
        orderNotes: String,
        paymentMethod: String,
        saveProfile: Boolean,
        launchWhatsAppAfterOrder: Boolean
    ) -> Unit
) {
    BackHandler { onBackToCart() }

    var customerName by remember { mutableStateOf(savedProfile?.fullName ?: "") }
    var customerPhone by remember { mutableStateOf(savedProfile?.phone ?: "+92") }
    var customerCity by remember { mutableStateOf(savedProfile?.city ?: "") }
    var customerAddress by remember { mutableStateOf(savedProfile?.address ?: "") }
    var orderNotes by remember { mutableStateOf("") }
    var saveToProfile by remember { mutableStateOf(true) }
    var validationError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(savedProfile) {
        if (savedProfile != null) {
            if (customerName.isBlank()) customerName = savedProfile.fullName
            if (customerPhone == "+92" || customerPhone.isBlank()) customerPhone = savedProfile.phone
            if (customerCity.isBlank()) customerCity = savedProfile.city
            if (customerAddress.isBlank()) customerAddress = savedProfile.address
        }
    }

    val subtotal = cartItems.sumOf { it.lineTotalPkr }
    val grandTotal = subtotal + deliveryFeePkr

    fun validateInputs(): Boolean {
        val cleanPhone = customerPhone.trim().replace(" ", "").replace("-", "")
        val pkPhoneRegex = "^(\\+92|0)?3[0-9]{9}$".toRegex()
        if (customerName.trim().length < 2) {
            validationError = "Please enter your full recipient name (at least 2 characters)."
            return false
        }
        if (!pkPhoneRegex.matches(cleanPhone)) {
            validationError = "Please enter a valid Pakistani mobile number (e.g. +923001234567 or 03001234567)."
            return false
        }
        if (customerCity.trim().length < 2) {
            validationError = "Please enter your city in Pakistan (e.g. Lahore, Karachi, Islamabad)."
            return false
        }
        if (customerAddress.trim().length < 8) {
            validationError = "Please enter your complete house/street delivery address (at least 8 characters)."
            return false
        }
        validationError = null
        return true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBackToCart) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to cart")
            }
            Text(
                text = "Cash on Delivery Checkout",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Delivery Details Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "1. Customer & Delivery Address",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Recipient Full Name *") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("checkout_name_input")
                )

                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { customerPhone = it },
                    label = { Text("Pakistani Mobile Number (WhatsApp / Call) *") },
                    placeholder = { Text("+923001234567 or 03001234567") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("checkout_phone_input")
                )

                OutlinedTextField(
                    value = customerCity,
                    onValueChange = { customerCity = it },
                    label = { Text("City (Pakistan) *") },
                    placeholder = { Text("Lahore, Karachi, Islamabad, Rawalpindi, Faisalabad...") },
                    leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("checkout_city_input")
                )

                OutlinedTextField(
                    value = customerAddress,
                    onValueChange = { customerAddress = it },
                    label = { Text("Complete Delivery Address *") },
                    placeholder = { Text("House / Flat No, Street, Block / Sector, Famous Landmark") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("checkout_address_input")
                )

                OutlinedTextField(
                    value = orderNotes,
                    onValueChange = { orderNotes = it },
                    label = { Text("Order Notes (Optional)") },
                    placeholder = { Text("Preferred delivery timing or landmark instructions") },
                    minLines = 2,
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("checkout_notes_input")
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = saveToProfile,
                        onCheckedChange = { saveToProfile = it }
                    )
                    Text(
                        text = "Save delivery details to my private ANZORA account for faster checkout",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Payment Method & Order Summary
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "2. Payment Method & Order Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = AnzoraElectricBlue.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AnzoraElectricBlue, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = AnzoraElectricBlue
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Cash on Delivery (COD) — PKR",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Pay cash in Pakistani Rupees when our courier delivers your sealed ANZORA package.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                cartItems.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${item.quantity}x ${item.product.name}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = WhatsAppHelper.formatPkr(item.lineTotalPkr),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Subtotal", style = MaterialTheme.typography.bodyMedium)
                    Text(WhatsAppHelper.formatPkr(subtotal), style = MaterialTheme.typography.bodyMedium)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Delivery Charges", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        if (deliveryFeePkr == 0L) "FREE" else WhatsAppHelper.formatPkr(deliveryFeePkr),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Total COD Payable",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        WhatsAppHelper.formatPkr(grandTotal),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AnzoraElectricBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Important WhatsApp Notice
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = AnzoraWhatsAppGreen.copy(alpha = 0.12f)
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "WhatsApp Notice",
                    tint = AnzoraWhatsAppGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "WhatsApp Ordering Note: When choosing 'Order & Send via WhatsApp' (+923712047636), your order is saved in our system AND WhatsApp opens with a prefilled summary. You must tap the Send button inside WhatsApp for the ANZORA team to receive your WhatsApp message.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (validationError != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = AnzoraError.copy(alpha = 0.15f)
                )
            ) {
                Text(
                    text = validationError ?: "",
                    color = AnzoraError,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button 1: Standard Cash on Delivery Checkout
        Button(
            onClick = {
                if (validateInputs()) {
                    onPlaceOrder(
                        customerName,
                        customerPhone,
                        customerCity,
                        customerAddress,
                        orderNotes,
                        "COD",
                        saveToProfile,
                        false
                    )
                }
            },
            enabled = !isSubmitting,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("confirm_cod_order_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AnzoraElectricBlue,
                contentColor = AnzoraNavyPrimary
            )
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = AnzoraNavyPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(Icons.Default.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Place Cash on Delivery Order (${WhatsAppHelper.formatPkr(grandTotal)})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Button 2: WhatsApp Checkout with Prefilled Order Summary
        Button(
            onClick = {
                if (validateInputs()) {
                    onPlaceOrder(
                        customerName,
                        customerPhone,
                        customerCity,
                        customerAddress,
                        orderNotes,
                        "COD_WHATSAPP",
                        saveToProfile,
                        true
                    )
                }
            },
            enabled = !isSubmitting,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("confirm_whatsapp_order_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AnzoraWhatsAppGreen,
                contentColor = AnzoraNavyPrimary
            )
        ) {
            Text(
                text = "Place Order & Send Summary on WhatsApp",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun AnzoraOrderSuccessDialog(
    order: Order,
    whatsappNumber: String,
    onDismissAndViewOrders: () -> Unit,
    onSendWhatsApp: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Order Confirmed",
                tint = AnzoraSuccess,
                modifier = Modifier.size(54.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Order Placed Successfully!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Order Reference: #${order.id.takeLast(6).uppercase()} • Total COD: ${WhatsAppHelper.formatPkr(order.totalPkr)}",
                style = MaterialTheme.typography.titleSmall,
                color = AnzoraElectricBlue,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Important Reminder: If you want instant confirmation on WhatsApp ($whatsappNumber), tap the button below and press SEND inside WhatsApp so our dispatch team receives your message.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = {
                    val msg = WhatsAppHelper.buildWhatsAppOrderMessageFromOrder(order)
                    val url = WhatsAppHelper.buildWhatsAppUrl(whatsappNumber, msg)
                    onSendWhatsApp(url)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AnzoraWhatsAppGreen,
                    contentColor = AnzoraNavyPrimary
                )
            ) {
                Text(
                    text = "Send Order Summary on WhatsApp",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onDismissAndViewOrders,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "View My Order History",
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import com.example.data.model.Order
import com.example.data.model.StoreConfig
import com.example.data.model.UserProfile
import com.example.data.util.WhatsAppHelper
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
fun AnzoraOrdersAndAccountScreen(
    userEmail: String,
    userDisplayName: String,
    isAdmin: Boolean,
    ordersState: UiState<List<Order>>,
    savedProfile: UserProfile?,
    storeConfig: StoreConfig,
    onSaveProfile: (String, String, String, String) -> Unit,
    onCancelOrder: (String) -> Unit,
    onResendWhatsApp: (String) -> Unit,
    onSignOut: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = My Orders, 1 = Delivery Profile, 2 = Legal & Policies

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Account Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(AnzoraElectricBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User",
                            tint = AnzoraElectricBlue
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = userDisplayName.ifBlank { "ANZORA Customer" },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (isAdmin) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = AnzoraElectricBlue,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "ADMIN",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AnzoraNavyPrimary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = userEmail,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                OutlinedButton(
                    onClick = onSignOut,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("sign_out_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Sign Out",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sign Out", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sub-navigation chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                label = { Text("Order History") },
                leadingIcon = {
                    Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AnzoraElectricBlue,
                    selectedLabelColor = AnzoraNavyPrimary,
                    selectedLeadingIconColor = AnzoraNavyPrimary
                )
            )
            FilterChip(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                label = { Text("Saved Delivery Address") },
                leadingIcon = {
                    Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(16.dp))
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AnzoraElectricBlue,
                    selectedLabelColor = AnzoraNavyPrimary,
                    selectedLeadingIconColor = AnzoraNavyPrimary
                )
            )
            FilterChip(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                label = { Text("About, Contact & Policies") },
                leadingIcon = {
                    Icon(Icons.Default.Policy, contentDescription = null, modifier = Modifier.size(16.dp))
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AnzoraElectricBlue,
                    selectedLabelColor = AnzoraNavyPrimary,
                    selectedLeadingIconColor = AnzoraNavyPrimary
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            0 -> CustomerOrderHistorySubScreen(
                ordersState = ordersState,
                whatsappNumber = storeConfig.whatsappNumber,
                onCancelOrder = onCancelOrder,
                onResendWhatsApp = onResendWhatsApp
            )
            1 -> SavedAddressProfileSubScreen(
                savedProfile = savedProfile,
                onSaveProfile = onSaveProfile
            )
            2 -> AnzoraPoliciesSubScreen(
                storeConfig = storeConfig,
                onOpenWhatsApp = onResendWhatsApp
            )
        }
    }
}

@Composable
private fun CustomerOrderHistorySubScreen(
    ordersState: UiState<List<Order>>,
    whatsappNumber: String,
    onCancelOrder: (String) -> Unit,
    onResendWhatsApp: (String) -> Unit
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
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = "No orders",
                            tint = AnzoraElectricBlue,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No Orders Placed Yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Your ANZORA Cash on Delivery and WhatsApp orders will appear here with real-time status updates.",
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
                                    Text(
                                        text = "Order #${order.id.takeLast(6).uppercase()}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Surface(
                                        color = statusColor.copy(alpha = 0.16f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = order.status,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = statusColor,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                val formattedDate = order.createdAt?.toDate()?.let { dateFormat.format(it) } ?: "Just now"
                                Text(
                                    text = "$formattedDate • ${order.paymentMethod}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                                Text(
                                    text = order.itemsSummary,
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Text(
                                    text = "Deliver to: ${order.customerName} (${order.customerPhone}) — ${order.customerAddress}, ${order.customerCity}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Total COD: ${WhatsAppHelper.formatPkr(order.totalPkr)}",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AnzoraElectricBlue
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        if (order.status == "Pending") {
                                            TextButton(
                                                onClick = { onCancelOrder(order.id) }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Cancel,
                                                    contentDescription = "Cancel Order",
                                                    tint = AnzoraError,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Cancel", color = AnzoraError)
                                            }
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                val msg = WhatsAppHelper.buildWhatsAppOrderMessageFromOrder(order)
                                                onResendWhatsApp(
                                                    WhatsAppHelper.buildWhatsAppUrl(whatsappNumber, msg)
                                                )
                                            },
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = "Send on WhatsApp",
                                                tint = AnzoraWhatsAppGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("WhatsApp", style = MaterialTheme.typography.labelMedium)
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
}

@Composable
private fun SavedAddressProfileSubScreen(
    savedProfile: UserProfile?,
    onSaveProfile: (String, String, String, String) -> Unit
) {
    var fullName by remember { mutableStateOf(savedProfile?.fullName ?: "") }
    var phone by remember { mutableStateOf(savedProfile?.phone ?: "+92") }
    var city by remember { mutableStateOf(savedProfile?.city ?: "") }
    var address by remember { mutableStateOf(savedProfile?.address ?: "") }

    LaunchedEffect(savedProfile) {
        if (savedProfile != null) {
            fullName = savedProfile.fullName
            phone = savedProfile.phone
            city = savedProfile.city
            address = savedProfile.address
        }
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
            Text(
                text = "Private Saved Delivery Address",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Your contact and delivery details are encrypted and isolated to your personal account via Firestore security rules.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Recipient Full Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Pakistani Mobile Number") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Complete Street Address") },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { onSaveProfile(fullName, phone, city, address) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AnzoraElectricBlue,
                    contentColor = AnzoraNavyPrimary
                )
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save Delivery Profile",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AnzoraPoliciesSubScreen(
    storeConfig: StoreConfig,
    onOpenWhatsApp: (String) -> Unit
) {
    var selectedPolicy by remember { mutableIntStateOf(0) }
    val policyTitles = listOf(
        "About Us",
        "Contact Us",
        "Shipping Policy",
        "Returns Policy",
        "Privacy Policy",
        "Terms of Service"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            policyTitles.forEachIndexed { index, title ->
                FilterChip(
                    selected = selectedPolicy == index,
                    onClick = { selectedPolicy = index },
                    label = { Text(title) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AnzoraElectricBlue.copy(alpha = 0.22f),
                        selectedLabelColor = AnzoraElectricBlue
                    )
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                when (selectedPolicy) {
                    0 -> {
                        Text("About ANZORA Pakistan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(
                            "ANZORA is a premier Pakistani electronics accessories brand dedicated to bringing audiophile wireless earbuds, studio-grade headphones, tangle-free handsfree, 100W PD braided cables, 65W GaN fast chargers, AMOLED smartwatches, creator ring lights, and everyday mobile accessories to customers nationwide.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Every ANZORA product undergoes multi-point quality inspection for acoustic clarity, voltage stability on Pakistani power grids, and long-term durability before dispatch.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    1 -> {
                        Text("Contact ANZORA Support", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(
                            "• Official WhatsApp Helpline & Ordering: ${storeConfig.whatsappNumber}\n" +
                                "• Customer Support Email: ${storeConfig.supportEmail}\n" +
                                "• Operating Hours: Monday – Saturday, 10:00 AM to 8:00 PM (PKT)\n" +
                                "• Operations: Nationwide Courier Dispatch across Pakistan",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(
                            onClick = {
                                val msg = "Assalam-o-Alaikum ANZORA Support! I need assistance regarding a product or order."
                                onOpenWhatsApp(
                                    WhatsAppHelper.buildWhatsAppUrl(storeConfig.whatsappNumber, msg)
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AnzoraWhatsAppGreen,
                                contentColor = AnzoraNavyPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.SupportAgent, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Chat on WhatsApp (${storeConfig.whatsappNumber})", fontWeight = FontWeight.Bold)
                        }
                    }
                    2 -> {
                        Text("Shipping & Delivery Policy", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(
                            "• Payment Method: 100% Cash on Delivery (COD) in Pakistani Rupees (PKR).\n" +
                                "• Standard Delivery Fee: ${WhatsAppHelper.formatPkr(storeConfig.deliveryFeePkr)} nationwide.\n" +
                                "• Free Delivery Threshold: Orders of ${WhatsAppHelper.formatPkr(storeConfig.freeDeliveryThresholdPkr)} or above qualify for FREE nationwide shipping.\n" +
                                "• Estimated Transit Time: 2–3 working days for major cities (Karachi, Lahore, Islamabad, Rawalpindi, Faisalabad, Multan, Peshawar) and 3–5 working days for other regions.\n" +
                                "• Order Verification: Orders may be confirmed via phone call or WhatsApp (+923712047636) prior to courier dispatch.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    3 -> {
                        Text("7-Day Replacement & Returns Policy", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(
                            "• 7-Day Checking Warranty: If you receive a damaged, defective, or incorrect accessory, notify us on WhatsApp (${storeConfig.whatsappNumber}) within 7 days of delivery with an unboxing photo/video.\n" +
                                "• Eligibility: Items must be returned in original ANZORA retail packaging with all included cables, manuals, and accessories intact.\n" +
                                "• Resolution: Defective items are replaced free of cost or refunded via bank transfer / EasyPaisa / JazzCash after inspection.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    4 -> {
                        Text("Privacy Policy", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(
                            "• Data Collection: We collect only your recipient name, Pakistani mobile number, city, and delivery address strictly to fulfill and deliver your Cash on Delivery orders.\n" +
                                "• Zero-Trust Isolation: Customer profiles and order histories are stored in Google Cloud Firestore with strict per-user security rules so no other customer can view your personal details.\n" +
                                "• Third-Party Sharing: Your phone number and delivery address are shared solely with our licensed courier partner for doorstep parcel delivery.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    5 -> {
                        Text("Terms & Conditions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(
                            "• All prices are listed in Pakistani Rupees (PKR) and validated against our backend catalog.\n" +
                                "• Cash on Delivery orders must be paid in full to the courier rider upon parcel handover.\n" +
                                "• When using WhatsApp Checkout, customers must tap Send in WhatsApp so the prefilled order message is transmitted to ${storeConfig.whatsappNumber}.\n" +
                                "• ANZORA reserves the right to cancel fraudulent or unverified COD orders.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.data.local.CustomerAccountEntity
import com.example.data.local.OrderEntity
import com.example.data.repository.AnzoraRepository
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
fun CustomerAccountAndOrdersScreen(
    loggedInCustomer: CustomerAccountEntity?,
    customerOrders: List<OrderEntity>,
    guestLookupPhone: String,
    onSetGuestLookupPhone: (String) -> Unit,
    onRegisterCustomer: (
        fullName: String,
        phone: String,
        email: String,
        city: String,
        completeAddress: String,
        passwordPlain: String
    ) -> Unit,
    onLoginCustomer: (emailOrPhone: String, passwordPlain: String) -> Unit,
    onUpdateProfile: (
        account: CustomerAccountEntity,
        fullName: String,
        phone: String,
        city: String,
        completeAddress: String
    ) -> Unit,
    onLogoutCustomer: () -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isRegisterMode by remember { mutableStateOf(false) }

    // Auth form state
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Karachi") }
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Guest lookup input
    var lookupPhoneInput by remember(guestLookupPhone) { mutableStateOf(guestLookupPhone) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("customer_account_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Account Section (Signed In vs Sign In / Register)
        item {
            if (loggedInCustomer != null) {
                var editName by remember(loggedInCustomer) { mutableStateOf(loggedInCustomer.fullName) }
                var editPhone by remember(loggedInCustomer) { mutableStateOf(loggedInCustomer.phone) }
                var editCity by remember(loggedInCustomer) { mutableStateOf(loggedInCustomer.city) }
                var editAddress by remember(loggedInCustomer) { mutableStateOf(loggedInCustomer.completeAddress) }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(AnzoraNavyDeep),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.AccountCircle,
                                        contentDescription = null,
                                        tint = AnzoraElectricBlue,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = loggedInCustomer.fullName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = loggedInCustomer.email,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = onLogoutCustomer,
                                modifier = Modifier.testTag("customer_logout_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sign Out")
                            }
                        }

                        HorizontalDivider()

                        Text(
                            text = "Saved Delivery Address (Auto-fills Checkout)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Full Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = editPhone,
                                onValueChange = { editPhone = it },
                                label = { Text("Mobile Number") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = editCity,
                                onValueChange = { editCity = it },
                                label = { Text("City") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        OutlinedTextField(
                            value = editAddress,
                            onValueChange = { editAddress = it },
                            label = { Text("Complete Delivery Address") },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                onUpdateProfile(loggedInCustomer, editName, editPhone, editCity, editAddress)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("save_customer_profile_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AnzoraNavyDeep,
                                contentColor = AnzoraWhite
                            )
                        ) {
                            Icon(Icons.Filled.Save, contentDescription = null, tint = AnzoraElectricBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save Delivery Profile")
                        }
                    }
                }
            } else {
                // Sign-In or Create Account Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isRegisterMode) "Create ANZORA Customer Account" else "Customer Account Sign In",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Save your Pakistan delivery address & track all COD orders.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = !isRegisterMode,
                                onClick = { isRegisterMode = false },
                                label = { Text("Sign In") },
                                leadingIcon = {
                                    Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            )
                            FilterChip(
                                selected = isRegisterMode,
                                onClick = { isRegisterMode = true },
                                label = { Text("Create Account") },
                                leadingIcon = {
                                    Icon(Icons.Filled.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            )
                        }

                        if (isRegisterMode) {
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text("Full Name *") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_name_input")
                            )
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("Pakistani Mobile Number (03XX-XXXXXXX) *") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_phone_input")
                            )
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email Address *") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_email_input")
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = city,
                                    onValueChange = { city = it },
                                    label = { Text("City *") },
                                    singleLine = true,
                                    modifier = Modifier.weight(0.4f)
                                )
                                OutlinedTextField(
                                    value = address,
                                    onValueChange = { address = it },
                                    label = { Text("Complete Address *") },
                                    singleLine = true,
                                    modifier = Modifier.weight(0.6f)
                                )
                            }
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Create Password (min 6 chars) *") },
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reg_password_input")
                            )

                            Button(
                                onClick = {
                                    onRegisterCustomer(fullName, phone, email, city, address, password)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("register_customer_submit"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AnzoraNavyDeep,
                                    contentColor = AnzoraWhite
                                )
                            ) {
                                Text("Create Customer Account")
                            }
                        } else {
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email or Pakistani Mobile Number") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_email_phone_input")
                            )
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("login_password_input")
                            )
                            Button(
                                onClick = {
                                    onLoginCustomer(email, password)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("login_customer_submit"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AnzoraNavyDeep,
                                    contentColor = AnzoraWhite
                                )
                            ) {
                                Text("Sign In to ANZORA Account")
                            }
                        }
                    }
                }
            }
        }

        // 2. Quick Order Lookup by Phone Number (for guests or quick verification)
        if (loggedInCustomer == null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Track Guest COD Order by Mobile Number",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = lookupPhoneInput,
                                onValueChange = { lookupPhoneInput = it },
                                placeholder = { Text("Enter 03XX-XXXXXXX") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("guest_lookup_phone_input")
                            )
                            Button(
                                onClick = { onSetGuestLookupPhone(lookupPhoneInput) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AnzoraNavyDeep,
                                    contentColor = AnzoraWhite
                                ),
                                modifier = Modifier
                                    .height(52.dp)
                                    .testTag("guest_lookup_button")
                            ) {
                                Icon(Icons.Filled.Search, contentDescription = null, tint = AnzoraElectricBlue)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Find")
                            }
                        }
                    }
                }
            }
        }

        // 3. Order History Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.History,
                    contentDescription = null,
                    tint = AnzoraElectricBlueDark
                )
                Text(
                    text = "My Order History (${customerOrders.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        if (customerOrders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ReceiptLong,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "No orders found for this account / phone number yet.",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "When you place a Cash on Delivery or WhatsApp order, it will appear here with live status updates.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(customerOrders, key = { it.orderNumber }) { order ->
                CustomerOrderCard(
                    order = order,
                    onResendWhatsApp = {
                        val waUrl = AnzoraRepository.buildWhatsAppCheckoutUrl(order)
                        launchExternalUrl(context, waUrl, onShowMessage)
                    }
                )
            }
        }
    }
}

@Composable
private fun CustomerOrderCard(
    order: OrderEntity,
    onResendWhatsApp: () -> Unit
) {
    val statusColor = when (order.status) {
        "Delivered" -> AnzoraSuccess
        "Cancelled" -> AnzoraError
        "Dispatched", "Packed", "Confirmed" -> AnzoraElectricBlueDark
        else -> AnzoraWarning
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("order_history_card_${order.orderNumber}"),
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
                        text = order.orderNumber,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = AnzoraRepository.formatTimestamp(order.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = statusColor.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = order.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = statusColor,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            HorizontalDivider()

            order.parsedItems.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${item.quantity}x ${item.title}",
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
                Text(
                    text = "Delivery to: ${order.customerCity} (${order.paymentMethod})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Total: ${AnzoraRepository.formatPkr(order.totalPkr)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = AnzoraElectricBlueDark
                )
            }

            OutlinedButton(
                onClick = onResendWhatsApp,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = null,
                    tint = WhatsAppGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send / Follow Up on WhatsApp (+92 371 2047636)")
            }
        }
    }
}

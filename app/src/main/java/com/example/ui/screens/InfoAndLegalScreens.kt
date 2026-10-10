package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.StoreSettingsEntity
import com.example.data.repository.AnzoraRepository
import com.example.ui.LegalPolicyTopic
import com.example.ui.components.launchExternalUrl
import com.example.ui.theme.AnzoraElectricBlue
import com.example.ui.theme.AnzoraElectricBlueDark
import com.example.ui.theme.AnzoraNavyDeep
import com.example.ui.theme.AnzoraNavySurface
import com.example.ui.theme.AnzoraWhite
import com.example.ui.theme.WhatsAppGreen
import java.net.URLEncoder

@Composable
fun InfoAndLegalHubScreen(
    storeSettings: StoreSettingsEntity,
    activeTopic: LegalPolicyTopic?,
    onOpenTopic: (LegalPolicyTopic) -> Unit,
    onCloseTopic: () -> Unit,
    onOpenAdminPortal: () -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (activeTopic != null) {
        LegalTopicDetailScreen(
            topic = activeTopic,
            storeSettings = storeSettings,
            onBack = onCloseTopic,
            onShowMessage = onShowMessage,
            modifier = modifier
        )
        return
    }

    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("info_and_legal_hub_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Protected Admin Dashboard Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AnzoraNavyDeep)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AdminPanelSettings,
                            contentDescription = null,
                            tint = AnzoraElectricBlue,
                            modifier = Modifier.size(30.dp)
                        )
                        Column {
                            Text(
                                text = "ANZORA Protected Admin Dashboard",
                                style = MaterialTheme.typography.titleMedium,
                                color = AnzoraWhite,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Manage products, categories, PKR prices, photos, stock, delivery fees & COD orders.",
                                style = MaterialTheme.typography.bodySmall,
                                color = AnzoraWhite.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Button(
                        onClick = onOpenAdminPortal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("open_admin_dashboard_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AnzoraElectricBlue,
                            contentColor = AnzoraNavyDeep
                        )
                    ) {
                        Text(
                            text = "Open Protected Admin Console",
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        // 2. Direct WhatsApp Support Banner
        item {
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
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SupportAgent,
                            contentDescription = null,
                            tint = AnzoraElectricBlueDark
                        )
                        Column {
                            Text(
                                text = "ANZORA Pakistan Official Support",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "WhatsApp: +92 371 2047636 • Email: ${storeSettings.supportEmail}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val msg = "Assalam-o-Alaikum ANZORA Support! I have a question regarding your electronics accessories."
                            val url = "https://wa.me/${AnzoraRepository.OFFICIAL_WHATSAPP_DIGITS}?text=${URLEncoder.encode(msg, "UTF-8")}"
                            launchExternalUrl(context, url, onShowMessage)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WhatsAppGreen,
                            contentColor = AnzoraNavyDeep
                        )
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Chat with ANZORA on WhatsApp (+92 371 2047636)", fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }

        // 3. Store Information & Policies Directory
        item {
            Text(
                text = "Brand Pages, Policies & Technical Architecture",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }

        items(LegalPolicyTopic.entries) { topic ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenTopic(topic) }
                    .testTag("policy_card_${topic.name}"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = when (topic) {
                                LegalPolicyTopic.SHIPPING_POLICY -> Icons.Filled.LocalShipping
                                LegalPolicyTopic.PLATFORM_ARCHITECTURE -> Icons.Filled.Code
                                LegalPolicyTopic.PRIVACY_POLICY, LegalPolicyTopic.TERMS_CONDITIONS -> Icons.Filled.Policy
                                else -> Icons.Filled.Description
                            },
                            contentDescription = null,
                            tint = AnzoraElectricBlueDark
                        )
                        Column {
                            Text(
                                text = topic.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = topic.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = AnzoraElectricBlueDark
                    )
                }
            }
        }
    }
}

@Composable
private fun LegalTopicDetailScreen(
    topic: LegalPolicyTopic,
    storeSettings: StoreSettingsEntity,
    onBack: () -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("policy_detail_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OutlinedButton(
            onClick = onBack,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("policy_back_button")
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            Spacer(modifier = Modifier.width(6.dp))
            Text("Back to Info & Policies")
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AnzoraNavyDeep)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = topic.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = AnzoraWhite,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = topic.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AnzoraElectricBlue
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (topic) {
                    LegalPolicyTopic.ABOUT_US -> {
                        PolicySection(
                            heading = "Our Mission in Pakistan",
                            body = "ANZORA is a premier Pakistani electronics accessories brand dedicated to delivering studio-grade wireless audio, Gallium Nitride (GaN) high-speed charging solutions, AMOLED smartwatches, and creator lighting gear at honest PKR prices."
                        )
                        PolicySection(
                            heading = "Engineered for Local Conditions",
                            body = "Every ANZORA charger, braided cable, and wireless audio product is tested for Pakistani power grid fluctuations (100V–240V surge resilience), summer thermal stability, and crystal-clear Environmental Noise Cancellation (ENC) for busy urban environments."
                        )
                        PolicySection(
                            heading = "100% Authentic & Backed by Official Warranty",
                            body = "We stand behind every accessory we ship with transparent stock availability, nationwide Cash on Delivery (COD), and direct WhatsApp customer care."
                        )
                    }

                    LegalPolicyTopic.CONTACT_US -> {
                        PolicySection(
                            heading = "Official WhatsApp Ordering & Support Desk",
                            body = "Phone / WhatsApp: +92 371 2047636\nOperating Hours: Monday to Saturday, 10:00 AM – 9:00 PM (PKT)"
                        )
                        PolicySection(
                            heading = "Customer Support Email",
                            body = storeSettings.supportEmail
                        )
                        PolicySection(
                            heading = "Operations & Dispatch Hubs",
                            body = storeSettings.headquartersAddress
                        )
                        Button(
                            onClick = {
                                val url = "https://wa.me/${AnzoraRepository.OFFICIAL_WHATSAPP_DIGITS}"
                                launchExternalUrl(context, url, onShowMessage)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = WhatsAppGreen,
                                contentColor = AnzoraNavyDeep
                            )
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open WhatsApp Chat (+92 371 2047636)", fontWeight = FontWeight.Bold)
                        }
                    }

                    LegalPolicyTopic.SHIPPING_POLICY -> {
                        PolicySection(
                            heading = "Nationwide Cash on Delivery (COD)",
                            body = "ANZORA ships to over 150+ cities and towns across Pakistan via trusted courier partners (TCS, Leopards Courier, PostEx, and CallCourier). Customers pay in PKR cash at the time of doorstep delivery."
                        )
                        PolicySection(
                            heading = "Delivery Charges (Admin Configurable)",
                            body = "• Standard Nationwide Delivery Fee: ${AnzoraRepository.formatPkr(storeSettings.standardDeliveryFeePkr)}\n" +
                                if (storeSettings.enableFreeDeliveryThreshold) {
                                    "• Free Nationwide Shipping on all orders of ${AnzoraRepository.formatPkr(storeSettings.freeDeliveryThresholdPkr)} and above."
                                } else {
                                    "• Flat-rate delivery applies to all orders."
                                }
                        )
                        PolicySection(
                            heading = "Dispatch & Transit Timelines",
                            body = "• Major Cities (Karachi, Lahore, Islamabad, Rawalpindi, Faisalabad): 2 to 3 working days.\n" +
                                "• Secondary Cities & Regional Towns: 3 to 5 working days.\n" +
                                "• Orders placed before 3:00 PM PKT are dispatched the same business day."
                        )
                    }

                    LegalPolicyTopic.RETURNS_POLICY -> {
                        PolicySection(
                            heading = "7-Day Easy Replacement Guarantee",
                            body = "If you receive a damaged, defective, or incorrect electronics accessory, notify us on WhatsApp (+92 371 2047636) within 7 days of delivery with your Order ID and unboxing video/photo for an immediate free replacement."
                        )
                        PolicySection(
                            heading = "Official Warranty Coverage",
                            body = "Covered products include a 6-month to 1-year official ANZORA replacement warranty against manufacturing defects in drivers, PCB circuitry, and charging ports. Physical damage, water submersion beyond IP rating, or cut cables are excluded."
                        )
                    }

                    LegalPolicyTopic.PRIVACY_POLICY -> {
                        PolicySection(
                            heading = "Customer Data Minimization & Privacy",
                            body = "ANZORA collects only the information strictly required to fulfill and deliver your order: Full Name, Pakistani Mobile Number, City, Complete Street Address, and optional Delivery Notes."
                        )
                        PolicySection(
                            heading = "Strict Access Isolation",
                            body = "Customer contact details and order records are kept strictly private. Customers can only view their own orders, and administrative order management is restricted to authenticated ANZORA administrators."
                        )
                    }

                    LegalPolicyTopic.TERMS_CONDITIONS -> {
                        PolicySection(
                            heading = "Pricing & Stock Verification",
                            body = "All prices are listed in Pakistani Rupees (PKR). Every order undergoes atomic backend validation to verify product price integrity and live stock availability before confirmation."
                        )
                        PolicySection(
                            heading = "WhatsApp Checkout Confirmation",
                            body = "When choosing 'Order via WhatsApp', the app generates a URL-encoded summary for +92 371 2047636. Customers must tap 'Send' inside WhatsApp so our fulfillment team receives the WhatsApp message."
                        )
                    }

                    LegalPolicyTopic.PLATFORM_ARCHITECTURE -> {
                        PolicySection(
                            heading = "1. Shared Backend & Security Rules (firestore.rules & storage.rules)",
                            body = "The workspace root includes complete `firestore.rules`, `storage.rules`, `firebase-blueprint.json`, and `ARCHITECTURE_AND_SETUP.md` so the Web storefront (or Capacitor hybrid build) and this native Android application share the exact same `/products`, `/categories`, `/orders`, and `/settings/store` collections."
                        )
                        PolicySection(
                            heading = "2. Server-Side Price & Stock Validation",
                            body = "Both the Android repository transaction (`AnzoraRepository.placeOrder`) and the Firestore Security Rules enforce positive PKR pricing, non-negative stock deductions, and immutable customer order totals to prevent unauthorized order modifications."
                        )
                        PolicySection(
                            heading = "3. Connecting Live Firebase or Exporting APK",
                            body = "To switch from the local Room persistence engine to live Cloud Firestore:\n" +
                                "1. Enable Firebase Authentication, Cloud Firestore, and Firebase Storage in your Firebase Console.\n" +
                                "2. Deploy `/firestore.rules` and `/storage.rules` via `firebase deploy --only firestore:rules,storage`.\n" +
                                "3. Set custom admin claim `{\"admin\": true}` on your admin UID using Firebase Admin SDK.\n" +
                                "4. Generate your signed APK/AAB directly from the AI Studio Settings / Export menu or run `gradle assembleDebug`."
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PolicySection(heading: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = heading,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AnzoraElectricBlueDark
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
}

package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material.icons.filled.Earbuds
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.SettingsInputComponent
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.filled.WbIridescent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.ProductEntity
import com.example.ui.theme.AnzoraElectricBlue
import com.example.ui.theme.AnzoraError
import com.example.ui.theme.AnzoraNavyDeep
import com.example.ui.theme.AnzoraNavySurface
import com.example.ui.theme.AnzoraSuccess
import com.example.ui.theme.AnzoraWarning
import com.example.ui.theme.AnzoraWhite

@Composable
fun AnzoraProductVisual(
    product: ProductEntity,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val customUri = product.customImageUri
    if (!customUri.isNullOrBlank()) {
        AsyncImage(
            model = customUri,
            contentDescription = product.title,
            modifier = modifier,
            contentScale = contentScale,
            error = painterResource(id = resolveDrawableRes(product.primaryImageResName)),
            placeholder = painterResource(id = resolveDrawableRes(product.primaryImageResName))
        )
    } else {
        Image(
            painter = painterResource(id = resolveDrawableRes(product.primaryImageResName)),
            contentDescription = product.title,
            modifier = modifier,
            contentScale = contentScale
        )
    }
}

fun resolveDrawableRes(resName: String): Int {
    return when (resName) {
        "img_hero_banner" -> R.drawable.img_hero_banner_1791591491584
        "img_cat_audio" -> R.drawable.img_cat_audio_1791591505169
        "img_cat_charging" -> R.drawable.img_cat_charging_1791591519051
        "img_cat_wearables" -> R.drawable.img_cat_wearables_1791591535850
        else -> R.drawable.img_cat_audio_1791591505169
    }
}

fun categoryIconFor(iconName: String): ImageVector {
    return when (iconName) {
        "earbuds" -> Icons.Filled.Earbuds
        "headphones" -> Icons.Filled.Headphones
        "settings_input_component" -> Icons.Filled.SettingsInputComponent
        "cable" -> Icons.Filled.Cable
        "power" -> Icons.Filled.Power
        "watch" -> Icons.Filled.Watch
        "wb_iridescent" -> Icons.Filled.WbIridescent
        else -> Icons.Filled.DevicesOther
    }
}

@Composable
fun StockAvailabilityBadge(stockQuantity: Int, modifier: Modifier = Modifier) {
    val (label, bgColor, textColor) = when {
        stockQuantity <= 0 -> Triple("Out of Stock", AnzoraError.copy(alpha = 0.16f), AnzoraError)
        stockQuantity <= 5 -> Triple("Only $stockQuantity left in stock", AnzoraWarning.copy(alpha = 0.18f), AnzoraWarning)
        else -> Triple("In Stock ($stockQuantity available)", AnzoraSuccess.copy(alpha = 0.16f), AnzoraSuccess)
    }
    Surface(
        modifier = modifier,
        color = bgColor,
        shape = RoundedCornerShape(50)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AnzoraBrandBadge(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    Brush.linearGradient(
                        listOf(AnzoraNavyDeep, AnzoraNavySurface)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "AZ",
                style = MaterialTheme.typography.titleMedium,
                color = AnzoraElectricBlue,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "ANZORA",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Surface(
                color = AnzoraElectricBlue.copy(alpha = 0.2f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "PK",
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = AnzoraElectricBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun launchExternalUrl(context: Context, url: String, onError: (String) -> Unit = {}) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        onError("Unable to open external app: ${e.localizedMessage ?: "No handler installed"}")
    }
}

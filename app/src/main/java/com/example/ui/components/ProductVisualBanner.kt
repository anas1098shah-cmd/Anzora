package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.AnzoraElectricBlue
import com.example.ui.theme.AnzoraNavyCard
import com.example.ui.theme.AnzoraNavyDark

@Composable
fun ProductVisualBanner(
    imageKeyOrUrl: String,
    category: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val trimmed = imageKeyOrUrl.trim()
    val isRemoteUrl = trimmed.startsWith("http://") || trimmed.startsWith("https://")

    if (isRemoteUrl) {
        AsyncImage(
            model = trimmed,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Crop,
            error = painterResource(id = resolvePresetDrawable(trimmed, category)),
            placeholder = painterResource(id = resolvePresetDrawable(trimmed, category))
        )
    } else {
        val drawableRes = resolvePresetDrawable(trimmed, category)
        Box(modifier = modifier) {
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                AnzoraNavyDark.copy(alpha = 0.08f),
                                AnzoraNavyCard.copy(alpha = 0.45f)
                            )
                        )
                    )
            )
            Icon(
                imageVector = categoryIcon(category),
                contentDescription = null,
                tint = AnzoraElectricBlue.copy(alpha = 0.85f),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(28.dp)
            )
        }
    }
}

fun resolvePresetDrawable(imageKey: String, category: String): Int {
    return when {
        imageKey.contains("hero", ignoreCase = true) -> R.drawable.img_anzora_hero_banner_1791593805771
        imageKey.contains("power", ignoreCase = true) ||
            imageKey.contains("cable", ignoreCase = true) ||
            imageKey.contains("charger", ignoreCase = true) ||
            imageKey.contains("ringlight", ignoreCase = true) ||
            imageKey.contains("accessory", ignoreCase = true) -> R.drawable.img_cat_power_studio_1791593833981
        imageKey.contains("earbuds", ignoreCase = true) ||
            imageKey.contains("headphones", ignoreCase = true) ||
            imageKey.contains("handsfree", ignoreCase = true) ||
            imageKey.contains("smartwatch", ignoreCase = true) -> R.drawable.img_cat_audio_wearables_1791593820401
        else -> when (category) {
            "Charging cables",
            "Mobile chargers and adapters",
            "Ring lights",
            "Mobile accessories" -> R.drawable.img_cat_power_studio_1791593833981
            else -> R.drawable.img_cat_audio_wearables_1791593820401
        }
    }
}

fun categoryIcon(category: String): ImageVector {
    return when (category) {
        "Wireless earbuds" -> Icons.Default.Headset
        "Headphones" -> Icons.Default.Headphones
        "Handsfree" -> Icons.Default.HeadsetMic
        "Charging cables" -> Icons.Default.Cable
        "Mobile chargers and adapters" -> Icons.Default.BatteryChargingFull
        "Smartwatches" -> Icons.Default.Watch
        "Ring lights" -> Icons.Default.LightMode
        "Mobile accessories" -> Icons.Default.PhoneAndroid
        else -> Icons.Default.Headphones
    }
}

package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val slug: String,
    val description: String,
    val iconName: String,
    val displayOrder: Int
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val categoryId: String,
    val categoryName: String,
    val pricePkr: Long,
    val compareAtPricePkr: Long?,
    val stockQuantity: Int,
    val sku: String,
    val description: String,
    val specsPipeSeparated: String, // e.g. "Driver: 13mm Titanium|Battery: 36 Hours|ANC: 42dB Hybrid|Warranty: 1 Year ANZORA Pakistan"
    val primaryImageResName: String,
    val customImageUri: String?, // Uploaded via Android Photo Picker or URL by Admin
    val galleryPipeSeparated: String, // Angle/View labels or secondary URIs
    val isFeatured: Boolean,
    val isNewArrival: Boolean,
    val updatedAt: Long = System.currentTimeMillis()
) {
    val specsList: List<Pair<String, String>>
        get() = specsPipeSeparated.split("|").mapNotNull { entry ->
            val parts = entry.split(":", limit = 2)
            if (parts.size == 2) parts[0].trim() to parts[1].trim() else null
        }

    val galleryAngles: List<String>
        get() = galleryPipeSeparated.split("|").filter { it.isNotBlank() }
}

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    val quantity: Int,
    val addedAt: Long = System.currentTimeMillis()
)

data class CartItemWithProduct(
    val product: ProductEntity,
    val quantity: Int
) {
    val lineTotalPkr: Long
        get() = product.pricePkr * quantity
}

@Entity(tableName = "customer_accounts")
data class CustomerAccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val phone: String,
    val email: String,
    val city: String,
    val completeAddress: String,
    val passwordHash: String,
    val isLoggedIn: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderNumber: String, // e.g., ANZ-849201
    val customerAccountId: Long?,
    val customerName: String,
    val customerPhone: String,
    val customerCity: String,
    val customerAddress: String,
    val orderNotes: String,
    val itemsSummaryJson: String, // Encoded list of items: title::qty::pricePkr::sku
    val subtotalPkr: Long,
    val deliveryFeePkr: Long,
    val totalPkr: Long,
    val paymentMethod: String, // "Cash on Delivery"
    val orderedViaWhatsApp: Boolean,
    val status: String, // "Pending", "Confirmed", "Packed", "Dispatched", "Delivered", "Cancelled"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    data class OrderLineItem(
        val productId: String,
        val title: String,
        val sku: String,
        val quantity: Int,
        val unitPricePkr: Long
    ) {
        val lineTotalPkr: Long get() = unitPricePkr * quantity
    }

    val parsedItems: List<OrderLineItem>
        get() = itemsSummaryJson.split("||").mapNotNull { row ->
            val parts = row.split("::")
            if (parts.size >= 5) {
                OrderLineItem(
                    productId = parts[0],
                    title = parts[1],
                    sku = parts[2],
                    quantity = parts[3].toIntOrNull() ?: 1,
                    unitPricePkr = parts[4].toLongOrNull() ?: 0L
                )
            } else null
        }
}

@Entity(tableName = "store_settings")
data class StoreSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val standardDeliveryFeePkr: Long = 250L,
    val freeDeliveryThresholdPkr: Long = 5000L,
    val enableFreeDeliveryThreshold: Boolean = true,
    val whatsappBusinessNumber: String = "+923712047636",
    val supportEmail: String = "support@anzora.pk",
    val headquartersAddress: String = "Plot 14-C, Tech Commercial Lane 4, DHA Phase 6, Karachi & Gulberg III, Lahore, Pakistan",
    val promoBannerHeadline: String = "NEXT-GEN AUDIO & GAN POWER",
    val promoBannerSubtext: String = "Engineered for Pakistan. Nationwide Cash on Delivery & 1-Year Official Replacement Warranty.",
    // Protected Admin Credentials hash (SHA-256 of admin email + password salt, default: admin@anzora.pk / Anzora@2026)
    val adminEmail: String = "admin@anzora.pk",
    val adminPasswordHash: String = ""
)

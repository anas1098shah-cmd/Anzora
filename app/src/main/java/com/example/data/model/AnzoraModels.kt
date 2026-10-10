package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Product(
    @DocumentId val id: String = "",
    val name: String = "",
    val category: String = "Wireless earbuds",
    val pricePkr: Long = 1000L,
    val originalPricePkr: Long = 1200L,
    val stock: Long = 10L,
    val sku: String = "ANZ-001",
    val description: String = "",
    val specs: String = "",
    val imageUrls: List<String> = listOf("preset:earbuds"),
    val featured: Boolean = false,
    val active: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class StoreConfig(
    @DocumentId val id: String = "global",
    val deliveryFeePkr: Long = 250L,
    val freeDeliveryThresholdPkr: Long = 5000L,
    val whatsappNumber: String = "+923712047636",
    val announcementBanner: String = "Nationwide Cash on Delivery | Free Shipping on Orders Above PKR 5,000",
    val supportEmail: String = "support@anzora.pk",
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Order(
    @DocumentId val id: String = "",
    val userId: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val customerCity: String = "",
    val customerAddress: String = "",
    val orderNotes: String = "",
    val itemsSummary: String = "",
    val itemCount: Long = 1L,
    val subtotalPkr: Long = 1000L,
    val deliveryFeePkr: Long = 250L,
    val totalPkr: Long = 1250L,
    val paymentMethod: String = "COD",
    val status: String = "Pending",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val fullName: String = "",
    val phone: String = "",
    val city: String = "",
    val address: String = "",
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class CartItem(
    val product: Product,
    val quantity: Int
) {
    val lineTotalPkr: Long
        get() = product.pricePkr * quantity
}

object AnzoraCategories {
    const val ALL = "All"
    val LIST = listOf(
        "Wireless earbuds",
        "Headphones",
        "Handsfree",
        "Charging cables",
        "Mobile chargers and adapters",
        "Smartwatches",
        "Ring lights",
        "Mobile accessories"
    )
}

object OrderStatuses {
    val LIST = listOf(
        "Pending",
        "Confirmed",
        "Packed",
        "Shipped",
        "Delivered",
        "Cancelled"
    )
}

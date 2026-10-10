package com.example.data.util

import com.example.data.model.CartItem
import com.example.data.model.Order
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.NumberFormat
import java.util.Locale

object WhatsAppHelper {
    const val OFFICIAL_WHATSAPP_NUMBER = "+923712047636"

    fun formatPkr(amount: Long): String {
        val formatter = NumberFormat.getNumberInstance(Locale.US)
        return "PKR ${formatter.format(amount)}"
    }

    fun buildOrderItemsSummary(items: List<CartItem>): String {
        return items.joinToString("\n") { item ->
            "• ${item.quantity}x ${item.product.name} (${formatPkr(item.product.pricePkr)} each) = ${formatPkr(item.lineTotalPkr)}"
        }
    }

    fun buildWhatsAppOrderMessage(
        orderId: String,
        customerName: String,
        customerPhone: String,
        customerCity: String,
        customerAddress: String,
        orderNotes: String,
        itemsSummary: String,
        subtotalPkr: Long,
        deliveryFeePkr: Long,
        totalPkr: Long
    ): String {
        return buildString {
            appendLine("*ANZORA PAKISTAN — NEW ORDER*")
            if (orderId.isNotBlank()) {
                appendLine("Order Ref: #${orderId.takeLast(6).uppercase()}")
            }
            appendLine("Payment Method: Cash on Delivery (COD)")
            appendLine("─────────────────────")
            appendLine("*ORDERED ITEMS:*")
            appendLine(itemsSummary)
            appendLine("─────────────────────")
            appendLine("Subtotal: ${formatPkr(subtotalPkr)}")
            appendLine("Delivery Charges: ${if (deliveryFeePkr == 0L) "FREE" else formatPkr(deliveryFeePkr)}")
            appendLine("*TOTAL PAYABLE (COD): ${formatPkr(totalPkr)}*")
            appendLine("─────────────────────")
            appendLine("*CUSTOMER & DELIVERY DETAILS:*")
            appendLine("Name: $customerName")
            appendLine("Phone: $customerPhone")
            appendLine("City: $customerCity")
            appendLine("Address: $customerAddress")
            if (orderNotes.isNotBlank()) {
                appendLine("Order Notes: $orderNotes")
            }
            appendLine("─────────────────────")
            append("Please confirm my ANZORA Cash on Delivery order.")
        }
    }

    fun buildWhatsAppOrderMessageFromOrder(order: Order): String {
        return buildWhatsAppOrderMessage(
            orderId = order.id,
            customerName = order.customerName,
            customerPhone = order.customerPhone,
            customerCity = order.customerCity,
            customerAddress = order.customerAddress,
            orderNotes = order.orderNotes,
            itemsSummary = order.itemsSummary,
            subtotalPkr = order.subtotalPkr,
            deliveryFeePkr = order.deliveryFeePkr,
            totalPkr = order.totalPkr
        )
    }

    fun buildWhatsAppUrl(phoneNumber: String = OFFICIAL_WHATSAPP_NUMBER, message: String): String {
        val cleanPhone = phoneNumber.replace("[^0-9]".toRegex(), "")
        val encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString())
        return "https://wa.me/$cleanPhone?text=$encodedMessage"
    }
}

package com.example

import com.example.data.local.OrderEntity
import com.example.data.repository.AnzoraRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun pakistaniPhoneValidation_acceptsLocalAndInternationalFormats() {
        assertTrue(AnzoraRepository.isValidPakistaniPhone("03001234567"))
        assertTrue(AnzoraRepository.isValidPakistaniPhone("0371-2047636"))
        assertTrue(AnzoraRepository.isValidPakistaniPhone("+923712047636"))
        assertTrue(AnzoraRepository.isValidPakistaniPhone("0092 321 9876543"))
        assertFalse(AnzoraRepository.isValidPakistaniPhone("12345"))
        assertFalse(AnzoraRepository.isValidPakistaniPhone("02134567890"))
    }

    @Test
    fun whatsappCheckoutUrl_encodesOrderDetailsAndUsesOfficialNumber() {
        val sampleOrder = OrderEntity(
            orderNumber = "ANZ-778899",
            customerAccountId = 1L,
            customerName = "Ali Raza",
            customerPhone = "+923001234567",
            customerCity = "Lahore",
            customerAddress = "House 12, Street 4, DHA Phase 5, Lahore",
            orderNotes = "Call before arrival",
            itemsSummaryJson = "prod_earbuds_1::ANZORA Aether Pro ANC Earbuds::ANZ-EB-001::2::6499",
            subtotalPkr = 12998L,
            deliveryFeePkr = 0L,
            totalPkr = 12998L,
            paymentMethod = "Cash on Delivery (COD)",
            orderedViaWhatsApp = true,
            status = "Pending"
        )

        val message = AnzoraRepository.buildWhatsAppOrderMessage(sampleOrder)
        assertTrue(message.contains("ANZ-778899"))
        assertTrue(message.contains("ANZORA Aether Pro ANC Earbuds"))
        assertTrue(message.contains("PKR 12,998"))
        assertTrue(message.contains("House 12, Street 4, DHA Phase 5, Lahore"))

        val url = AnzoraRepository.buildWhatsAppCheckoutUrl(sampleOrder)
        assertTrue(url.startsWith("https://wa.me/923712047636?text="))
        assertEquals(1, sampleOrder.parsedItems.size)
        assertEquals(12998L, sampleOrder.parsedItems.first().lineTotalPkr)
    }
}

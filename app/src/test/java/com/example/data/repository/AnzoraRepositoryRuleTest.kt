package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.CartItem
import com.example.data.model.Product
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class AnzoraRepositoryRuleTest : FirestoreEmulatorTestBase() {

    private val sampleCartItem = CartItem(
        product = Product(
            id = "", // Skip product stock lookup if product not seeded in test
            name = "ANZORA SonicPro ANC Wireless Earbuds",
            category = "Wireless earbuds",
            pricePkr = 4499L,
            originalPricePkr = 5999L,
            stock = 20L
        ),
        quantity = 2
    )

    @Test
    fun placeOrder_authenticatedCustomer_createsOrderAndCanObserveOwnOrders() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val repository = AnzoraRepository(firestore)

        val orderResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.placeOrder(
                customerName = "Ayesha Khan",
                customerPhone = "+923001234567",
                customerCity = "Lahore",
                customerAddress = "House 12, Street 4, Gulberg III, Lahore",
                orderNotes = "Deliver between 2 PM and 6 PM",
                cartItems = listOf(sampleCartItem),
                deliveryFeePkr = 250L,
                paymentMethod = "COD"
            )
        }

        assertTrue("Order creation should succeed", orderResult.isSuccess)
        val createdOrder = orderResult.getOrThrow()
        assertEquals(9248L, createdOrder.totalPkr)
        assertEquals("Pending", createdOrder.status)

        val observedOrders = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeCustomerOrders(aliceUid).first { list ->
                list.any { it.id == createdOrder.id }
            }
        }
        assertTrue(observedOrders.any { it.id == createdOrder.id })
    }

    @Test
    fun getOrderById_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val aliceRepo = AnzoraRepository(firestore)
        val createdOrder = withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.placeOrder(
                customerName = "Ayesha Khan",
                customerPhone = "+923001234567",
                customerCity = "Karachi",
                customerAddress = "Clifton Block 5, Karachi",
                orderNotes = "",
                cartItems = listOf(sampleCartItem),
                deliveryFeePkr = 250L,
                paymentMethod = "COD_WHATSAPP"
            ).getOrThrow()
        }

        // Switch to Bob
        signInTestUser(BOB_EMAIL)
        val bobRepo = AnzoraRepository(firestore)
        val bobFetch = withTimeout(DEFAULT_TIMEOUT_MS) {
            bobRepo.getOrderById(createdOrder.id)
        }
        assertTrue("Cross-user order fetch must fail", bobFetch.isFailure)
        val exception = bobFetch.exceptionOrNull() as? FirebaseFirestoreException
        assertNotNull(exception)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, exception?.code)
    }

    @Test
    fun saveAndObserveUserProfile_isolatedToOwner() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val repo = AnzoraRepository(firestore)

        val saveResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repo.saveUserProfile(
                fullName = "Ayesha Khan",
                phone = "+923001234567",
                city = "Islamabad",
                address = "Sector F-7/2, Street 14, Islamabad"
            )
        }
        assertTrue(saveResult.isSuccess)

        val profile = withTimeout(FLOW_TIMEOUT_MS) {
            repo.observeUserProfile(aliceUid).first { it != null }
        }
        assertEquals("Islamabad", profile?.city)

        // Switch to Bob and verify Bob cannot read Alice's profile
        signInTestUser(BOB_EMAIL)
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repo.observeUserProfile(aliceUid).first()
            }
            fail("Expected PERMISSION_DENIED exception")
        } catch (e: Exception) {
            val rootCause = generateSequence<Throwable>(e) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            assertNotNull("Expected FirebaseFirestoreException in cause chain", rootCause)
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, rootCause?.code)
        }
    }

    @Test
    fun observeActiveProducts_unauthenticatedUser_failsWithIllegalStateOrPermissionDenied() = runBlocking {
        auth.signOut()
        val repository = AnzoraRepository(firestore)
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeActiveProducts().first()
            }
            fail("Unauthenticated user must not be able to observe products")
        } catch (expected: Exception) {
            assertTrue(
                expected is IllegalStateException ||
                    (expected is FirebaseFirestoreException &&
                        expected.code == FirebaseFirestoreException.Code.PERMISSION_DENIED)
            )
        }
    }

    private companion object {
        const val ALICE_EMAIL = "alice.anzora@test.com"
        const val BOB_EMAIL = "bob.anzora@test.com"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}

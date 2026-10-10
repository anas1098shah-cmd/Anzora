const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ADMIN_UID = "admin_anas";
const ADMIN_TOKEN = {
  email: "anas1098shah@gmail.com",
  email_verified: true,
};
const SPOOF_ADMIN_TOKEN = {
  email: "anas1098shah@gmail.com",
  email_verified: false,
};
const ALICE_UID = "alice_customer_1";
const BOB_UID = "bob_customer_2";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

function validProductPayload(overrides = {}) {
  const now = new Date(Date.now() - 1000);
  return {
    name: "ANZORA SonicPro ANC Wireless Earbuds",
    category: "Wireless earbuds",
    pricePkr: 4499,
    originalPricePkr: 5999,
    stock: 25,
    sku: "ANZ-TWS-01",
    description: "Active noise cancelling wireless earbuds with 36-hour battery life and low-latency gaming mode.",
    specs: "Bluetooth 5.3 | ANC 35dB | USB-C Fast Charge | IPX5 Water Resistant",
    imageUrls: ["preset:earbuds"],
    featured: true,
    active: true,
    createdAt: now,
    updatedAt: now,
    ...overrides,
  };
}

function validOrderPayload(userId = ALICE_UID, overrides = {}) {
  const now = new Date(Date.now() - 1000);
  return {
    userId,
    customerName: "Ayesha Khan",
    customerPhone: "+923001234567",
    customerCity: "Lahore",
    customerAddress: "House 42, Street 10, DHA Phase 5, Lahore",
    orderNotes: "Call before delivery",
    itemsSummary: "1x ANZORA SonicPro ANC Wireless Earbuds (PKR 4,499)",
    itemCount: 1,
    subtotalPkr: 4499,
    deliveryFeePkr: 250,
    totalPkr: 4749,
    paymentMethod: "COD",
    status: "Pending",
    createdAt: now,
    updatedAt: now,
    ...overrides,
  };
}

test("1. Unauthenticated user cannot read or list products", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("products").get());
});

test("2. Verified admin can create and update products; non-admin cannot create or edit price", async () => {
  const adminDb = testEnv.authenticatedContext(ADMIN_UID, ADMIN_TOKEN).firestore();
  await assertSucceeds(
    adminDb.collection("products").doc("prod_1").set(validProductPayload())
  );

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  // Alice can read active product
  await assertSucceeds(aliceDb.collection("products").doc("prod_1").get());
  // Alice cannot create product
  await assertFails(
    aliceDb.collection("products").doc("prod_2").set(validProductPayload())
  );
  // Alice cannot change pricePkr
  await assertFails(
    aliceDb.collection("products").doc("prod_1").update({
      pricePkr: 100,
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("3. Unverified admin email spoof attack is rejected", async () => {
  const spoofDb = testEnv
    .authenticatedContext("spoof_uid", SPOOF_ADMIN_TOKEN)
    .firestore();
  await assertFails(
    spoofDb.collection("products").doc("prod_spoof").set(validProductPayload())
  );
});

test("4. Customer can decrement product stock during checkout, but cannot increment or set negative stock", async () => {
  const adminDb = testEnv.authenticatedContext(ADMIN_UID, ADMIN_TOKEN).firestore();
  await assertSucceeds(
    adminDb.collection("products").doc("prod_1").set(validProductPayload({ stock: 5 }))
  );

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  // Valid stock deduction
  await assertSucceeds(
    aliceDb.collection("products").doc("prod_1").update({
      stock: 3,
      updatedAt: new Date(Date.now() - 500),
    })
  );
  // Cannot increase stock
  await assertFails(
    aliceDb.collection("products").doc("prod_1").update({
      stock: 10,
      updatedAt: new Date(Date.now() - 500),
    })
  );
  // Cannot set negative stock
  await assertFails(
    aliceDb.collection("products").doc("prod_1").update({
      stock: -1,
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("5. Customer can create valid order for themselves, but fails on totalPkr mismatch or spoofed userId", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("orders").doc("order_1").set(validOrderPayload(ALICE_UID))
  );

  // Spoofed userId
  await assertFails(
    aliceDb.collection("orders").doc("order_2").set(validOrderPayload(BOB_UID))
  );

  // Tampered totalPkr
  await assertFails(
    aliceDb
      .collection("orders")
      .doc("order_3")
      .set(validOrderPayload(ALICE_UID, { totalPkr: 100 }))
  );
});

test("6. Cross-user order isolation and Query Enforcer alignment", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("orders").doc("order_alice").set(validOrderPayload(ALICE_UID))
  );

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  // Bob cannot get Alice's order
  await assertFails(bobDb.collection("orders").doc("order_alice").get());
  // Bob cannot list all orders without filter
  await assertFails(bobDb.collection("orders").get());
  // Alice can list her own orders with matching userId filter
  await assertSucceeds(
    aliceDb.collection("orders").where("userId", "==", ALICE_UID).get()
  );

  // Admin can list all orders
  const adminDb = testEnv.authenticatedContext(ADMIN_UID, ADMIN_TOKEN).firestore();
  await assertSucceeds(adminDb.collection("orders").get());
});

test("7. Order status lifecycle: Customer can only cancel Pending order; Admin can update status", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("orders").doc("order_1").set(validOrderPayload(ALICE_UID))
  );

  // Alice cannot mark her own order Delivered
  await assertFails(
    aliceDb.collection("orders").doc("order_1").update({
      status: "Delivered",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Admin marks order Confirmed
  const adminDb = testEnv.authenticatedContext(ADMIN_UID, ADMIN_TOKEN).firestore();
  await assertSucceeds(
    adminDb.collection("orders").doc("order_1").update({
      status: "Confirmed",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Alice cannot cancel once Confirmed
  await assertFails(
    aliceDb.collection("orders").doc("order_1").update({
      status: "Cancelled",
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("8. PII isolation on /users/{userId} and Shadow Update rejection", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      fullName: "Ayesha Khan",
      phone: "+923001234567",
      city: "Lahore",
      address: "DHA Phase 5",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Shadow update with ghost field isVerified
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      fullName: "Ayesha K.",
      isVerified: true,
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Bob cannot read Alice's profile
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
});

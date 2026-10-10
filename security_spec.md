# ANZORA E-Commerce Platform Security Specification

## 1. Data Invariants
1. **Authentication Mandatory**: No unauthenticated reads or writes are permitted across any collection (`request.auth != null`).
2. **Role-Based Admin Protection**: Only verified administrators (`request.auth.token.email == 'anas1098shah@gmail.com' && request.auth.token.email_verified == true` or `exists(/databases/$(database)/documents/admins/$(request.auth.uid))`) may create, edit, or delete products in `/products/{productId}`, modify store configuration in `/store_config/{configId}`, or update order fulfillment statuses across all customer orders.
3. **Customer Order Isolation & Integrity**:
   - A customer can only create an order where `userId == request.auth.uid`, initial `status == 'Pending'`, `subtotalPkr > 0`, `deliveryFeePkr >= 0`, and `totalPkr == subtotalPkr + deliveryFeePkr`.
   - Customers can only `get` or `list` their own orders (`resource.data.userId == request.auth.uid`), while admins can `get` and `list` all orders.
   - Customers can only update their own order to cancel it (`status == 'Cancelled'`) when the order is currently `'Pending'`, and cannot alter prices, items, or customer identity. Once an order reaches `'Delivered'` or `'Cancelled'` (terminal states), customers cannot mutate it.
4. **Stock Deduction & Price Protection**:
   - Customers can only decrement `stock` on `/products/{productId}` (when placing an order) with `stock >= 0` and all other product fields (`pricePkr`, `name`, `category`, `sku`, etc.) strictly unchanged.
5. **PII Isolation**:
   - `/users/{userId}` contains customer delivery address and phone PII and is strictly isolated to `request.auth.uid == userId` or `isAdmin()`.

## 2. The "Dirty Dozen" Payloads
1. **Unauthenticated Product Scraping**: Unauthenticated client calls `db.collection("products").get()` -> Rejected (`request.auth != null`).
2. **Unauthorized Product Price Tampering**: Non-admin user attempts to update `pricePkr` on `/products/prod_1` to `1` PKR -> Rejected (`isAdmin()` required for price edits).
3. **Negative Stock Exploit**: Customer attempts to update `/products/prod_1` with `stock: -5` -> Rejected (`data.stock >= 0`).
4. **Ghost Field Injection (Shadow Update)**: User sends `isAdmin: true` or `verified: true` in `/users/{userId}` or `/orders/{orderId}` -> Rejected by `.keys().hasOnly(...)`.
5. **Order Identity Spoofing**: User A (`alice_123`) creates an order with `userId: "bob_456"` -> Rejected (`incoming().userId == request.auth.uid`).
6. **Order Total Manipulation**: User creates an order where `subtotalPkr: 5000`, `deliveryFeePkr: 250`, but `totalPkr: 100` -> Rejected (`data.totalPkr == data.subtotalPkr + data.deliveryFeePkr`).
7. **Order Status Escalation by Customer**: Customer attempts to update their order status from `'Pending'` to `'Delivered'` -> Rejected (customers can only transition `'Pending'` -> `'Cancelled'`).
8. **Terminal State Mutation**: Customer attempts to update an already `'Delivered'` or `'Cancelled'` order -> Rejected by terminal state lock.
9. **Cross-User Order Snooping (PII Leak)**: User A attempts `get` or `list` on User B's orders in `/orders` -> Rejected (`resource.data.userId == request.auth.uid || isAdmin()`).
10. **Unfiltered Order List Query**: Non-admin user executes `db.collection("orders").get()` without `.whereEqualTo("userId", uid)` -> Rejected by Query Enforcer.
11. **Cross-User Profile Access**: User A attempts to read `/users/bob_456` -> Rejected (`request.auth.uid == userId`).
12. **Admin Email Spoofing (Unverified Email)**: Attacker authenticates with `email: 'anas1098shah@gmail.com'` but `email_verified: false` -> Rejected (`request.auth.token.email_verified == true` required).

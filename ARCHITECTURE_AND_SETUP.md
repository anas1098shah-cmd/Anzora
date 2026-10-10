# ANZORA Pakistan — Full-Stack E-Commerce & Android Platform Guide

## 1. Platform Overview
- **Brand**: ANZORA (Pakistan)
- **Currency**: Pakistani Rupee (`PKR`)
- **Official WhatsApp Ordering & Support**: `+923712047636`
- **Payment Method**: Cash on Delivery (`COD`) + WhatsApp Prefilled Order Summary
- **Product Categories (8)**:
  1. Wireless earbuds
  2. Headphones
  3. Handsfree
  4. Charging cables
  5. Mobile chargers and adapters
  6. Smartwatches
  7. Ring lights
  8. Mobile accessories

---

## 2. Database & Backend Architecture
Because Firebase interactive cloud provisioning was declined in the UI prompt, the Android application runs out-of-the-box with a complete, persistent **Room SQLite transactional backend** (`AnzoraDatabase` + `AnzoraRepository`) while providing production-ready **Cloud Firestore** (`/firestore.rules`), **Firebase Storage** (`/storage.rules`), and schema blueprint (`/firebase-blueprint.json`) files for shared Web + Android cloud synchronization.

### Server-Side / Repository Validation Enforced:
1. **Authoritative Price & Stock Verification**: During checkout (`AnzoraRepository.placeOrder`), the repository runs inside an atomic database transaction (`database.withTransaction`), queries authoritative product prices and stock quantities directly from the catalog table, rejects out-of-stock or negative-quantity requests, deducts stock atomically, and computes the exact PKR subtotal and configurable delivery fee.
2. **Configurable Delivery Fee**: Administrators can configure the nationwide PKR delivery fee (default `PKR 250`) and optional Free Delivery threshold (default `PKR 5,000`) from the Protected Admin Console.
3. **Protected Admin Access**:
   - In the local repository: Admin authentication verifies a salted SHA-256 credential hash stored in `store_settings` (`admin@anzora.pk` / `Anzora@2026` by default, rotatable in the Admin Console) and guards all administrative repository methods behind session verification.
   - In Cloud Firestore & Storage (`/firestore.rules` & `/storage.rules`): Administrative writes require a server-verified Firebase Auth custom token claim (`request.auth.token.admin == true`), preventing any frontend-only bypass.

---

## 3. Manual Firebase & Web Sync Setup Steps
To connect both a Web storefront (React / Next.js / Capacitor) and this Android app to a live shared Firebase project:

1. **Create a Firebase Project**:
   - Open the [Firebase Console](https://console.firebase.google.com/) and create a project (e.g., `anzora-pakistan`).
   - Enable **Authentication** (Google Sign-In / Email & Password), **Cloud Firestore**, and **Firebase Storage**.
2. **Deploy Security Rules**:
   ```bash
   firebase deploy --only firestore:rules,storage
   ```
3. **Assign the Admin Custom Claim (Backend / Admin SDK)**:
   ```javascript
   // Run once via Node.js with Firebase Admin SDK
   const admin = require("firebase-admin");
   admin.initializeApp();
   await admin.auth().setCustomUserClaims("<ADMIN_USER_UID>", { admin: true });
   ```
4. **Android & Web Client Configuration**:
   - Download `google-services.json` for package `com.aistudio.anzora.pkxrzq` and place it in `app/google-services.json`.
   - For a companion Web / Capacitor app, configure `.env` with your public Firebase web config keys and point it to the same `/categories`, `/products`, `/orders`, and `/settings/store` collections defined in `/firebase-blueprint.json`.

---

## 4. Building & Testing the Android Application (APK / AAB)
- **Run Unit & Repository Validation Tests**:
  ```bash
  gradle :app:testDebugUnitTest
  ```
- **Compile Debug APK**:
  ```bash
  gradle assembleDebug
  ```
  The compiled APK is generated at `app/build/outputs/apk/debug/app-debug.apk`, or you can export the APK/AAB directly from the **AI Studio Settings menu**.

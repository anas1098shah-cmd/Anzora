package com.example.data.model

object AnzoraCatalogDefaults {
    val INITIAL_PRODUCTS: List<Product> = listOf(
        Product(
            id = "anz_earbuds_sonicpro",
            name = "ANZORA SonicPro ANC Wireless Earbuds",
            category = "Wireless earbuds",
            pricePkr = 4499L,
            originalPricePkr = 5999L,
            stock = 35L,
            sku = "ANZ-TWS-001",
            description = "Engineered for crystal-clear calls and deep bass across Pakistan. Features 35dB Active Noise Cancellation, quad-mic ENC for noisy streets, and a sleek matte navy charging case with electric cyan LED battery telemetry.",
            specs = "Bluetooth 5.3 | 35dB Active Noise Cancellation | 36 Hours Total Playtime | Type-C Fast Charging | 45ms Low Latency Gaming Mode | IPX5 Sweat & Splash Resistant",
            imageUrls = listOf("preset:earbuds", "preset:hero"),
            featured = true,
            active = true
        ),
        Product(
            id = "anz_headphones_studiox",
            name = "ANZORA StudioX Over-Ear Wireless Headphones",
            category = "Headphones",
            pricePkr = 7899L,
            originalPricePkr = 9500L,
            stock = 20L,
            sku = "ANZ-HP-002",
            description = "Immersive 40mm neodymium drivers wrapped in ultra-plush memory foam ear cushions. Built for audiophiles, remote professionals, and long study or travel sessions with both Bluetooth and 3.5mm AUX support.",
            specs = "40mm Custom Dynamic Drivers | 50 Hours Battery Life | Hybrid ANC | Foldable Aluminum Hinges | Dual Device Multipoint Connection | USB-C + 3.5mm Audio Jack",
            imageUrls = listOf("preset:headphones", "preset:hero"),
            featured = true,
            active = true
        ),
        Product(
            id = "anz_handsfree_bassflow",
            name = "ANZORA BassFlow Pro Type-C & 3.5mm Handsfree",
            category = "Handsfree",
            pricePkr = 1299L,
            originalPricePkr = 1799L,
            stock = 60L,
            sku = "ANZ-HF-003",
            description = "Tangle-free Kevlar-reinforced braided handsfree with an inline HD MEMS microphone and tactile volume controls. Zero-latency wired audio ideal for daily calls and mobile gaming.",
            specs = "10mm Composite Diaphragm | Kevlar Braided 1.2m Cable | Inline HD Mic & Call Button | Gold-Plated Connector | Passive Noise Isolation Ear Tips",
            imageUrls = listOf("preset:handsfree", "preset:earbuds"),
            featured = false,
            active = true
        ),
        Product(
            id = "anz_cable_hyperweave_100w",
            name = "ANZORA HyperWeave 100W PD Type-C Fast Cable",
            category = "Charging cables",
            pricePkr = 999L,
            originalPricePkr = 1499L,
            stock = 80L,
            sku = "ANZ-CBL-004",
            description = "Smart E-Marker chip regulated 100W Power Delivery braided nylon cable. Charges laptops, tablets, and flagship Android & iPhone 15/16 series devices safely at maximum speed.",
            specs = "100W (20V/5A) PD 3.0 Fast Charge | Built-in E-Marker Safety Chip | 20,000+ Bend Lifespan Nylon Braiding | 480Mbps Data Sync | 1.5 Meter Length | Aluminum Alloy Shells",
            imageUrls = listOf("preset:cable", "preset:power"),
            featured = true,
            active = true
        ),
        Product(
            id = "anz_charger_gan_65w",
            name = "ANZORA VoltCore 65W GaN Triple-Port Fast Charger",
            category = "Mobile chargers and adapters",
            pricePkr = 4850L,
            originalPricePkr = 6200L,
            stock = 28L,
            sku = "ANZ-CHG-005",
            description = "Next-generation Gallium Nitride (GaN) wall adapter with 2x USB-C PD ports and 1x USB-A QC 3.0 port. Built-in voltage fluctuation protection engineered for Pakistani power grids (100V–240V).",
            specs = "65W Max Output | 2x USB-C PD 3.0 + 1x USB-A QC 3.0 | GaN Cool-Tech Architecture | Over-Voltage & Surge Protection | UK/PK 3-Pin & Euro Plug Support",
            imageUrls = listOf("preset:charger", "preset:power"),
            featured = true,
            active = true
        ),
        Product(
            id = "anz_watch_apex_amoled",
            name = "ANZORA Apex Ultra AMOLED Bluetooth Calling Smartwatch",
            category = "Smartwatches",
            pricePkr = 8999L,
            originalPricePkr = 11500L,
            stock = 18L,
            sku = "ANZ-SW-006",
            description = "1.96-inch Always-On Super AMOLED display housed in a precision zinc-alloy chassis. Answer calls directly from your wrist, monitor heart rate and SpO2, and enjoy 7-day battery endurance.",
            specs = "1.96\" 410x502 AMOLED Display | Bluetooth 5.3 Single-Chip Calling | 100+ Sports Modes | 24/7 Heart Rate & SpO2 Sensor | IP68 Water Resistant | 7-Day Typical Battery",
            imageUrls = listOf("preset:smartwatch", "preset:hero"),
            featured = true,
            active = true
        ),
        Product(
            id = "anz_ringlight_lumapro_14",
            name = "ANZORA LumaPro 14\" RGB & Bi-Color Studio Ring Light with Tripod",
            category = "Ring lights",
            pricePkr = 4250L,
            originalPricePkr = 5500L,
            stock = 22L,
            sku = "ANZ-RL-007",
            description = "Creator-grade 14-inch LED ring light with 3 color temperatures (3000K–6500K), 10 brightness levels, 360-degree gooseneck phone holder, Bluetooth shutter remote, and a 7-foot heavy-duty tripod stand.",
            specs = "14-Inch Diffused LED Ring | 3000K-6500K Bi-Color + RGB Modes | CRI > 95 | 7ft Extendable Aluminum Tripod | 3x Phone Mount Slots | Wireless Bluetooth Shutter Included",
            imageUrls = listOf("preset:ringlight", "preset:power"),
            featured = false,
            active = true
        ),
        Product(
            id = "anz_acc_magsafe_powerstand",
            name = "ANZORA MagGrip 10000mAh Magnetic Wireless Power Bank & Stand",
            category = "Mobile accessories",
            pricePkr = 5499L,
            originalPricePkr = 6999L,
            stock = 30L,
            sku = "ANZ-ACC-008",
            description = "Snap-and-charge 15W magnetic wireless power bank with a foldable aluminum kickstand and 22.5W wired PD bidirectional Type-C port. Pocket-sized backup power for long commutes.",
            specs = "10,000mAh High-Density Li-Polymer | 15W Magnetic Wireless + 22.5W USB-C PD | Integrated Foldable Kickstand | LED Battery Indicator | Airline Safe",
            imageUrls = listOf("preset:accessory", "preset:power"),
            featured = true,
            active = true
        )
    )
}

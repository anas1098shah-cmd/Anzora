package com.example.data.local

object AnzoraCatalogSeeder {

    val initialCategories = listOf(
        CategoryEntity(
            id = "cat_earbuds",
            name = "Wireless earbuds",
            slug = "wireless-earbuds",
            description = "Active Noise Cancelling (ANC), ENC quad-mic & ultra-low latency gaming earbuds.",
            iconName = "earbuds",
            displayOrder = 1
        ),
        CategoryEntity(
            id = "cat_headphones",
            name = "Headphones",
            slug = "headphones",
            description = "Over-ear studio wireless headphones with spatial audio and plush memory foam.",
            iconName = "headphones",
            displayOrder = 2
        ),
        CategoryEntity(
            id = "cat_handsfree",
            name = "Handsfree",
            slug = "handsfree",
            description = "Hi-Res Type-C DAC and 3.5mm braided acoustic wired handsfree with inline mic.",
            iconName = "settings_input_component",
            displayOrder = 3
        ),
        CategoryEntity(
            id = "cat_cables",
            name = "Charging cables",
            slug = "charging-cables",
            description = "100W PD kevlar-braided Type-C to Type-C, Lightning fast-charge & 480Mbps data cables.",
            iconName = "cable",
            displayOrder = 4
        ),
        CategoryEntity(
            id = "cat_chargers",
            name = "Mobile chargers and adapters",
            slug = "mobile-chargers-and-adapters",
            description = "GaN III 33W, 45W Super Fast & 65W multi-port wall chargers with surge protection.",
            iconName = "power",
            displayOrder = 5
        ),
        CategoryEntity(
            id = "cat_smartwatches",
            name = "Smartwatches",
            slug = "smartwatches",
            description = "AMOLED Bluetooth calling smartwatches with IP68 water resistance and health sensors.",
            iconName = "watch",
            displayOrder = 6
        ),
        CategoryEntity(
            id = "cat_ringlights",
            name = "Ring lights",
            slug = "ring-lights",
            description = "Bi-color studio LED ring lights and 7ft aluminum tripods for creators and streams.",
            iconName = "wb_iridescent",
            displayOrder = 7
        ),
        CategoryEntity(
            id = "cat_accessories",
            name = "Mobile accessories",
            slug = "mobile-accessories",
            description = "MagSafe power banks, aluminum desk stands, car mounts, and gaming phone coolers.",
            iconName = "devices_other",
            displayOrder = 8
        )
    )

    val initialProducts = listOf(
        // 1. Wireless earbuds
        ProductEntity(
            id = "prod_earbuds_1",
            title = "ANZORA Aether Pro ANC Earbuds",
            subtitle = "42dB Hybrid Active Noise Cancellation • 36H Total Playback",
            categoryId = "cat_earbuds",
            categoryName = "Wireless earbuds",
            pricePkr = 6499L,
            compareAtPricePkr = 8499L,
            stockQuantity = 24,
            sku = "ANZ-EB-001",
            description = "Engineered for crystal-clear calls on Pakistani urban commutes and immersive lossless bass. Features 6-mic Environmental Noise Cancellation (ENC), Bluetooth 5.4 dual-device pairing, and IPX5 sweat resistance.",
            specsPipeSeparated = "ANC Depth: 42dB Hybrid ANC + Transparency Mode|Driver Size: 13mm Titanium Composite Dynamic|Battery Life: 7.5H Buds + 36H Charging Case|Bluetooth: v5.4 (45ms Low Latency Gaming Mode)|Charging Port: USB Type-C Fast Charge (10m = 2H)|Warranty: 1 Year ANZORA Official Pakistan Replacement",
            primaryImageResName = "img_cat_audio",
            customImageUri = null,
            galleryPipeSeparated = "Studio Front View • Charging Case & Buds|Acoustic Chamber Detail • 13mm Titanium Driver|Ergonomic In-Ear Fit • Matte Navy Finish",
            isFeatured = true,
            isNewArrival = true
        ),
        ProductEntity(
            id = "prod_earbuds_2",
            title = "ANZORA SonicPods Air ENC",
            subtitle = "Quad-Mic AI Clear Calls • Featherlight Semi-In-Ear Design",
            categoryId = "cat_earbuds",
            categoryName = "Wireless earbuds",
            pricePkr = 3850L,
            compareAtPricePkr = 4999L,
            stockQuantity = 40,
            sku = "ANZ-EB-002",
            description = "All-day comfort meets punchy acoustic performance. Built with 4 beamforming microphones to isolate your voice during outdoor calls and online meetings.",
            specsPipeSeparated = "Call Tech: Quad-Mic AI Environmental Noise Reduction|Weight: 3.8g per earbud|Battery: 28 Hours with Compact Pocket Case|Water Resistance: IPX4 Splash Proof|Warranty: 6 Months ANZORA Warranty",
            primaryImageResName = "img_hero_banner",
            customImageUri = null,
            galleryPipeSeparated = "Pocket Case View • Matte Midnight Blue|Earbud Stem & Touch Sensor View|USB-C Fast Charging Port View",
            isFeatured = false,
            isNewArrival = true
        ),

        // 2. Headphones
        ProductEntity(
            id = "prod_headphones_1",
            title = "ANZORA Aurora Studio ANC Over-Ear Headphones",
            subtitle = "Hi-Res Spatial Audio • 60-Hour Battery • Plush Protein Leather",
            categoryId = "cat_headphones",
            categoryName = "Headphones",
            pricePkr = 11999L,
            compareAtPricePkr = 14999L,
            stockQuantity = 15,
            sku = "ANZ-HP-001",
            description = "Studio-grade acoustic isolation with custom 40mm neodymium drivers. Switch seamlessly between wireless Bluetooth 5.4 and zero-latency 3.5mm AUX studio monitoring.",
            specsPipeSeparated = "Drivers: 40mm Custom Neodymium Hi-Res Audio|Noise Cancelling: Multi-Mode Hybrid ANC|Battery Life: Up to 60 Hours (40H with ANC On)|Ear Cushions: Breathable Memory Foam Protein Leather|Connectivity: Bluetooth 5.4 + 3.5mm Braided AUX Cable|Warranty: 1 Year ANZORA Official Warranty",
            primaryImageResName = "img_cat_audio",
            customImageUri = null,
            galleryPipeSeparated = "Over-Ear Studio Profile • Navy & Brushed Silver|Foldable Travel Hinge & Memory Foam Cups|Tactile ANC & Volume Control Interface",
            isFeatured = true,
            isNewArrival = false
        ),
        ProductEntity(
            id = "prod_headphones_2",
            title = "ANZORA PulseBass Wireless On-Ear",
            subtitle = "Deep Bass Boost Mode • Foldable Commuter Frame • 35H Battery",
            categoryId = "cat_headphones",
            categoryName = "Headphones",
            pricePkr = 5499L,
            compareAtPricePkr = 6800L,
            stockQuantity = 19,
            sku = "ANZ-HP-002",
            description = "Designed for students, remote professionals, and gym sessions. Dedicated Bass-Up button delivers rich low-end punch without muddying vocals.",
            specsPipeSeparated = "Drivers: 36mm Dynamic Bass Drivers|Playtime: 35 Hours Continuous|Multipoint: Connect Phone + Laptop Simultaneously|Microphone: Built-in HD Voice Mic|Charging: Type-C Rapid Charge",
            primaryImageResName = "img_hero_banner",
            customImageUri = null,
            galleryPipeSeparated = "Folded Commuter View|Headband Cushion Detail|Control Buttons & AUX Jack",
            isFeatured = false,
            isNewArrival = false
        ),

        // 3. Handsfree
        ProductEntity(
            id = "prod_handsfree_1",
            title = "ANZORA AcousticPro Type-C DAC Handsfree",
            subtitle = "Built-in 24-Bit Digital DAC Chip • Kevlar Braided Cord • HD Mic",
            categoryId = "cat_handsfree",
            categoryName = "Handsfree",
            pricePkr = 1650L,
            compareAtPricePkr = 2200L,
            stockQuantity = 55,
            sku = "ANZ-HF-001",
            description = "Universal compatibility with Samsung Galaxy, iPhone 15/16 Series, Pixel, Xiaomi, and Infinix Type-C devices with zero static noise. Tangle-free braided jacket survives daily pocket wear.",
            specsPipeSeparated = "Connector: USB Type-C with Built-in 24-Bit/96kHz DAC|Cable Material: Dual-Layer Kevlar Braided + TPE|Driver: 10mm Brass Ring Composite Speaker|In-Line Remote: 3-Button Volume + Call/Track Control|Cable Length: 1.2 Meters",
            primaryImageResName = "img_cat_audio",
            customImageUri = null,
            galleryPipeSeparated = "Metal Earbud Housing & Type-C DAC Plug|3-Button Inline Microphone Controller|Braided Stress-Relief Cord Detail",
            isFeatured = true,
            isNewArrival = false
        ),
        ProductEntity(
            id = "prod_handsfree_2",
            title = "ANZORA BassLine 3.5mm Metal Handsfree",
            subtitle = "CNC Anodized Aluminum Chamber • Zero-Lag Gaming & Calls",
            categoryId = "cat_handsfree",
            categoryName = "Handsfree",
            pricePkr = 1199L,
            compareAtPricePkr = 1599L,
            stockQuantity = 68,
            sku = "ANZ-HF-002",
            description = "Classic gold-plated L-shape 3.5mm jack engineered for mobile gamers and music lovers who demand zero latency and durable build quality.",
            specsPipeSeparated = "Connector: 3.5mm L-Shape Gold-Plated Audio Jack|Housing: CNC Machined Navy Aluminum|Eartips: S/M/L Silicone Acoustic Tips Included|Mic: High-Sensitivity Omni-Directional Mic|Length: 1.25 Meters",
            primaryImageResName = "img_cat_charging",
            customImageUri = null,
            galleryPipeSeparated = "CNC Metal Earphone Buds|L-Shaped 90-Degree Gaming Audio Plug|Silicone Ear-Tips Kit",
            isFeatured = false,
            isNewArrival = false
        ),

        // 4. Charging cables
        ProductEntity(
            id = "prod_cables_1",
            title = "ANZORA HyperWeave 100W PD Type-C to Type-C Cable",
            subtitle = "E-Marker Smart Chip • 5A Super Fast Charge • 20,000+ Bend Lifespan",
            categoryId = "cat_cables",
            categoryName = "Charging cables",
            pricePkr = 1350L,
            compareAtPricePkr = 1850L,
            stockQuantity = 80,
            sku = "ANZ-CB-001",
            description = "Charges MacBook Air/Pro, iPad Pro, Samsung S24 Ultra (45W PPS), and all Type-C smartphones at full speed. Integrated E-Marker chip regulates voltage safely.",
            specsPipeSeparated = "Max Output: 100W (20V/5A) Power Delivery 3.0 & PPS|Chipset: Certified Smart E-Marker IC|Data Transfer: 480Mbps USB 2.0 High Speed|Armor: High-Density Ballistic Nylon Braid + Zinc Alloy Heads|Length: 1.5 Meters (5 Feet)",
            primaryImageResName = "img_cat_charging",
            customImageUri = null,
            galleryPipeSeparated = "65W/100W LED Connector Close-up|Ballistic Nylon Braided Weave|Velcro Cable Organizer Strap",
            isFeatured = true,
            isNewArrival = true
        ),
        ProductEntity(
            id = "prod_cables_2",
            title = "ANZORA PowerLink 27W Type-C to Lightning Cable",
            subtitle = "Fast Charge for iPhone 8 to 14 Series • Reinforced Connector",
            categoryId = "cat_cables",
            categoryName = "Charging cables",
            pricePkr = 1299L,
            compareAtPricePkr = 1699L,
            stockQuantity = 45,
            sku = "ANZ-CB-002",
            description = "Delivers 0% to 50% charge in 28 minutes when paired with an ANZORA PD adapter. Built with tinned copper cores for minimal resistance.",
            specsPipeSeparated = "Output: Up to 27W PD Fast Charge|Compatibility: iPhone 14/13/12/11/XS/XR & iPad|Jacket: Navy Braided Anti-Fray Exterior|Length: 1.2 Meters",
            primaryImageResName = "img_cat_charging",
            customImageUri = null,
            galleryPipeSeparated = "Lightning & Type-C Precision Ends|Reinforced Strain Relief Neck|Coiled Desk Setup",
            isFeatured = false,
            isNewArrival = false
        ),

        // 5. Mobile chargers and adapters
        ProductEntity(
            id = "prod_chargers_1",
            title = "ANZORA VoltGaN 65W 3-Port Fast Wall Charger",
            subtitle = "2x USB-C PD + 1x USB-A QC 4.0+ • Samsung 45W SFC 2.0 & Laptop Ready",
            categoryId = "cat_chargers",
            categoryName = "Mobile chargers and adapters",
            pricePkr = 4899L,
            compareAtPricePkr = 6200L,
            stockQuantity = 30,
            sku = "ANZ-CH-001",
            description = "Powered by 3rd-generation Gallium Nitride (GaN) semiconductors to run cooler in Pakistani summers and handle voltage fluctuations (100V–240V) with built-in surge protection.",
            specsPipeSeparated = "Total Output: 65W Max (GaN III Technology)|Ports: USB-C1 (65W) + USB-C2 (33W) + USB-A (22.5W)|Protocols: PD 3.0, PPS (Samsung 45W Super Fast 2.0), QC 4.0+, SCP|Voltage Protection: Over-Voltage, Short-Circuit & Thermal Guard|Warranty: 1 Year ANZORA Official Warranty",
            primaryImageResName = "img_cat_charging",
            customImageUri = null,
            galleryPipeSeparated = "65W GaN Charger with Electric Blue LED Indicator|Triple Port Output Configuration|Compact Palm-Sized Footprint",
            isFeatured = true,
            isNewArrival = true
        ),
        ProductEntity(
            id = "prod_chargers_2",
            title = "ANZORA TurboCube 33W Dual-Port PD + QC Charger",
            subtitle = "Compact Everyday Fast Adapter for iPhone, Samsung & Xiaomi",
            categoryId = "cat_chargers",
            categoryName = "Mobile chargers and adapters",
            pricePkr = 2450L,
            compareAtPricePkr = 3100L,
            stockQuantity = 52,
            sku = "ANZ-CH-002",
            description = "Pocket-friendly 33W fast charger featuring both USB-C Power Delivery and USB-A Quick Charge 3.0 ports.",
            specsPipeSeparated = "Max Power: 33W PPS / PD 3.0|Ports: 1x Type-C + 1x USB-A|Input Voltage: Wide AC 100-240V 50/60Hz|Casing: Fire-Retardant Matte PC",
            primaryImageResName = "img_hero_banner",
            customImageUri = null,
            galleryPipeSeparated = "Front Port Faceplate|Side Profile & Pin Plug|Size Comparison with Earbuds Case",
            isFeatured = false,
            isNewArrival = false
        ),

        // 6. Smartwatches
        ProductEntity(
            id = "prod_watches_1",
            title = "ANZORA ChronoApex AMOLED Calling Smartwatch",
            subtitle = "1.96\" Always-On Super AMOLED • Bluetooth Calling • Zinc Alloy Frame",
            categoryId = "cat_smartwatches",
            categoryName = "Smartwatches",
            pricePkr = 8999L,
            compareAtPricePkr = 11500L,
            stockQuantity = 18,
            sku = "ANZ-SW-001",
            description = "Ultra-crisp 410x502 pixel AMOLED display visible under direct sunlight. Answer phone calls directly from your wrist, track heart rate, SpO2, sleep stages, and 100+ sports modes.",
            specsPipeSeparated = "Display: 1.96-inch Super AMOLED (60Hz Refresh Rate, 800 Nits)|Calling: Single-Chip Bluetooth 5.3 HD Speaker & Mic|Body: Brushed Midnight Navy Alloy + Digital Crown|Waterproof Rating: IP68 Dust & Water Resistant|Battery: 7-10 Days Typical Usage (Magnetic Dock Included)|Straps: 2 Straps in Box (Navy Silicone + Braided Loop)",
            primaryImageResName = "img_cat_wearables",
            customImageUri = null,
            galleryPipeSeparated = "Electric Blue Chronograph Dial on Charging Dock|Side Rotary Crown & Metallic Frame|Heart Rate & SpO2 Sensor Array",
            isFeatured = true,
            isNewArrival = true
        ),
        ProductEntity(
            id = "prod_watches_2",
            title = "ANZORA FitPulse Rugged Outdoor Smartwatch",
            subtitle = "Military-Grade Shockproof Case • 14-Day Battery • Compass & Barometer",
            categoryId = "cat_smartwatches",
            categoryName = "Smartwatches",
            pricePkr = 7250L,
            compareAtPricePkr = 9200L,
            stockQuantity = 12,
            sku = "ANZ-SW-002",
            description = "Built for northern tours, trekking, and heavy gym routines. Features a 450mAh high-density battery that lasts up to two weeks on a single charge.",
            specsPipeSeparated = "Display: 1.85\" HD Gorilla Glass Tough Screen|Battery Capacity: 450mAh (14 Days Active / 35 Days Standby)|Durability: 3ATM Water Resistant + Shockproof Bezel|Bluetooth Calling: Supported with Quick Dial Pad",
            primaryImageResName = "img_cat_wearables",
            customImageUri = null,
            galleryPipeSeparated = "Rugged Bezel Front View|Tactical Silicone Strap Lock|Outdoor Sports Tracking UI",
            isFeatured = false,
            isNewArrival = false
        ),

        // 7. Ring lights
        ProductEntity(
            id = "prod_ringlight_1",
            title = "ANZORA LuminaStudio 18\" RGB & Bi-Color Ring Light Kit",
            subtitle = "7ft Heavy-Duty Tripod • 3 Phone Holders • Wireless Shutter & Remote",
            categoryId = "cat_ringlights",
            categoryName = "Ring lights",
            pricePkr = 6850L,
            compareAtPricePkr = 8500L,
            stockQuantity = 14,
            sku = "ANZ-RL-001",
            description = "Complete lighting studio for YouTubers, TikTokers, makeup artists, and live e-commerce sellers. Features stepless 2700K–6500K color temperature plus 16 dynamic RGB creative modes.",
            specsPipeSeparated = "Ring Diameter: 18 Inches (45cm) High-CRI 95+ LEDs|Color Temp: 2700K Warm, 4500K Natural, 6500K Daylight + RGB|Tripod Stand: 2.1 Meter (7 Feet) Weighted Aluminum Stand|Mounts: 3x 360° Flexible Gooseneck Smartphone Clamps|Extras: IR Wireless Remote + Bluetooth Selfie Shutter + Carry Bag",
            primaryImageResName = "img_cat_wearables",
            customImageUri = null,
            galleryPipeSeparated = "18-Inch Halo Illumination View|7ft Aluminum Tripod & Triple Phone Mount|Touch Panel & Wireless Remote Controller",
            isFeatured = true,
            isNewArrival = false
        ),
        ProductEntity(
            id = "prod_ringlight_2",
            title = "ANZORA DeskGlow 10\" Creator LED Ring Light",
            subtitle = "Weighted Metal Desktop Stand • USB Powered • 10 Brightness Levels",
            categoryId = "cat_ringlights",
            categoryName = "Ring lights",
            pricePkr = 2750L,
            compareAtPricePkr = 3500L,
            stockQuantity = 27,
            sku = "ANZ-RL-002",
            description = "Compact desktop ring light ideal for Zoom calls, online teaching, product unboxing videos, and vanity mirrors. Runs off any USB port or power bank.",
            specsPipeSeparated = "Ring Size: 10 Inches (26cm)|Lighting Modes: 3 Color Temperatures x 10 Brightness Steps|Stand: Adjustable Telescopic Metal Desk Base (35cm–55cm)|Power: 5V USB Cable with Inline Controller",
            primaryImageResName = "img_cat_wearables",
            customImageUri = null,
            galleryPipeSeparated = "Desktop Stand Setup|Inline Brightness & Color Controller|360-Degree Ball Head Joint",
            isFeatured = false,
            isNewArrival = false
        ),

        // 8. Mobile accessories
        ProductEntity(
            id = "prod_acc_1",
            title = "ANZORA MagCharge 10000mAh 22.5W Magnetic Power Bank",
            subtitle = "15W Wireless MagSafe Snap + 22.5W Wired PD • Foldable Kickstand",
            categoryId = "cat_accessories",
            categoryName = "Mobile accessories",
            pricePkr = 5299L,
            compareAtPricePkr = 6799L,
            stockQuantity = 22,
            sku = "ANZ-AC-001",
            description = "Snap-and-charge convenience with N52 ultra-strong magnets and an integrated aluminum kickstand. Digital LED percentage display shows exact remaining battery.",
            specsPipeSeparated = "Capacity: 10,000mAh High-Density Li-Polymer|Wireless Output: 15W Max MagSafe / Qi Magnetic|Wired Output: 22.5W USB-C PD In/Out|Special Feature: Fold-Out Zinc Alloy Desk Kickstand + LED Display|Warranty: 1 Year ANZORA Official Warranty",
            primaryImageResName = "img_hero_banner",
            customImageUri = null,
            galleryPipeSeparated = "Magnetic Snap & Kickstand View|Digital LED Battery Percentage Screen|Slim Pocket Profile",
            isFeatured = true,
            isNewArrival = true
        ),
        ProductEntity(
            id = "prod_acc_2",
            title = "ANZORA AeroTilt 360° Aluminum Phone & Tablet Stand",
            subtitle = "Rotating Ratchet Base • Foldable CNC Alloy • Supports up to 12.9\" Tablets",
            categoryId = "cat_accessories",
            categoryName = "Mobile accessories",
            pricePkr = 2199L,
            compareAtPricePkr = 2899L,
            stockQuantity = 35,
            sku = "ANZ-AC-002",
            description = "Precision-machined aircraft aluminum stand with satisfying mechanical 360-degree rotation clicks and anti-slip silicone pads.",
            specsPipeSeparated = "Material: 4mm Thick Anodized Aircraft Aluminum|Rotation: 360° Mechanical Swivel Base|Compatibility: All 4\" to 12.9\" Smartphones & Tablets|Foldable: Folds Flat for Laptop Bags",
            primaryImageResName = "img_cat_wearables",
            customImageUri = null,
            galleryPipeSeparated = "Desk Elevation View|360-Degree Mechanical Base|Folded Travel Position",
            isFeatured = false,
            isNewArrival = false
        )
    )
}

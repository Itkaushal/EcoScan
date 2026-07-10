package com.kaushalprajapati.ecoscan.data.local

import com.kaushalprajapati.ecoscan.data.modal.Product

/**
 * Offline product catalog keyed by barcode (EAN/UPC string).
 * Swap this out for a real network-backed repository (e.g. Open Food Facts)
 * without touching any UI code — [com.ecoscan.app.data.repository.EcoRepository]
 * is the only consumer.
 */
object ProductCatalog {

    private val products: List<Product> = listOf(
        Product(
            barcode = "8901030826501",
            name = "Reusable Steel Water Bottle",
            brand = "GreenLife",
            category = "Home & Kitchen",
            imageEmoji = "🍶",
            carbonFootprintKg = 0.8,
            recyclablePackaging = true,
            plasticFree = true,
            sustainableSourcing = true,
            certifications = listOf("BPA-Free", "Recycled Steel"),
            description = "Double-walled stainless steel bottle designed to replace hundreds of single-use bottles."
        ),
        Product(
            barcode = "7622210951348",
            name = "Chocolate Wafer Bars (Pack of 6)",
            brand = "SnackCo",
            category = "Snacks",
            imageEmoji = "🍫",
            carbonFootprintKg = 3.4,
            recyclablePackaging = false,
            plasticFree = false,
            sustainableSourcing = false,
            certifications = emptyList(),
            description = "Individually plastic-wrapped wafer bars in a multipack pouch."
        ),
        Product(
            barcode = "8901063020014",
            name = "Cold-Pressed Coconut Oil",
            brand = "PureHarvest",
            category = "Grocery",
            imageEmoji = "🥥",
            carbonFootprintKg = 1.2,
            recyclablePackaging = true,
            plasticFree = false,
            sustainableSourcing = true,
            certifications = listOf("Fair Trade", "Organic"),
            description = "Cold-pressed coconut oil in a recyclable glass jar with a plastic cap seal."
        ),
        Product(
            barcode = "0012345678905",
            name = "Bamboo Toothbrush",
            brand = "EarthSmile",
            category = "Personal Care",
            imageEmoji = "🪥",
            carbonFootprintKg = 0.3,
            recyclablePackaging = true,
            plasticFree = true,
            sustainableSourcing = true,
            certifications = listOf("FSC Certified", "Compostable"),
            description = "Biodegradable bamboo handle toothbrush, packaged in recycled cardboard."
        ),
        Product(
            barcode = "5901234123457",
            name = "Instant Noodles Cup",
            brand = "QuickBite",
            category = "Grocery",
            imageEmoji = "🍜",
            carbonFootprintKg = 2.6,
            recyclablePackaging = false,
            plasticFree = false,
            sustainableSourcing = false,
            certifications = emptyList(),
            description = "Single-use polystyrene cup with plastic-wrapped seasoning sachets."
        ),
        Product(
            barcode = "4006381333931",
            name = "Organic Cotton T-Shirt",
            brand = "WearFair",
            category = "Apparel",
            imageEmoji = "👕",
            carbonFootprintKg = 2.1,
            recyclablePackaging = true,
            plasticFree = true,
            sustainableSourcing = true,
            certifications = listOf("GOTS Organic", "Fair Trade"),
            description = "100% organic cotton t-shirt, shipped in a compostable mailer."
        ),
        Product(
            barcode = "9310036001234",
            name = "Disposable Razor (5-pack)",
            brand = "SmoothCo",
            category = "Personal Care",
            imageEmoji = "🪒",
            carbonFootprintKg = 1.9,
            recyclablePackaging = false,
            plasticFree = false,
            sustainableSourcing = false,
            certifications = emptyList(),
            description = "Single-use plastic razors sold in a blister pack."
        ),
        Product(
            barcode = "3017620422003",
            name = "Hazelnut Spread",
            brand = "NuttyDelight",
            category = "Grocery",
            imageEmoji = "🍯",
            carbonFootprintKg = 2.9,
            recyclablePackaging = true,
            plasticFree = false,
            sustainableSourcing = false,
            certifications = listOf("Rainforest Alliance"),
            description = "Palm-oil based hazelnut spread in a recyclable glass jar."
        )
    )

    private val byBarcode: Map<String, Product> = products.associateBy { it.barcode }

    fun findByBarcode(barcode: String): Product? = byBarcode[barcode]

    fun all(): List<Product> = products

    /** Used by the manual-entry / demo flow so users without a real barcode can still try the app. */
    fun sampleBarcodes(): List<String> = products.map { it.barcode }
}

package com.kaushalprajapati.ecoscan.data.modal

/**
 * Represents a scannable product and the raw sustainability signals used
 * to compute its eco score. In a production app these fields would be
 * populated from a remote catalog / open product database; here they are
 * seeded locally so the app is fully functional offline.
 */
data class Product(
    val barcode: String,
    val name: String,
    val brand: String,
    val category: String,
    val imageEmoji: String,
    val carbonFootprintKg: Double,      // estimated CO2e per unit
    val recyclablePackaging: Boolean,
    val plasticFree: Boolean,
    val sustainableSourcing: Boolean,
    val certifications: List<String> = emptyList(),
    val description: String = ""
)

/**
 * Result of running a [Product] through the [com.ecoscan.app.util.EcoScoreEngine].
 */
data class EcoScoreResult(
    val score: Int,             // 0-100
    val grade: EcoGrade,
    val breakdown: List<ScoreFactor>,
    val tips: List<String>
)

data class ScoreFactor(
    val label: String,
    val points: Int,
    val maxPoints: Int,
    val positive: Boolean
)

enum class EcoGrade(val label: String, val colorHex: Long) {
    A(label = "A", colorHex = 0xFF2E7D32),
    B(label = "B", colorHex = 0xFF66BB6A),
    C(label = "C", colorHex = 0xFFFBC02D),
    D(label = "D", colorHex = 0xFFEF6C00),
    F(label = "F", colorHex = 0xFFC62828)
}

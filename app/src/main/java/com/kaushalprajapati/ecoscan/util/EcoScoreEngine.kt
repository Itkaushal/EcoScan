package com.kaushalprajapati.ecoscan.util

import com.kaushalprajapati.ecoscan.data.modal.EcoGrade
import com.kaushalprajapati.ecoscan.data.modal.EcoScoreResult
import com.kaushalprajapati.ecoscan.data.modal.Product
import com.kaushalprajapati.ecoscan.data.modal.ScoreFactor


/**
 * Deterministic, explainable scoring engine. Keeping this pure (no I/O,
 * no Android framework classes) makes it trivial to unit test.
 */
object EcoScoreEngine {

    private const val MAX_CARBON_POINTS = 40
    private const val MAX_PACKAGING_POINTS = 20
    private const val MAX_PLASTIC_POINTS = 15
    private const val MAX_SOURCING_POINTS = 15
    private const val MAX_CERT_POINTS = 10

    fun score(product: Product): EcoScoreResult {
        val factors = mutableListOf<ScoreFactor>()

        // Lower carbon footprint => more points. Anything >= 5kg CO2e scores 0.
        val carbonPoints = (MAX_CARBON_POINTS * (1 - (product.carbonFootprintKg / 5.0)).coerceIn(0.0, 1.0)).toInt()
        factors += ScoreFactor(
            label = "Carbon footprint (${"%.1f".format(product.carbonFootprintKg)} kg CO2e)",
            points = carbonPoints,
            maxPoints = MAX_CARBON_POINTS,
            positive = carbonPoints >= MAX_CARBON_POINTS / 2
        )

        val packagingPoints = if (product.recyclablePackaging) MAX_PACKAGING_POINTS else 4
        factors += ScoreFactor(
            label = "Recyclable packaging",
            points = packagingPoints,
            maxPoints = MAX_PACKAGING_POINTS,
            positive = product.recyclablePackaging
        )

        val plasticPoints = if (product.plasticFree) MAX_PLASTIC_POINTS else 3
        factors += ScoreFactor(
            label = "Plastic-free materials",
            points = plasticPoints,
            maxPoints = MAX_PLASTIC_POINTS,
            positive = product.plasticFree
        )

        val sourcingPoints = if (product.sustainableSourcing) MAX_SOURCING_POINTS else 2
        factors += ScoreFactor(
            label = "Sustainable sourcing",
            points = sourcingPoints,
            maxPoints = MAX_SOURCING_POINTS,
            positive = product.sustainableSourcing
        )

        val certPoints = (product.certifications.size * 4).coerceAtMost(MAX_CERT_POINTS)
        factors += ScoreFactor(
            label = "Certifications (${product.certifications.size})",
            points = certPoints,
            maxPoints = MAX_CERT_POINTS,
            positive = certPoints > 0
        )

        val total = factors.sumOf { it.points }.coerceIn(0, 100)
        val grade = gradeFor(total)

        return EcoScoreResult(
            score = total,
            grade = grade,
            breakdown = factors,
            tips = buildTips(product, factors)
        )
    }

    private fun gradeFor(score: Int): EcoGrade = when {
        score >= 85 -> EcoGrade.A
        score >= 70 -> EcoGrade.B
        score >= 50 -> EcoGrade.C
        score >= 30 -> EcoGrade.D
        else -> EcoGrade.F
    }

    private fun buildTips(product: Product, factors: List<ScoreFactor>): List<String> {
        val tips = mutableListOf<String>()
        if (!product.recyclablePackaging) tips += "Look for a version with recyclable or refillable packaging."
        if (!product.plasticFree) tips += "Consider plastic-free alternatives in this category."
        if (!product.sustainableSourcing) tips += "Check the brand's sourcing policy before repurchasing."
        if (product.carbonFootprintKg >= 3.0) tips += "This item has a relatively high carbon footprint — buying less often helps."
        if (product.certifications.isEmpty()) tips += "No eco-certifications found; verify claims independently."
        if (tips.isEmpty()) tips += "Great choice! This product performs well across all tracked factors."
        return tips
    }
}

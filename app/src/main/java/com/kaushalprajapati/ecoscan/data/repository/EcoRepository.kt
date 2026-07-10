package com.kaushalprajapati.ecoscan.data.repository

import com.kaushalprajapati.ecoscan.data.local.ProductCatalog
import com.kaushalprajapati.ecoscan.data.local.ScanHistoryDao
import com.kaushalprajapati.ecoscan.data.local.ScanHistoryEntity
import com.kaushalprajapati.ecoscan.data.modal.EcoGrade
import com.kaushalprajapati.ecoscan.data.modal.EcoScoreResult
import com.kaushalprajapati.ecoscan.data.modal.Product
import com.kaushalprajapati.ecoscan.data.remote.OpenFoodFactsApi
import com.kaushalprajapati.ecoscan.util.EcoScoreEngine

import kotlinx.coroutines.flow.Flow

sealed class ScanLookupResult {
    data class Found(val product: Product, val score: EcoScoreResult) : ScanLookupResult()
    data class NotFound(val barcode: String) : ScanLookupResult()
    data object Loading : ScanLookupResult()
    data class Error(val message: String) : ScanLookupResult()
}

/**
 * Single source of truth for the UI layer. ViewModels only ever talk to
 * this class, never directly to Room or the catalog.
 */
class EcoRepository(
    private val api: OpenFoodFactsApi,
    private val scanHistoryDao: ScanHistoryDao
) {

    suspend fun lookup(barcode: String): ScanLookupResult {
        // First check local catalog
        val localProduct = ProductCatalog.findByBarcode(barcode)
        if (localProduct != null) {
            val score = EcoScoreEngine.score(localProduct)
            return ScanLookupResult.Found(localProduct, score)
        }

        // Then check remote API
        return try {
            val response = api.getProduct(barcode)
            if (response.status == 1 && response.product != null) {
                val offProduct = response.product
                val product = Product(
                    barcode = offProduct.barcode,
                    name = offProduct.productName ?: "Unknown Product",
                    brand = offProduct.brands ?: "Unknown Brand",
                    category = offProduct.categories ?: "Unknown Category",
                    imageEmoji = "📦", // Default for remote
                    carbonFootprintKg = 0.0, // OFF doesn't provide this directly easily
                    recyclablePackaging = offProduct.packaging?.contains("recycl", ignoreCase = true) ?: false,
                    plasticFree = false,
                    sustainableSourcing = false,
                    description = offProduct.ingredients ?: ""
                )
                
                // Map OFF eco-score if available
                val grade = when (offProduct.ecoScoreGrade?.lowercase()) {
                    "a" -> EcoGrade.A
                    "b" -> EcoGrade.B
                    "c" -> EcoGrade.C
                    "d" -> EcoGrade.D
                    "e" -> EcoGrade.F
                    else -> EcoGrade.C // Default
                }
                
                val scoreResult = EcoScoreResult(
                    score = offProduct.ecoScore ?: 50,
                    grade = grade,
                    breakdown = emptyList(),
                    tips = listOf("Information fetched from Open Food Facts")
                )
                
                ScanLookupResult.Found(product, scoreResult)
            } else {
                ScanLookupResult.NotFound(barcode)
            }
        } catch (e: Exception) {
            ScanLookupResult.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun recordScan(product: Product, score: EcoScoreResult) {
        scanHistoryDao.insert(
            ScanHistoryEntity(
                barcode = product.barcode,
                productName = product.name,
                brand = product.brand,
                imageEmoji = product.imageEmoji,
                ecoScore = score.score,
                ecoGrade = score.grade.label,
                scannedAtEpochMillis = System.currentTimeMillis()
            )
        )
    }

    fun observeHistory(): Flow<List<ScanHistoryEntity>> = scanHistoryDao.observeAll()

    fun observeScanCount(): Flow<Int> = scanHistoryDao.observeCount()

    fun observeAverageScore(): Flow<Double?> = scanHistoryDao.observeAverageScore()

    suspend fun deleteHistoryItem(entity: ScanHistoryEntity) = scanHistoryDao.delete(entity)

    suspend fun clearHistory() = scanHistoryDao.clearAll()

    fun sampleBarcodesForDemo(): List<String> = ProductCatalog.sampleBarcodes()
}

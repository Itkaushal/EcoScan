package com.kaushalprajapati.ecoscan.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Headers

interface OpenFoodFactsApi {
    @Headers("User-Agent: EcoScan - Android - Version 1.0")
    @GET("api/v2/product/{barcode}.json")
    suspend fun getProduct(@Path("barcode") barcode: String): OffResponse
}

data class OffResponse(
    @SerializedName("status") val status: Int,
    @SerializedName("product") val product: OffProduct?
)

data class OffProduct(
    @SerializedName("_id") val barcode: String,
    @SerializedName("product_name") val productName: String?,
    @SerializedName("brands") val brands: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("ecoscore_grade") val ecoScoreGrade: String?,
    @SerializedName("ecoscore_score") val ecoScore: Int?,
    @SerializedName("categories") val categories: String?,
    @SerializedName("packaging") val packaging: String?,
    @SerializedName("ingredients_text") val ingredients: String?
)

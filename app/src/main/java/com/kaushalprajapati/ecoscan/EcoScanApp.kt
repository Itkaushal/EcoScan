package com.kaushalprajapati.ecoscan

import android.app.Application
import com.kaushalprajapati.ecoscan.data.local.AppDatabase
import com.kaushalprajapati.ecoscan.data.pref.UserPreferences
import com.kaushalprajapati.ecoscan.data.remote.OpenFoodFactsApi
import com.kaushalprajapati.ecoscan.data.repository.EcoRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EcoScanApp : Application() {
    lateinit var repository: EcoRepository
        private set

    lateinit var userPreferences: UserPreferences
        private set

    override fun onCreate() {
        super.onCreate()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(OpenFoodFactsApi::class.java)

        userPreferences = UserPreferences(this)
        val db = AppDatabase.getInstance(this)
        repository = EcoRepository(api, scanHistoryDao = db.scanHistoryDao())
    }
}

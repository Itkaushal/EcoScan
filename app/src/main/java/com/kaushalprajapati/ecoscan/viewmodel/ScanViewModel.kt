package com.kaushalprajapati.ecoscan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaushalprajapati.ecoscan.data.local.ScanHistoryEntity
import com.kaushalprajapati.ecoscan.data.modal.EcoScoreResult
import com.kaushalprajapati.ecoscan.data.modal.Product
import com.kaushalprajapati.ecoscan.data.repository.EcoRepository
import com.kaushalprajapati.ecoscan.data.repository.ScanLookupResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ScanUiState {
    data object Idle : ScanUiState()
    data object Scanning : ScanUiState()
    data class Success(val product: Product, val score: EcoScoreResult) : ScanUiState()
    data class NotFound(val barcode: String) : ScanUiState()
    data object Loading : ScanUiState()
    data class Error(val message: String) : ScanUiState()
}

class ScanViewModel(private val repository: EcoRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val uiState: StateFlow<ScanUiState> = _uiState

    // Prevents the same barcode from being logged repeatedly while the
    // camera keeps detecting it in consecutive frames.
    private var lastProcessedBarcode: String? = null

    val history: StateFlow<List<ScanHistoryEntity>> =
        repository.observeHistory().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scanCount: StateFlow<Int> =
        repository.observeScanCount().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val averageScore: StateFlow<Double?> =
        repository.observeAverageScore().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun onBarcodeDetected(barcode: String) {
        if (barcode == lastProcessedBarcode) return
        lastProcessedBarcode = barcode
        processBarcode(barcode)
    }

    fun onManualBarcodeEntered(barcode: String) {
        lastProcessedBarcode = barcode
        processBarcode(barcode)
    }

    private fun processBarcode(barcode: String) {
        _uiState.value = ScanUiState.Loading
        viewModelScope.launch {
            when (val result = repository.lookup(barcode)) {
                is ScanLookupResult.Found -> {
                    _uiState.value = ScanUiState.Success(result.product, result.score)
                    repository.recordScan(result.product, result.score)
                }
                is ScanLookupResult.NotFound -> {
                    _uiState.value = ScanUiState.NotFound(result.barcode)
                }
                is ScanLookupResult.Loading -> {
                    _uiState.value = ScanUiState.Loading
                }
                is ScanLookupResult.Error -> {
                    _uiState.value = ScanUiState.Error(result.message)
                }
            }
        }
    }

    fun resetScan() {
        lastProcessedBarcode = null
        _uiState.value = ScanUiState.Idle
    }

    fun deleteHistoryItem(entity: ScanHistoryEntity) {
        viewModelScope.launch { repository.deleteHistoryItem(entity) }
    }

    fun clearHistory() {
        viewModelScope.launch { repository.clearHistory() }
    }

    fun sampleBarcodes(): List<String> = repository.sampleBarcodesForDemo()

    companion object {
        fun factory(repository: EcoRepository) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ScanViewModel(repository) as T
            }
        }
    }
}

package com.dylphiiee.piecalculator.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dylphiiee.piecalculator.data.RetrofitClient
import kotlinx.coroutines.launch

sealed class CurrencyUiState {
    object Idle : CurrencyUiState()
    object Loading : CurrencyUiState()
    data class Success(val result: Double, val rate: Double, val lastUpdated: String) : CurrencyUiState()
    data class Error(val message: String) : CurrencyUiState()
}

class CurrencyViewModel : ViewModel() {

    var amountInput by mutableStateOf("1")
    var fromCurrency by mutableStateOf("USD")
    var toCurrency by mutableStateOf("IDR")
    var uiState by mutableStateOf<CurrencyUiState>(CurrencyUiState.Idle)
        private set

    fun swapCurrencies() {
        val temp = fromCurrency
        fromCurrency = toCurrency
        toCurrency = temp
    }

    fun convert() {
        val amount = amountInput.toDoubleOrNull()
        if (amount == null) {
            uiState = CurrencyUiState.Error("Masukkan jumlah yang valid")
            return
        }
        if (fromCurrency == toCurrency) {
            uiState = CurrencyUiState.Success(amount, 1.0, "-")
            return
        }

        uiState = CurrencyUiState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitClient.frankfurterApi.getLatestRates(fromCurrency)
                val rate = response.rates[toCurrency]
                if (rate == null) {
                    uiState = CurrencyUiState.Error("Kurs $toCurrency tidak tersedia")
                } else {
                    uiState = CurrencyUiState.Success(
                        result = amount * rate,
                        rate = rate,
                        lastUpdated = response.date
                    )
                }
            } catch (e: Exception) {
                uiState = CurrencyUiState.Error("Gagal mengambil kurs. Periksa koneksi internet.")
            }
        }
    }
}

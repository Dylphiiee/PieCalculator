package com.dylphiiee.piecalculator.data

import retrofit2.http.GET
import retrofit2.http.Query

data class FrankfurterResponse(
    val amount: Double,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)

interface FrankfurterApi {
    /** Ambil kurs terbaru relatif terhadap mata uang [from]. Gratis, tanpa API key. */
    @GET("latest")
    suspend fun getLatestRates(@Query("from") from: String): FrankfurterResponse
}

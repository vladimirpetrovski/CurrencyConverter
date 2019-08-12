package com.vladimirpetrovski.currencyconverter.data.network

import com.vladimirpetrovski.currencyconverter.data.network.model.response.RatesResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface RatesService {

    @GET("/latest")
    fun getLatestRates(@Query("base") baseCurrency: String): Single<RatesResponse>
}
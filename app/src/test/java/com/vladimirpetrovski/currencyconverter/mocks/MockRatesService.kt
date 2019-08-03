package com.vladimirpetrovski.currencyconverter.mocks

import com.vladimirpetrovski.currencyconverter.data.network.RatesService
import com.vladimirpetrovski.currencyconverter.data.network.model.response.RatesResponse
import io.reactivex.Single

class MockRatesService : RatesService {

    override fun getLatestRates(baseCurrency: String): Single<RatesResponse> {
        val response = RatesResponse(
            "EUR",
            mapOf("HRK" to 7.43, "USD" to 1.16, "AUD" to 1.61)
        )
        return Single.just(response)
    }
}
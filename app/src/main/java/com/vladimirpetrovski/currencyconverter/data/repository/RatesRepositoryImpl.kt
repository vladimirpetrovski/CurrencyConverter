package com.vladimirpetrovski.currencyconverter.data.repository

import com.vladimirpetrovski.currencyconverter.data.network.RatesService
import com.vladimirpetrovski.currencyconverter.domain.model.CalculatedRate
import com.vladimirpetrovski.currencyconverter.domain.model.Rate
import com.vladimirpetrovski.currencyconverter.domain.repository.RatesRepository
import io.reactivex.Single
import javax.inject.Inject

class RatesRepositoryImpl @Inject constructor(
    private val ratesService: RatesService
) : RatesRepository {

    override val cachedLatestRates = mutableListOf<Rate>()

    override fun fetchLatestRates(baseCurrency: String): Single<List<Rate>> {
        return ratesService.getLatestRates(baseCurrency)
            .map { response ->
                return@map response.rates.map { rate ->
                    Rate(
                        currency = rate.key,
                        rate = rate.value
                    )
                }
            }
            .doOnSuccess {
                cachedLatestRates.clear()
                cachedLatestRates.addAll(it)
            }
    }

    override val cachedCalculatedRates = mutableListOf<CalculatedRate>()

    override fun clear() {
        cachedLatestRates.clear()
        cachedCalculatedRates.clear()
    }
}

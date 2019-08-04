package com.vladimirpetrovski.currencyconverter.domain.repository

import com.vladimirpetrovski.currencyconverter.domain.model.CalculatedRate
import com.vladimirpetrovski.currencyconverter.domain.model.Rate
import io.reactivex.Single

interface RatesRepository {

    val cachedLatestRates: MutableList<Rate>

    fun fetchLatestRates(baseCurrency: String): Single<List<Rate>>

    val cachedCalculatedRates: MutableList<CalculatedRate>

    fun clear()
}

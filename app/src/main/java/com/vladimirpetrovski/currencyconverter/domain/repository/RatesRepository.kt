package com.vladimirpetrovski.currencyconverter.domain.repository

import com.vladimirpetrovski.currencyconverter.domain.model.CalculatedRate
import com.vladimirpetrovski.currencyconverter.domain.model.Rate
import io.reactivex.Flowable
import io.reactivex.Single

interface RatesRepository {

    var cachedLatestRates: List<Rate>

    fun fetchLatestRates(baseCurrency: String): Single<List<Rate>>

    var cachedCalculatedRates: List<CalculatedRate>

    fun observeCachedCalculatedRatesChanges(): Flowable<List<CalculatedRate>>

    fun clear()
}

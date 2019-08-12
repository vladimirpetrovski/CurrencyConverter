package com.vladimirpetrovski.currencyconverter.data.repository

import com.vladimirpetrovski.currencyconverter.data.network.RatesService
import com.vladimirpetrovski.currencyconverter.domain.model.CalculatedRate
import com.vladimirpetrovski.currencyconverter.domain.model.Rate
import com.vladimirpetrovski.currencyconverter.domain.repository.RatesRepository
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class RatesRepositoryImpl @Inject constructor(
    private val ratesService: RatesService
) : RatesRepository {

    private val cachedCalculatedRatesObservable =
        BehaviorSubject.createDefault(emptyList<CalculatedRate>())

    override var cachedLatestRates: List<Rate> = emptyList()

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
                cachedLatestRates = it
            }
    }

    override var cachedCalculatedRates: List<CalculatedRate> = emptyList()
        set(value) {
            field = value
            cachedCalculatedRatesObservable.onNext(value)
        }

    override fun observeCachedCalculatedRatesChanges(): Flowable<List<CalculatedRate>> {
        return cachedCalculatedRatesObservable.toFlowable(BackpressureStrategy.BUFFER)
    }

    override fun clear() {
        cachedLatestRates = emptyList()
        cachedCalculatedRates = emptyList()
    }
}

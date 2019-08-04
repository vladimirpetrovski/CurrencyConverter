package com.vladimirpetrovski.currencyconverter.domain.usecase

import com.vladimirpetrovski.currencyconverter.domain.CalculateRatesHelper.calculate
import com.vladimirpetrovski.currencyconverter.domain.model.CalculatedRate
import com.vladimirpetrovski.currencyconverter.domain.repository.RatesRepository
import io.reactivex.Single
import javax.inject.Inject

/**
 * Recalculate given amount with rates from the cache.
 *
 * @return new recalculated list.
 */
class RecalculateRatesUseCase @Inject constructor(
    private val ratesRepository: RatesRepository
) {

    operator fun invoke(amount: Double): Single<List<CalculatedRate>> {
        val list = calculate(
            ratesRepository.cachedCalculatedRates,
            amount,
            ratesRepository.cachedLatestRates
        )
        ratesRepository.cachedCalculatedRates.clear()
        ratesRepository.cachedCalculatedRates.addAll(list)
        return Single.just(list)
    }
}

package com.vladimirpetrovski.currencyconverter.domain.usecase

import com.vladimirpetrovski.currencyconverter.domain.CalculateRatesHelper.calculate
import com.vladimirpetrovski.currencyconverter.domain.CalculateRatesHelper.initialCalculate
import com.vladimirpetrovski.currencyconverter.domain.model.CalculatedRate
import com.vladimirpetrovski.currencyconverter.domain.repository.RatesRepository
import io.reactivex.Single
import javax.inject.Inject

/**
 * Fetch rates from network and calculate given currency and amount with the newest rates.
 *
 * @return new calculated list.
 */
class FetchRatesUseCase @Inject constructor(
    private val ratesRepository: RatesRepository
) {

    operator fun invoke(
        baseCurrency: String,
        amount: Double
    ): Single<List<CalculatedRate>> {
        return ratesRepository.fetchLatestRates(baseCurrency)
            .map { latestRates ->
                if (ratesRepository.cachedCalculatedRates.isEmpty()) {
                    val list = initialCalculate(amount, latestRates)
                    ratesRepository.cachedCalculatedRates = list
                    return@map list
                }
                val list = calculate(ratesRepository.cachedCalculatedRates, amount, latestRates)
                ratesRepository.cachedCalculatedRates = list
                return@map list
            }
    }
}

package com.vladimirpetrovski.currencyconverter.domain.usecase

import com.vladimirpetrovski.currencyconverter.domain.CalculateRatesHelper
import com.vladimirpetrovski.currencyconverter.domain.model.CalculatedRate
import com.vladimirpetrovski.currencyconverter.domain.repository.RatesRepository
import io.reactivex.Single
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Fetch rates from network and calculate given currency and amount with the newest rates.
 *
 * @return new calculated list.
 */
class FetchRatesUseCase @Inject constructor(
    private val ratesRepository: RatesRepository,
    private val calculateRatesHelper: CalculateRatesHelper
) {

    operator fun invoke(
        baseCurrency: String,
        amount: BigDecimal
    ): Single<List<CalculatedRate>> {
        return ratesRepository.fetchLatestRates(baseCurrency)
            .map { latestRates ->
                if (ratesRepository.cachedCalculatedRates.isEmpty()) {
                    val list = calculateRatesHelper.initialCalculate(amount, latestRates)
                    ratesRepository.cachedCalculatedRates = list
                    return@map list
                }
                val list = calculateRatesHelper.calculate(
                    ratesRepository.cachedCalculatedRates,
                    amount,
                    latestRates
                )
                ratesRepository.cachedCalculatedRates = list
                return@map list
            }
    }
}

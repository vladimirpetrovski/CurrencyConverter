package com.vladimirpetrovski.currencyconverter.domain.usecase

import com.vladimirpetrovski.currencyconverter.domain.model.CalculatedRate
import com.vladimirpetrovski.currencyconverter.domain.repository.RatesRepository
import io.reactivex.Single
import javax.inject.Inject

/**
 * Select given currency and move that list item to the top.
 *
 * @return new list with the given currency to the top.
 */
class SelectRateUseCase @Inject constructor(
    private val ratesRepository: RatesRepository
) {

    operator fun invoke(currentCurrency: String): Single<List<CalculatedRate>> {
        val oldIndex = ratesRepository.cachedCalculatedRates
            .indexOfFirst { rate -> rate.currency == currentCurrency }
        ratesRepository.cachedCalculatedRates.add(
            0,
            ratesRepository.cachedCalculatedRates.removeAt(oldIndex)
        )
        return Single.just(ratesRepository.cachedCalculatedRates)
    }
}

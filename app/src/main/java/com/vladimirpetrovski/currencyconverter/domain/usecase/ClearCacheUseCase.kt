package com.vladimirpetrovski.currencyconverter.domain.usecase

import com.vladimirpetrovski.currencyconverter.domain.repository.RatesRepository
import javax.inject.Inject

/**
 * Clear repositories.
 */
class ClearCacheUseCase @Inject constructor(
    private val ratesRepository: RatesRepository
) {

    operator fun invoke() {
        ratesRepository.clear()
    }
}

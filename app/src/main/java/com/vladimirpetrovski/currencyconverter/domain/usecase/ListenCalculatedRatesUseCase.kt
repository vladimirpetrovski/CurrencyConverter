package com.vladimirpetrovski.currencyconverter.domain.usecase

import com.vladimirpetrovski.currencyconverter.domain.model.CalculatedRate
import com.vladimirpetrovski.currencyconverter.domain.repository.RatesRepository
import io.reactivex.Flowable
import javax.inject.Inject

class ListenCalculatedRatesUseCase @Inject constructor(
    private val ratesRepository: RatesRepository
) {

    operator fun invoke(): Flowable<List<CalculatedRate>> {
        return ratesRepository.observeCachedCalculatedRatesChanges()
    }
}

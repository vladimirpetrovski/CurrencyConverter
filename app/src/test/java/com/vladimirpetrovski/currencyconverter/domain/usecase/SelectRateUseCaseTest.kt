package com.vladimirpetrovski.currencyconverter.domain.usecase

import com.vladimirpetrovski.currencyconverter.domain.model.CalculatedRate
import com.vladimirpetrovski.currencyconverter.domain.model.Rate
import com.vladimirpetrovski.currencyconverter.domain.repository.RatesRepository
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class SelectRateUseCaseTest {

    private val repo: RatesRepository = mock(RatesRepository::class.java)

    private val useCase = SelectRateUseCase(repo)

    @Before
    fun setup() {
        val rates = listOf(
            Rate(
                currency = "HRK",
                rate = 7.43
            ), Rate(
                currency = "USD",
                rate = 1.16
            ), Rate(
                currency = "AUD",
                rate = 1.61
            )
        )
        `when`(repo.fetchLatestRates("EUR")).thenReturn(Single.just(rates))
        val givenRates = mutableListOf(
            CalculatedRate(
                currency = "EUR",
                flagUrl = "https://www.countryflags.io/EU/flat/64.png",
                description = "Euro",
                amount = 1.0,
                isEnabled = true
            ), CalculatedRate(
                currency = "HRK",
                flagUrl = "https://www.countryflags.io/HR/flat/64.png",
                description = "Kuna",
                amount = 7.43,
                isEnabled = false
            ), CalculatedRate(
                currency = "USD",
                flagUrl = "https://www.countryflags.io/US/flat/64.png",
                description = "US Dollar",
                amount = 1.16,
                isEnabled = false
            ), CalculatedRate(
                currency = "AUD",
                flagUrl = "https://www.countryflags.io/AU/flat/64.png",
                description = "Australian Dollar",
                amount = 1.61,
                isEnabled = false
            )
        )
        `when`(repo.cachedCalculatedRates).thenReturn(givenRates)
    }

    @Test
    fun `select use case should move item as first`() {
        // Given
        val currency = "HRK"

        // When
        val test = useCase(currency)
            .test()

        // Then
        val expected = listOf(
            CalculatedRate(
                currency = "HRK",
                flagUrl = "https://www.countryflags.io/HR/flat/64.png",
                description = "Kuna",
                amount = 7.43,
                isEnabled = false
            ),
            CalculatedRate(
                currency = "EUR",
                flagUrl = "https://www.countryflags.io/EU/flat/64.png",
                description = "Euro",
                amount = 1.0,
                isEnabled = true
            ), CalculatedRate(
                currency = "USD",
                flagUrl = "https://www.countryflags.io/US/flat/64.png",
                description = "US Dollar",
                amount = 1.16,
                isEnabled = false
            ), CalculatedRate(
                currency = "AUD",
                flagUrl = "https://www.countryflags.io/AU/flat/64.png",
                description = "Australian Dollar",
                amount = 1.61,
                isEnabled = false
            )
        )
        verify(repo).cachedCalculatedRates = expected
    }
}

package com.vladimirpetrovski.currencyconverter.domain.usecase

import com.vladimirpetrovski.currencyconverter.domain.model.CalculatedRate
import com.vladimirpetrovski.currencyconverter.domain.model.Rate
import com.vladimirpetrovski.currencyconverter.domain.repository.RatesRepository
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class FetchRatesUseCaseTest {

    private val repo: RatesRepository = mock(RatesRepository::class.java)

    private val useCase = FetchRatesUseCase(repo)

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
    }

    @Test
    fun `fetch use case should match calculated list of rates - initial calculation`() {
        // Given
        val currency = "EUR"
        val amount = 10.0

        // When
        val test = useCase(currency, amount)
            .test()

        // Then
        val expected = listOf(
            CalculatedRate(
                currency = "EUR",
                flagUrl = "https://www.countryflags.io/EU/flat/64.png",
                description = "Euro",
                amount = 10.0,
                isEnabled = true
            ), CalculatedRate(
                currency = "HRK",
                flagUrl = "https://www.countryflags.io/HR/flat/64.png",
                description = "Kuna",
                amount = 74.3,
                isEnabled = false
            ), CalculatedRate(
                currency = "USD",
                flagUrl = "https://www.countryflags.io/US/flat/64.png",
                description = "US Dollar",
                amount = 11.6,
                isEnabled = false
            ), CalculatedRate(
                currency = "AUD",
                flagUrl = "https://www.countryflags.io/AU/flat/64.png",
                description = "Australian Dollar",
                amount = 16.1,
                isEnabled = false
            )
        )

        test.assertValue(expected)
    }

    @Test
    fun `fetch use case should match calculated list of rates - normal calculation`() {
        // Given
        val currency = "EUR"
        val amount = 20.0
        val given = mutableListOf(
            CalculatedRate(
                currency = "EUR",
                flagUrl = "https://www.countryflags.io/EU/flat/64.png",
                description = "Euro",
                amount = 10.0,
                isEnabled = true
            ), CalculatedRate(
                currency = "HRK",
                flagUrl = "https://www.countryflags.io/HR/flat/64.png",
                description = "Kuna",
                amount = 74.3,
                isEnabled = false
            ), CalculatedRate(
                currency = "USD",
                flagUrl = "https://www.countryflags.io/US/flat/64.png",
                description = "US Dollar",
                amount = 11.6,
                isEnabled = false
            ), CalculatedRate(
                currency = "AUD",
                flagUrl = "https://www.countryflags.io/AU/flat/64.png",
                description = "Australian Dollar",
                amount = 16.1,
                isEnabled = false
            )
        )
        `when`(repo.cachedCalculatedRates).thenReturn(given)

        // When
        val test = useCase(currency, amount)
            .test()

        // Then
        val expected = listOf(
            CalculatedRate(
                currency = "EUR",
                flagUrl = "https://www.countryflags.io/EU/flat/64.png",
                description = "Euro",
                amount = 20.0,
                isEnabled = true
            ), CalculatedRate(
                currency = "HRK",
                flagUrl = "https://www.countryflags.io/HR/flat/64.png",
                description = "Kuna",
                amount = 148.6,
                isEnabled = false
            ), CalculatedRate(
                currency = "USD",
                flagUrl = "https://www.countryflags.io/US/flat/64.png",
                description = "US Dollar",
                amount = 23.2,
                isEnabled = false
            ), CalculatedRate(
                currency = "AUD",
                flagUrl = "https://www.countryflags.io/AU/flat/64.png",
                description = "Australian Dollar",
                amount = 32.2,
                isEnabled = false
            )
        )

        test.assertValue(expected)
    }
}

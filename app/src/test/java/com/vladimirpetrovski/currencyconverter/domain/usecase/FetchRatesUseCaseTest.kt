package com.vladimirpetrovski.currencyconverter.domain.usecase

import com.vladimirpetrovski.currencyconverter.domain.model.CalculatedRate
import com.vladimirpetrovski.currencyconverter.domain.model.Rate
import com.vladimirpetrovski.currencyconverter.domain.repository.RatesRepository
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.math.MathContext

class FetchRatesUseCaseTest {

    private val repo: RatesRepository = mock(RatesRepository::class.java)

    private val useCase = FetchRatesUseCase(repo)

    @Before
    fun setup() {
        val rates = listOf(
            Rate(
                currency = "HRK",
                rate = 7.43.toBigDecimal()
            ), Rate(
                currency = "USD",
                rate = 1.16.toBigDecimal()
            ), Rate(
                currency = "AUD",
                rate = 1.61.toBigDecimal()
            )
        )
        `when`(repo.fetchLatestRates("EUR")).thenReturn(Single.just(rates))
    }

    @Test
    fun `fetch use case should match calculated list of rates - initial calculation`() {
        // Given
        val currency = "EUR"
        val amount = 10.toBigDecimal()

        // When
        val test = useCase(currency, amount)
            .test()

        // Then
        val expected = listOf(
            CalculatedRate(
                currency = "EUR",
                flagUrl = "https://www.countryflags.io/EU/flat/64.png",
                description = "Euro",
                amount = 10.toBigDecimal(),
                isEnabled = true
            ), CalculatedRate(
                currency = "HRK",
                flagUrl = "https://www.countryflags.io/HR/flat/64.png",
                description = "Kuna",
                amount = 74.3.toBigDecimal().setScale(2),
                isEnabled = false
            ), CalculatedRate(
                currency = "USD",
                flagUrl = "https://www.countryflags.io/US/flat/64.png",
                description = "US Dollar",
                amount = 11.6.toBigDecimal().setScale(2),
                isEnabled = false
            ), CalculatedRate(
                currency = "AUD",
                flagUrl = "https://www.countryflags.io/AU/flat/64.png",
                description = "Australian Dollar",
                amount = 16.1.toBigDecimal().setScale(2),
                isEnabled = false
            )
        )

        test.assertValue(expected)
    }

    @Test
    fun `fetch use case should match calculated list of rates - normal calculation`() {
        // Given
        val currency = "EUR"
        val amount = 20.toBigDecimal()
        val given = mutableListOf(
            CalculatedRate(
                currency = "EUR",
                flagUrl = "https://www.countryflags.io/EU/flat/64.png",
                description = "Euro",
                amount = 10.0.toBigDecimal(),
                isEnabled = true
            ), CalculatedRate(
                currency = "HRK",
                flagUrl = "https://www.countryflags.io/HR/flat/64.png",
                description = "Kuna",
                amount = 74.3.toBigDecimal(),
                isEnabled = false
            ), CalculatedRate(
                currency = "USD",
                flagUrl = "https://www.countryflags.io/US/flat/64.png",
                description = "US Dollar",
                amount = 11.6.toBigDecimal(),
                isEnabled = false
            ), CalculatedRate(
                currency = "AUD",
                flagUrl = "https://www.countryflags.io/AU/flat/64.png",
                description = "Australian Dollar",
                amount = 16.1.toBigDecimal(),
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
                amount = 20.toBigDecimal(),
                isEnabled = true
            ), CalculatedRate(
                currency = "HRK",
                flagUrl = "https://www.countryflags.io/HR/flat/64.png",
                description = "Kuna",
                amount = 148.6.toBigDecimal().setScale(2),
                isEnabled = false
            ), CalculatedRate(
                currency = "USD",
                flagUrl = "https://www.countryflags.io/US/flat/64.png",
                description = "US Dollar",
                amount = 23.2.toBigDecimal().setScale(2),
                isEnabled = false
            ), CalculatedRate(
                currency = "AUD",
                flagUrl = "https://www.countryflags.io/AU/flat/64.png",
                description = "Australian Dollar",
                amount = 32.2.toBigDecimal().setScale(2),
                isEnabled = false
            )
        )

        test.assertValue(expected)
    }
}

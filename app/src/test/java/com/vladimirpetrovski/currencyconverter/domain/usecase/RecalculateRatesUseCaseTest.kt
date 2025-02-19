package com.vladimirpetrovski.currencyconverter.domain.usecase

import com.vladimirpetrovski.currencyconverter.domain.CalculateRatesHelper
import com.vladimirpetrovski.currencyconverter.domain.model.CalculatedRate
import com.vladimirpetrovski.currencyconverter.domain.model.Rate
import com.vladimirpetrovski.currencyconverter.domain.repository.RatesRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class RecalculateRatesUseCaseTest {

    private val repo: RatesRepository = Mockito.mock(RatesRepository::class.java)

    private val calculateRatesHelper = CalculateRatesHelper()

    private val useCase = RecalculateRatesUseCase(repo, calculateRatesHelper)

    @Before
    fun setUp() {
        val rates = mutableListOf(
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
        val calculatedRates = mutableListOf(
            CalculatedRate(
                currency = "EUR",
                flagUrl = "https://www.countryflags.io/EU/flat/64.png",
                description = "Euro",
                amount = 100.0.toBigDecimal(),
                isEnabled = true
            ), CalculatedRate(
                currency = "HRK",
                flagUrl = "https://www.countryflags.io/HR/flat/64.png",
                description = "Kuna",
                amount = 743.0.toBigDecimal(),
                isEnabled = false
            ), CalculatedRate(
                currency = "USD",
                flagUrl = "https://www.countryflags.io/US/flat/64.png",
                description = "US Dollar",
                amount = 116.0.toBigDecimal(),
                isEnabled = false
            ), CalculatedRate(
                currency = "AUD",
                flagUrl = "https://www.countryflags.io/AU/flat/64.png",
                description = "Australian Dollar",
                amount = 161.0.toBigDecimal(),
                isEnabled = false
            )
        )
        `when`(repo.cachedLatestRates).thenReturn(rates)
        `when`(repo.cachedCalculatedRates).thenReturn(calculatedRates)
    }

    @Test
    fun `recalculate use case should return calculated amount list from the cached rates`() {
        // Given
        val amount = 100.toBigDecimal()

        // When
        val test = useCase(amount)
            .test()

        // Then
        val expected = listOf(
            CalculatedRate(
                currency = "EUR",
                flagUrl = "https://www.countryflags.io/EU/flat/64.png",
                description = "Euro",
                amount = 100.toBigDecimal(),
                isEnabled = true
            ), CalculatedRate(
                currency = "HRK",
                flagUrl = "https://www.countryflags.io/HR/flat/64.png",
                description = "Kuna",
                amount = 743.toBigDecimal().setScale(2),
                isEnabled = false
            ), CalculatedRate(
                currency = "USD",
                flagUrl = "https://www.countryflags.io/US/flat/64.png",
                description = "US Dollar",
                amount = 116.toBigDecimal().setScale(2),
                isEnabled = false
            ), CalculatedRate(
                currency = "AUD",
                flagUrl = "https://www.countryflags.io/AU/flat/64.png",
                description = "Australian Dollar",
                amount = 161.toBigDecimal().setScale(2),
                isEnabled = false
            )
        )

        test.assertValue(expected)
    }

    @Test
    fun `recalculate use case should add calculated rates to cache`() {
        // Given
        val amount = 100.toBigDecimal()

        // When
        useCase(amount)
            .test()

        // Then
        val newList = listOf(
            CalculatedRate(
                currency = "EUR",
                flagUrl = "https://www.countryflags.io/EU/flat/64.png",
                description = "Euro",
                amount = 100.0.toBigDecimal(),
                isEnabled = true
            ), CalculatedRate(
                currency = "HRK",
                flagUrl = "https://www.countryflags.io/HR/flat/64.png",
                description = "Kuna",
                amount = 743.0.toBigDecimal(),
                isEnabled = false
            ), CalculatedRate(
                currency = "USD",
                flagUrl = "https://www.countryflags.io/US/flat/64.png",
                description = "US Dollar",
                amount = 116.0.toBigDecimal(),
                isEnabled = false
            ), CalculatedRate(
                currency = "AUD",
                flagUrl = "https://www.countryflags.io/AU/flat/64.png",
                description = "Australian Dollar",
                amount = 161.0.toBigDecimal(),
                isEnabled = false
            )
        )
        assertEquals(newList, repo.cachedCalculatedRates)
    }
}

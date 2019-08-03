package com.vladimirpetrovski.currencyconverter.data.repository

import com.vladimirpetrovski.currencyconverter.data.network.MockRatesService
import com.vladimirpetrovski.currencyconverter.data.network.RatesService
import com.vladimirpetrovski.currencyconverter.domain.model.Rate
import com.vladimirpetrovski.currencyconverter.domain.repository.RatesRepository
import org.junit.Assert.assertEquals
import org.junit.Test

class RatesRepositoryImplTest {

    private val ratesService: RatesService = MockRatesService()

    private val repo: RatesRepository = RatesRepositoryImpl(ratesService)

    @Test
    fun `fetch latest rates for given currency should map rates into list from network`() {
        // Given
        val givenCurrency = "EUR"

        // When
        val test = repo.fetchLatestRates(givenCurrency)
            .test()

        // Then
        val expected = listOf(
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
        test.assertValue(expected)
    }

    @Test
    fun `fetch latest rates for given currency should save rates to cache`() {
        // Given
        val givenCurrency = "EUR"

        // When
        val test = repo.fetchLatestRates(givenCurrency)
            .test()

        // Then
        val expected = listOf(
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
        assertEquals(expected, repo.cachedLatestRates)
    }

    @Test
    fun `clear repository should clear cached calculated rates and latest rates`() {
        // Given
        val givenCurrency = "EUR"

        // When
        repo.fetchLatestRates(givenCurrency)
            .test()

        repo.clear()

        // Then
        assert(repo.cachedLatestRates.size == 0)
        assert(repo.cachedCalculatedRates.size == 0)
    }
}
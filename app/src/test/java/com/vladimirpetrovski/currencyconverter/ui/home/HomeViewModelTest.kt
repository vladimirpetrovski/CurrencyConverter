package com.vladimirpetrovski.currencyconverter.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.vladimirpetrovski.currencyconverter.domain.model.CalculatedRate
import com.vladimirpetrovski.currencyconverter.domain.usecase.*
import com.vladimirpetrovski.currencyconverter.rule.TestSchedulerRule
import io.reactivex.Flowable
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.util.concurrent.TimeUnit

class HomeViewModelTest {

    @Rule
    @JvmField
    var testSchedulerRule = TestSchedulerRule()

    @Rule
    @JvmField
    val ruleForLivaData = InstantTaskExecutorRule()

    @Mock
    lateinit var fetchRatesUseCase: FetchRatesUseCase

    @Mock
    lateinit var selectRateUseCase: SelectRateUseCase

    @Mock
    lateinit var recalculateRatesUseCase: RecalculateRatesUseCase

    @Mock
    lateinit var clearCacheUseCase: ClearCacheUseCase

    @Mock
    lateinit var listenCalculatedRatesUseCase: ListenCalculatedRatesUseCase

    lateinit var viewModel: HomeViewModel

    lateinit var givenRates: List<CalculatedRate>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        val currency = "EUR"
        val amount = 1.0.toBigDecimal()
        givenRates = listOf(
            CalculatedRate(
                currency = "EUR",
                flagUrl = "https://www.countryflags.io/EU/flat/64.png",
                description = "Euro",
                amount = 1.0.toBigDecimal(),
                isEnabled = true
            ), CalculatedRate(
                currency = "HRK",
                flagUrl = "https://www.countryflags.io/HR/flat/64.png",
                description = "Kuna",
                amount = 7.43.toBigDecimal(),
                isEnabled = false
            ), CalculatedRate(
                currency = "USD",
                flagUrl = "https://www.countryflags.io/US/flat/64.png",
                description = "US Dollar",
                amount = 1.16.toBigDecimal(),
                isEnabled = false
            ), CalculatedRate(
                currency = "AUD",
                flagUrl = "https://www.countryflags.io/AU/flat/64.png",
                description = "Australian Dollar",
                amount = 1.61.toBigDecimal(),
                isEnabled = false
            )
        )

        `when`(fetchRatesUseCase(currency, amount)).thenReturn(Single.just(givenRates))
        `when`(listenCalculatedRatesUseCase()).thenReturn(Flowable.just(givenRates))
        viewModel = HomeViewModel(
            fetchRatesUseCase,
            selectRateUseCase,
            recalculateRatesUseCase,
            clearCacheUseCase,
            listenCalculatedRatesUseCase
        )
    }

    @Test
    fun `load should start fetching rates and update list live data with calculated rates`() {
        // When
        viewModel.load()

        testSchedulerRule.testScheduler.advanceTimeTo(1, TimeUnit.SECONDS)

        // Then
        assertEquals(givenRates, viewModel.list.value)
    }

    @Test
    fun `pick currency should call calculate with new currency and update list live data with selected currency on top`() {
        // Given
        val newRates = listOf(
            CalculatedRate(
                currency = "HRK",
                flagUrl = "https://www.countryflags.io/HR/flat/64.png",
                description = "Kuna",
                amount = 7.43.toBigDecimal(),
                isEnabled = false
            ),
            CalculatedRate(
                currency = "EUR",
                flagUrl = "https://www.countryflags.io/EU/flat/64.png",
                description = "Euro",
                amount = 1.0.toBigDecimal(),
                isEnabled = true
            ), CalculatedRate(
                currency = "USD",
                flagUrl = "https://www.countryflags.io/US/flat/64.png",
                description = "US Dollar",
                amount = 1.16.toBigDecimal(),
                isEnabled = false
            ), CalculatedRate(
                currency = "AUD",
                flagUrl = "https://www.countryflags.io/AU/flat/64.png",
                description = "Australian Dollar",
                amount = 1.61.toBigDecimal(),
                isEnabled = false
            )
        )
        `when`(selectRateUseCase("HRK")).thenReturn(Single.just(newRates))
        `when`(listenCalculatedRatesUseCase()).thenReturn(Flowable.just(newRates))

        // When
        val pickedRate = CalculatedRate(
            currency = "HRK",
            amount = 7.43.toBigDecimal(),
            flagUrl = "https://www.countryflags.io/EU/flat/64.png",
            description = "Kuna"
        )
        viewModel.load()
        testSchedulerRule.testScheduler.advanceTimeTo(1, TimeUnit.SECONDS)
        viewModel.pickCurrency(pickedRate)

        // Then
        verify(selectRateUseCase).invoke("HRK")
        assertEquals(newRates, viewModel.list.value)
    }

    @Test
    fun `change amount should call recalculate with new amount and update list live data with new recalculated values`() {
        // Given
        val newRates = listOf(
            CalculatedRate(
                currency = "HRK",
                flagUrl = "https://www.countryflags.io/HR/flat/64.png",
                description = "Kuna",
                amount = 743.0.toBigDecimal(),
                isEnabled = false
            ), CalculatedRate(
                currency = "EUR",
                flagUrl = "https://www.countryflags.io/EU/flat/64.png",
                description = "Euro",
                amount = 100.0.toBigDecimal(),
                isEnabled = true
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
        `when`(recalculateRatesUseCase(100.0.toBigDecimal())).thenReturn(Single.just(newRates))
        `when`(listenCalculatedRatesUseCase()).thenReturn(Flowable.just(newRates))

        // When
        viewModel.load()
        testSchedulerRule.testScheduler.advanceTimeBy(400, TimeUnit.SECONDS)
        viewModel.changeAmount(100.0.toBigDecimal())
        testSchedulerRule.testScheduler.advanceTimeBy(100, TimeUnit.SECONDS)

        // Then
        verify(recalculateRatesUseCase).invoke(100.0.toBigDecimal())
    }

    @Test
    fun `change amount should ignore multiple change amount values in 400 milliseconds`() {
        // Given
        val newRates = listOf(
            CalculatedRate(
                currency = "HRK",
                flagUrl = "https://www.countryflags.io/HR/flat/64.png",
                description = "Kuna",
                amount = 743.0.toBigDecimal(),
                isEnabled = false
            ), CalculatedRate(
                currency = "EUR",
                flagUrl = "https://www.countryflags.io/EU/flat/64.png",
                description = "Euro",
                amount = 100.0.toBigDecimal(),
                isEnabled = true
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

        `when`(recalculateRatesUseCase(100.0.toBigDecimal())).thenReturn(Single.just(newRates))
        `when`(recalculateRatesUseCase(101.0.toBigDecimal())).thenReturn(Single.just(newRates))
        `when`(recalculateRatesUseCase(102.0.toBigDecimal())).thenReturn(Single.just(newRates))
        `when`(listenCalculatedRatesUseCase()).thenReturn(Flowable.just(newRates))

        // When
        viewModel.load()
        testSchedulerRule.testScheduler.advanceTimeBy(400, TimeUnit.MILLISECONDS)
        viewModel.changeAmount(100.0.toBigDecimal())
        testSchedulerRule.testScheduler.advanceTimeBy(300, TimeUnit.MILLISECONDS)
        viewModel.changeAmount(101.0.toBigDecimal())
        testSchedulerRule.testScheduler.advanceTimeBy(150, TimeUnit.MILLISECONDS)
        viewModel.changeAmount(102.0.toBigDecimal())
        testSchedulerRule.testScheduler.advanceTimeBy(400, TimeUnit.MILLISECONDS)

        // Then
        verify(recalculateRatesUseCase, times(1)).invoke(102.0.toBigDecimal())
    }
}

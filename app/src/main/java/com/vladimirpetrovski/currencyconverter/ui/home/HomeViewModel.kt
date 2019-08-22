package com.vladimirpetrovski.currencyconverter.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vladimirpetrovski.currencyconverter.domain.model.CalculatedRate
import com.vladimirpetrovski.currencyconverter.domain.usecase.ClearCacheUseCase
import com.vladimirpetrovski.currencyconverter.domain.usecase.FetchRatesUseCase
import com.vladimirpetrovski.currencyconverter.domain.usecase.ListenCalculatedRatesUseCase
import com.vladimirpetrovski.currencyconverter.domain.usecase.RecalculateRatesUseCase
import com.vladimirpetrovski.currencyconverter.domain.usecase.SelectRateUseCase
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Flowables
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val fetchRatesUseCase: FetchRatesUseCase,
    private val selectRateUseCase: SelectRateUseCase,
    private val recalculateRatesUseCase: RecalculateRatesUseCase,
    private val clearCacheUseCase: ClearCacheUseCase,
    private val listenCalculatedRatesUseCase: ListenCalculatedRatesUseCase
) : ViewModel() {

    private val compositeDouble = CompositeDisposable()

    val list = MutableLiveData<List<CalculatedRate>>()

    val error = MutableLiveData<String>()

    private val retry = PublishSubject.create<Unit>()

    private val currencyChangeSubject = PublishSubject.create<CalculatedRate>()

    private val amountChangeSubject = PublishSubject.create<Double>()

    private val pollingEnabledSubject = BehaviorSubject.create<Boolean>()

    private var current = CalculatedRate()

    fun load() {
        listenRatesChanges()
        listenCurrencyChanges()
        listenAmountChanges()
        startPolling()
    }

    private fun listenRatesChanges() {
        compositeDouble.add(
            listenCalculatedRatesUseCase()
                .throttleLast(IGNORE_INTERVAL, TimeUnit.MILLISECONDS)
                .subscribe {
                    list.postValue(it)
                }
        )
    }

    private fun listenCurrencyChanges() {
        compositeDouble.add(currencyChangeSubject
            .doOnNext { pauseUpdates() }
            .subscribeOn(Schedulers.computation())
            .doOnNext { newCalculatedRate -> current = newCalculatedRate }
            .flatMapSingle { selectRateUseCase(current.currency) }
            .doOnNext { resumeUpdates() }
            .subscribe())
    }

    private fun listenAmountChanges() {
        compositeDouble.add(amountChangeSubject
            .doOnNext { pauseUpdates() }
            .subscribeOn(Schedulers.computation())
            .doOnNext { newAmount -> current = current.copy(amount = newAmount) }
            .flatMapSingle { recalculateRatesUseCase(current.amount) }
            .doOnNext { resumeUpdates() }
            .subscribe()
        )
    }

    private fun startPolling() {
        compositeDouble.add(
            this.pollingEnabledSubject.distinctUntilChanged().toFlowable(BackpressureStrategy.LATEST)
                .switchMap {
                    if (it) {
                        return@switchMap Flowable.interval(0, REFRESH_INTERVAL, TimeUnit.SECONDS)
                            .subscribeOn(Schedulers.io())
                            .flatMapSingle {
                                fetchRatesUseCase(
                                    baseCurrency = current.currency,
                                    amount = current.amount
                                )
                            }
                            .doOnError { error.postValue(it.message) }
                            .retryWhen {
                                Flowables.zip(
                                    it,
                                    retry.toFlowable(BackpressureStrategy.BUFFER)
                                ) { throwable, retry -> throwable }
                            }
                            .takeWhile { pollingEnabledSubject.value == true }
                    }
                    return@switchMap Flowable.empty<Any>()
                }
                .subscribe()
        )
        pollingEnabledSubject.onNext(true)
    }

    fun pickCurrency(calculatedRate: CalculatedRate) {
        currencyChangeSubject.onNext(calculatedRate)
    }

    fun changeAmount(newAmount: Double) {
        amountChangeSubject.onNext(newAmount)
    }

    fun pauseUpdates() {
        pollingEnabledSubject.onNext(false)
    }

    fun resumeUpdates() {
        pollingEnabledSubject.onNext(true)
    }

    fun retry() {
        retry.onNext(Unit)
    }

    override fun onCleared() {
        clearCacheUseCase()
        compositeDouble.clear()
        super.onCleared()
    }

    companion object {
        private const val REFRESH_INTERVAL: Long = 1
        private const val IGNORE_INTERVAL: Long = 400
    }
}

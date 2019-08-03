package com.vladimirpetrovski.currencyconverter.di

import com.vladimirpetrovski.currencyconverter.data.repository.RatesRepositoryImpl
import com.vladimirpetrovski.currencyconverter.domain.repository.RatesRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module(
    includes = [
        NetworkModule::class]
)
abstract class AppModule {

    @Singleton
    @Binds
    abstract fun bindRatesRepository(repo: RatesRepositoryImpl): RatesRepository
}

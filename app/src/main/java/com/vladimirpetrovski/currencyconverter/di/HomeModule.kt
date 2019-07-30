package com.vladimirpetrovski.currencyconverter.di

import androidx.lifecycle.ViewModel
import com.vladimirpetrovski.currencyconverter.ui.home.HomeFragment
import com.vladimirpetrovski.currencyconverter.ui.home.HomeViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class HomeModule {

    @ContributesAndroidInjector(
        modules = [
            ViewModelBuilder::class
        ]
    )
    internal abstract fun homeFragment(): HomeFragment

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(viewmodel: HomeViewModel): ViewModel
}

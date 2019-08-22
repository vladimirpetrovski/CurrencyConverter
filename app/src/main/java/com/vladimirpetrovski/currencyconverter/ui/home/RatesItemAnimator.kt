package com.vladimirpetrovski.currencyconverter.ui.home

import androidx.recyclerview.widget.DefaultItemAnimator

class RatesItemAnimator : DefaultItemAnimator() {
    init {
        supportsChangeAnimations = false
    }
}
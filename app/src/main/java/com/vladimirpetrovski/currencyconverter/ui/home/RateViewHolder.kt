package com.vladimirpetrovski.currencyconverter.ui.home

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.squareup.picasso.Picasso
import com.vladimirpetrovski.currencyconverter.domain.model.CalculatedRate
import com.vladimirpetrovski.currencyconverter.ui.utils.CropBitmapTransformation
import com.vladimirpetrovski.currencyconverter.ui.utils.DecimalDigitsInputFilter
import com.vladimirpetrovski.currencyconverter.ui.utils.placeCursorToEnd
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.item_rate.view.*
import java.text.NumberFormat

class RateViewHolder(
    itemView: View,
    val clickListener: (Int) -> Unit,
    val amountChangeListener: (Double) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    init {
        itemView.amount.clicks()
            .doOnNext { selectCurrency() }
            .subscribe()

        itemView.rateContent.clicks()
            .doOnNext { selectCurrency() }
            .subscribe()

        itemView.amount.textChanges()
            .doOnNext { changeAmount(it) }
            .subscribe()
    }

    fun bind(item: CalculatedRate) {
        loadFlagImage(item)

        itemView.currency.text = item.currency

        itemView.description.text = item.description

        itemView.amount.setText(format.format(item.amount))
        itemView.amount.filters = arrayOf(filter)
        itemView.amount.isFocusableInTouchMode = item.isEnabled
    }

    private fun loadFlagImage(item: CalculatedRate) {
        Picasso.get()
            .load(item.flagUrl)
            .transform(listOf(CropBitmapTransformation(), CropCircleTransformation()))
            .centerCrop()
            .resize(220, 220)
            .noFade()
            .into(itemView.currencyFlag)
    }

    private fun selectCurrency() {
        clickListener(adapterPosition)
        itemView.amount.placeCursorToEnd()
        itemView.amount.requestFocus()
        itemView.amount.isFocusableInTouchMode = true
    }

    private fun changeAmount(text: CharSequence) {
        if (adapterPosition == 0) { // listen changes on first item
            if (text.isNotEmpty()) {
                try {
                    val value = NumberFormat.getInstance().parse(text.toString())
                    value?.let {
                        amountChangeListener(it.toDouble())
                    }
                } catch (e: NumberFormatException) {
                    amountChangeListener(0.0)
                }
            } else {
                amountChangeListener(0.0)
            }
        }
    }

    companion object {
        private val filter = DecimalDigitsInputFilter(10, 2)
        private val format: NumberFormat = NumberFormat.getInstance().apply {
            this.maximumFractionDigits = 2
            this.isGroupingUsed = false
        }
    }
}

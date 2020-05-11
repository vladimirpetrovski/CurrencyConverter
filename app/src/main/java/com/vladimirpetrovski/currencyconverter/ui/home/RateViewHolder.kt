package com.vladimirpetrovski.currencyconverter.ui.home

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding3.view.focusChanges
import com.jakewharton.rxbinding3.widget.textChanges
import com.squareup.picasso.Picasso
import com.vladimirpetrovski.currencyconverter.domain.model.CalculatedRate
import com.vladimirpetrovski.currencyconverter.ui.utils.CropBitmapTransformation
import com.vladimirpetrovski.currencyconverter.ui.utils.DecimalDigitsInputFilter
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.item_rate.view.*
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException

class RateViewHolder(
    itemView: View,
    val clickListener: (Int) -> Unit,
    val amountChangeListener: (BigDecimal) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    init {
        itemView.amount.focusChanges()
            .doOnNext {
                if (it) {
                    selectCurrency()
                }
            }
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
    }

    private fun changeAmount(text: CharSequence) {
        if (adapterPosition == 0) { // listen changes on first item
            if (text.isNotEmpty()) {
                try {
                    val value: BigDecimal = format.parse(text.toString()) as BigDecimal
                    amountChangeListener(value)
                } catch (e: NumberFormatException) {
                    amountChangeListener(0.0.toBigDecimal())
                } catch (e: ParseException) {
                    amountChangeListener(0.0.toBigDecimal())
                }
            } else {
                amountChangeListener(0.0.toBigDecimal())
            }
        }
    }

    companion object {
        private val filter = DecimalDigitsInputFilter(10, 2)
        private val format = NumberFormat.getInstance().apply {
            this.maximumFractionDigits = 2
            if (this is DecimalFormat) {
                this.isParseBigDecimal = true
            }
        }
    }
}

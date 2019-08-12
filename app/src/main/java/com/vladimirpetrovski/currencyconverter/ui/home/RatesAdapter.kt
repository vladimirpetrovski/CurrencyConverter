package com.vladimirpetrovski.currencyconverter.ui.home

import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.squareup.picasso.Picasso
import com.vladimirpetrovski.currencyconverter.R
import com.vladimirpetrovski.currencyconverter.domain.model.CalculatedRate
import com.vladimirpetrovski.currencyconverter.ui.utils.CropBitmapTransformation
import com.vladimirpetrovski.currencyconverter.ui.utils.DecimalDigitsInputFilter
import com.vladimirpetrovski.currencyconverter.ui.utils.placeCursorToEnd
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.item_rate.view.*
import timber.log.Timber
import java.text.NumberFormat
import javax.inject.Inject

class RatesAdapter @Inject constructor() :
    ListAdapter<CalculatedRate, RatesAdapter.RateViewHolder>(COMPARATOR) {

    private var onRateClickListener: OnRateClickListener? = null
    private var onAmountChangeListener: OnAmountChangeListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateViewHolder {
        return RateViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_rate, parent, false)
            , {
                onRateClickListener?.onClick(it)
            }, {
                onAmountChangeListener?.onChange(it)
            })
    }

    override fun onBindViewHolder(holder: RateViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    fun setOnRateClickListener(listener: (CalculatedRate?) -> Unit) {
        onRateClickListener = object : OnRateClickListener {
            override fun onClick(community: CalculatedRate?) {
                listener(community)
            }
        }
    }

    fun setOnAmountChangeListener(listener: (Double) -> Unit) {
        onAmountChangeListener = object : OnAmountChangeListener {
            override fun onChange(amount: Double) {
                listener(amount)
            }
        }
    }

    inner class RateViewHolder(
        itemView: View,
        val clickListener: (CalculatedRate?) -> Unit,
        val amountChangeListener: (Double) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: CalculatedRate) {

            loadFlagImage(item)

            itemView.currency.text = item.currency

            itemView.description.text = item.description

            val format = NumberFormat.getInstance()
            format.maximumFractionDigits = 2
            format.isGroupingUsed = false
            val number = format.format(item.amount)
            itemView.amount.setText(number)
            itemView.amount.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(10, 2))
//            itemView.amount.isEnabled = item.isEnabled

            itemView.rateContent.clicks()
                .doOnNext {
                    val selectedRate = getItem(adapterPosition)
                    clickListener(selectedRate)
                    itemView.amount.placeCursorToEnd()
                    itemView.amount.requestFocus()
                }
                .subscribe()

            itemView.amount.textChanges()
                .doOnNext { text ->
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
                .subscribe()
        }
    }

    private fun RateViewHolder.loadFlagImage(item: CalculatedRate) {
        Picasso.get()
            .load(item.flagUrl)
            .transform(listOf(CropBitmapTransformation(), CropCircleTransformation()))
            .centerCrop()
            .resize(220, 220)
            .into(itemView.currencyFlag)
    }

    interface OnRateClickListener {

        fun onClick(community: CalculatedRate?)
    }

    interface OnAmountChangeListener {

        fun onChange(amount: Double)
    }

    companion object {

        val COMPARATOR = object : DiffUtil.ItemCallback<CalculatedRate>() {

            override fun areItemsTheSame(
                oldItem: CalculatedRate,
                newItem: CalculatedRate
            ): Boolean {
                return oldItem.currency == newItem.currency
            }

            override fun areContentsTheSame(
                oldItem: CalculatedRate,
                newItem: CalculatedRate
            ): Boolean {
                if (newItem.isEnabled) {
                    return true
                }
                return oldItem.amount == newItem.amount
            }
        }
    }
}

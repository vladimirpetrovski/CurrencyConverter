package com.vladimirpetrovski.currencyconverter.ui.home

import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.vladimirpetrovski.currencyconverter.R
import com.vladimirpetrovski.currencyconverter.domain.model.CalculatedRate
import com.vladimirpetrovski.currencyconverter.ui.utils.DecimalDigitsInputFilter
import com.vladimirpetrovski.currencyconverter.ui.utils.placeCursorToEnd
import kotlinx.android.synthetic.main.item_rate.view.*
import java.text.NumberFormat
import java.util.ArrayList
import javax.inject.Inject

class RatesAdapter @Inject constructor() : RecyclerView.Adapter<RatesAdapter.RateViewHolder>() {

    private val list = ArrayList<CalculatedRate>()

    private var onRateClickListener: OnRateClickListener? = null
    private var onAmountChangeListener: OnAmountChangeListener? = null

    fun submitList(newList: List<CalculatedRate>) {
        val diffResult = DiffUtil.calculateDiff(CalculatedRateDiffCallback(newList, list))
        this.list.clear()
        this.list.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

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

    override fun getItemCount(): Int {
        return list.size
    }

    fun getItem(position: Int): CalculatedRate {
        return list[position]
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
//            itemView.amount.isFocusable = item.isEnabled

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
        val drawable = CircularProgressDrawable(itemView.context)
        drawable.strokeWidth = 5f
        drawable.centerRadius = 30f
        drawable.start()
        Glide.with(itemView.context)
            .load(item.flagUrl)
            .placeholder(drawable)
            .transform(
                MultiTransformation(
                    CircleCrop(), CenterInside()
                )
            )
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(itemView.currencyFlag)
    }

    interface OnRateClickListener {

        fun onClick(community: CalculatedRate?)
    }

    interface OnAmountChangeListener {

        fun onChange(amount: Double)
    }

    class CalculatedRateDiffCallback(
        private val newRows: List<CalculatedRate>,
        private val oldRows: List<CalculatedRate>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldRows.size

        override fun getNewListSize() = newRows.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldRow = oldRows[oldItemPosition]
            val newRow = newRows[newItemPosition]
            return oldRow.currency == newRow.currency
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            if (newItemPosition == 0) {
                return true // prevent updates on first item in order to preserve edit text changes
            }
            val oldRow = oldRows[oldItemPosition]
            val newRow = newRows[newItemPosition]
            return oldRow == newRow
        }
    }
}

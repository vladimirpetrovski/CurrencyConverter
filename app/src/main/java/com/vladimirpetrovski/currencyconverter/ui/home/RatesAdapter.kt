package com.vladimirpetrovski.currencyconverter.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vladimirpetrovski.currencyconverter.R
import com.vladimirpetrovski.currencyconverter.domain.model.CalculatedRate
import java.math.BigDecimal
import javax.inject.Inject

class RatesAdapter @Inject constructor() :
    RecyclerView.Adapter<RateViewHolder>() {

    private var list = emptyList<CalculatedRate>()

    private var onRateClickListener: OnRateClickListener? = null
    private var onAmountChangeListener: OnAmountChangeListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateViewHolder {
        return RateViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_rate, parent, false)
            , {
                onRateClickListener?.onClick(getItem(it))
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

    fun submitList(newList: List<CalculatedRate>) {
        val diffResult = DiffUtil.calculateDiff(RatesDiffUtil(newList, list))
        this.list = newList
        diffResult.dispatchUpdatesTo(this)
    }

    fun setOnRateClickListener(listener: (CalculatedRate) -> Unit) {
        onRateClickListener = object : OnRateClickListener {
            override fun onClick(community: CalculatedRate) {
                listener(community)
            }
        }
    }

    fun setOnAmountChangeListener(listener: (BigDecimal) -> Unit) {
        onAmountChangeListener = object : OnAmountChangeListener {
            override fun onChange(amount: BigDecimal) {
                listener(amount)
            }
        }
    }

    interface OnRateClickListener {

        fun onClick(community: CalculatedRate)
    }

    interface OnAmountChangeListener {

        fun onChange(amount: BigDecimal)
    }

    class RatesDiffUtil(
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
            val oldRow = oldRows[oldItemPosition]
            val newRow = newRows[newItemPosition]
            if (newRow.isEnabled) {
                return true
            }
            return oldRow.amount == newRow.amount
        }
    }
}

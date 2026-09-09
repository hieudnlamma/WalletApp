package com.lmt.global.base.presenter.main.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lmt.global.base.R
import com.lmt.global.base.databinding.ItemTransactionBinding
import com.lmt.global.base.extension.onDebounceClick
import com.lmt.global.base.model.Transaction
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class HistoryTransactionsAdapter(
    private val onTransactionClick: (Transaction) -> Unit,
) : ListAdapter<Transaction, HistoryTransactionsAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTransactionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onTransactionClick = onTransactionClick,
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position == itemCount - 1)
    }

    class ViewHolder(
        private val binding: ItemTransactionBinding,
        private val onTransactionClick: (Transaction) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: Transaction,
            isLastItem: Boolean,
        ) = with(binding) {
            this.isLastItem = isLastItem
            tvNameApp.text = item.merchantName
            tvDatetime.text = item.dateTime
            ivAvatarApp.setImageResource(item.merchantIconRes)
            bindAmount(item)
            layoutItemTransaction.onDebounceClick {
                onTransactionClick(item)
            }
            executePendingBindings()
        }

        private fun ItemTransactionBinding.bindAmount(item: Transaction) {
            val currency = runCatching { Currency.getInstance(item.currencyCode) }
                .getOrDefault(Currency.getInstance(DEFAULT_CURRENCY_CODE))
            val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
                this.currency = currency
                minimumFractionDigits = currency.defaultFractionDigits.coerceAtLeast(0)
                maximumFractionDigits = minimumFractionDigits
            }

            tvMoney.text = buildString {
                if (item.amount.signum() > 0) append('+')
                append(formatter.format(item.amount))
            }
            val amountColor = when {
                item.amount.signum() < 0 -> R.color.hues_red_golden_gate_bridge
                item.amount.signum() > 0 -> R.color.hues_green_sea_green
                else -> R.color.neutrals_black_coral
            }
            tvMoney.setTextColor(ContextCompat.getColor(tvMoney.context, amountColor))
        }
    }

    private companion object {
        const val DEFAULT_CURRENCY_CODE = "USD"

        val DiffCallback = object : DiffUtil.ItemCallback<Transaction>() {
            override fun areItemsTheSame(
                oldItem: Transaction,
                newItem: Transaction,
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Transaction,
                newItem: Transaction,
            ): Boolean = oldItem == newItem
        }
    }
}

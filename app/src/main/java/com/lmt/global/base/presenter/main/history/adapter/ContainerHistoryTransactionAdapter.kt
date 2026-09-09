package com.lmt.global.base.presenter.main.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lmt.global.base.R
import com.lmt.global.base.databinding.ItemContainerHistoryBinding
import com.lmt.global.base.model.Transaction

data class TransactionHistoryGroup(
    val id: String,
    val weekdayLabel: String?,
    val dateLabel: String,
    val transactions: List<Transaction>,
)

class ContainerHistoryTransactionAdapter(
    private val onTransactionClick: (Transaction) -> Unit = {},
) : ListAdapter<TransactionHistoryGroup, ContainerHistoryTransactionAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemContainerHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return ViewHolder(binding, onTransactionClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemContainerHistoryBinding,
        onTransactionClick: (Transaction) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        private val transactionsAdapter = HistoryTransactionsAdapter(onTransactionClick)
        private val transactionsRecyclerView =
            binding.root.findViewById<RecyclerView>(R.id.rcvListTransactions)

        init {
            transactionsRecyclerView.apply {
                adapter = transactionsAdapter
                isNestedScrollingEnabled = false
            }
        }

        fun bind(group: TransactionHistoryGroup) = with(binding) {
            tvWeekday.text = group.weekdayLabel
            tvWeekday.isVisible = !group.weekdayLabel.isNullOrBlank()
            tvDatetime.text = group.dateLabel
            transactionsAdapter.submitList(group.transactions)
        }
    }

    private companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<TransactionHistoryGroup>() {
            override fun areItemsTheSame(
                oldItem: TransactionHistoryGroup,
                newItem: TransactionHistoryGroup,
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: TransactionHistoryGroup,
                newItem: TransactionHistoryGroup,
            ): Boolean = oldItem == newItem
        }
    }
}

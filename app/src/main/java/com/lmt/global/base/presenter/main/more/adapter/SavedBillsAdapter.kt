package com.lmt.global.base.presenter.main.more.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lmt.global.base.databinding.ItemSavedBillersBinding
import com.lmt.global.base.model.Transaction

class SavedBillsAdapter(
    private val onSavedBillClick: (Transaction) -> Unit = {},
) : ListAdapter<Transaction, SavedBillsAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSavedBillersBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            item = getItem(position),
            isLastItem = position == itemCount - 1,
            onClick = onSavedBillClick,
        )
    }

    class ViewHolder(
        private val binding: ItemSavedBillersBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: Transaction,
            isLastItem: Boolean,
            onClick: (Transaction) -> Unit,
        ) = with(binding) {
            this.isLastItem = isLastItem
            tvNameBill.text = item.merchantName
            tvValueOfBill.text = item.dueText ?: item.dateTime
            ivAvatarBiller.setImageResource(item.merchantIconRes)
            layoutItemSavedBillers.setOnClickListener { onClick(item) }
            executePendingBindings()
        }
    }

    private companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Transaction>() {
            override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem == newItem
            }
        }
    }
}

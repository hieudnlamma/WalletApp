package com.lmt.global.base.presenter.main.more.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lmt.global.base.databinding.ItemSavedBillersBinding
import java.math.BigDecimal

data class SavedBill(
    val id: Long,
    val name: String,
    val dueText: String,
    val amount: BigDecimal,
    val currencyCode: String,
    val category: String,
    val dueDate: String,
    val registrationNumber: String,
    @DrawableRes val avatarRes: Int,
)

class SavedBillsAdapter(
    private val onSavedBillClick: (SavedBill) -> Unit = {},
) : ListAdapter<SavedBill, SavedBillsAdapter.ViewHolder>(DiffCallback) {

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
            item: SavedBill,
            isLastItem: Boolean,
            onClick: (SavedBill) -> Unit,
        ) = with(binding) {
            this.isLastItem = isLastItem
            tvNameBill.text = item.name
            tvValueOfBill.text = item.dueText
            ivAvatarBiller.setImageResource(item.avatarRes)
            layoutItemSavedBillers.setOnClickListener { onClick(item) }
            executePendingBindings()
        }
    }

    private companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<SavedBill>() {
            override fun areItemsTheSame(oldItem: SavedBill, newItem: SavedBill): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: SavedBill, newItem: SavedBill): Boolean {
                return oldItem == newItem
            }
        }
    }
}

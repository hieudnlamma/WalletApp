package com.lmt.global.base.presenter.main.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lmt.global.base.databinding.ItemAddListResentTransfersBinding
import com.lmt.global.base.databinding.ItemRecentTransfersBinding

data class RecentTransfer(
    val id: Long,
    val name: String,
    @DrawableRes val avatarRes: Int,
)

class RecentTransfersAdapter(
    private val onAddClick: () -> Unit = {},
    private val onTransferClick: (RecentTransfer) -> Unit = {},
) : ListAdapter<RecentTransferRow, RecyclerView.ViewHolder>(RecentTransferDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_ADD -> AddViewHolder(
                ItemAddListResentTransfersBinding.inflate(inflater, parent, false),
            )

            VIEW_TYPE_TRANSFER -> TransferViewHolder(
                ItemRecentTransfersBinding.inflate(inflater, parent, false),
            )

            else -> error("Unsupported recent transfer view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddViewHolder -> holder.bind(onAddClick)
            is TransferViewHolder -> holder.bind(
                item = (getItem(position) as RecentTransferRow.Transfer).value,
                onClick = onTransferClick,
            )
        }
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        RecentTransferRow.Add -> VIEW_TYPE_ADD
        is RecentTransferRow.Transfer -> VIEW_TYPE_TRANSFER
    }

    fun submitTransfers(transfers: List<RecentTransfer>) {
        submitList(listOf(RecentTransferRow.Add) + transfers.map(RecentTransferRow::Transfer))
    }

    private class AddViewHolder(
        private val binding: ItemAddListResentTransfersBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(onClick: () -> Unit) {
            binding.root.setOnClickListener { onClick() }
        }
    }

    private class TransferViewHolder(
        private val binding: ItemRecentTransfersBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecentTransfer, onClick: (RecentTransfer) -> Unit) = with(binding) {
            tvNameUser.text = item.name
            ivAvatar.setImageResource(item.avatarRes)
            root.setOnClickListener { onClick(item) }
        }
    }

    private companion object {
        const val VIEW_TYPE_ADD = 0
        const val VIEW_TYPE_TRANSFER = 1

        val RecentTransferDiffCallback = object : DiffUtil.ItemCallback<RecentTransferRow>() {
            override fun areItemsTheSame(
                oldItem: RecentTransferRow,
                newItem: RecentTransferRow,
            ): Boolean = when {
                oldItem === RecentTransferRow.Add && newItem === RecentTransferRow.Add -> true
                oldItem is RecentTransferRow.Transfer && newItem is RecentTransferRow.Transfer ->
                    oldItem.value.id == newItem.value.id

                else -> false
            }

            override fun areContentsTheSame(
                oldItem: RecentTransferRow,
                newItem: RecentTransferRow,
            ): Boolean = oldItem == newItem
        }
    }
}

sealed interface RecentTransferRow {
    data object Add : RecentTransferRow
    data class Transfer(val value: RecentTransfer) : RecentTransferRow
}

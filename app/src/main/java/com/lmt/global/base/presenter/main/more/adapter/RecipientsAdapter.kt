package com.lmt.global.base.presenter.main.more.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lmt.global.base.databinding.ItemRecipientBinding
import com.lmt.global.base.extension.onDebounceClick
import com.lmt.global.base.model.Recipient

class RecipientsAdapter(
    private val onRecipientClick: (Recipient) -> Unit = {},
) : ListAdapter<Recipient, RecipientsAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecipientBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            recipient = getItem(position),
            isLastItem = position == itemCount - 1,
            onClick = onRecipientClick,
        )
    }

    class ViewHolder(
        private val binding: ItemRecipientBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            recipient: Recipient,
            isLastItem: Boolean,
            onClick: (Recipient) -> Unit,
        ) = with(binding) {
            this.isLastItem = isLastItem
            tvUserName.text = recipient.name
            tvPhoneNumber.text = recipient.phoneNumber
            ivAvatarUser.setImageResource(recipient.avatarRes)
            layoutItemRecipient.onDebounceClick { onClick(recipient) }
            executePendingBindings()
        }
    }

    private companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Recipient>() {
            override fun areItemsTheSame(oldItem: Recipient, newItem: Recipient): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Recipient, newItem: Recipient): Boolean {
                return oldItem == newItem
            }
        }
    }
}

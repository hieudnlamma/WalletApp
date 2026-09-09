package com.lmt.global.base.presenter.main.more.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lmt.global.base.databinding.ItemContractBinding
import com.lmt.global.base.extension.onDebounceClick
import com.lmt.global.base.model.Contact

class ContactsAdapter(
    private val onContactClick: (Contact) -> Unit = {},
) : ListAdapter<Contact, ContactsAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemContractBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            contact = getItem(position),
            isLastItem = position == itemCount - 1,
            onClick = onContactClick,
        )
    }

    class ViewHolder(
        private val binding: ItemContractBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            contact: Contact,
            isLastItem: Boolean,
            onClick: (Contact) -> Unit,
        ) = with(binding) {
            this.isLastItem = isLastItem
            tvUserName.text = contact.name
            tvPhoneNumber.text = contact.phoneNumber
            ivAvatarUser.setImageResource(contact.avatarRes)
            layoutItemContract.onDebounceClick { onClick(contact) }
            executePendingBindings()
        }
    }

    private companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Contact>() {
            override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
                return oldItem == newItem
            }
        }
    }
}

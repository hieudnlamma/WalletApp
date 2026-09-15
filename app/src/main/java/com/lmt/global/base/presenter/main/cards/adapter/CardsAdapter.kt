package com.lmt.global.base.presenter.main.cards.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lmt.global.base.databinding.ItemCardBinding
import com.lmt.global.base.extension.onDebounceClick
import com.lmt.global.base.model.Card

class CardsAdapter(
    private val onCardClick: (Card) -> Unit = {},
) : ListAdapter<Card, CardsAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onCardClick)
    }

    class ViewHolder(
        private val binding: ItemCardBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(card: Card, onClick: (Card) -> Unit) = with(binding) {
            cardHolder = card.cardHolder
            maskedNumber = card.maskedNumber
            balance = card.balance
            cardColor = ContextCompat.getColor(root.context, card.cardColorRes)
            lightCard = card.lightCard
            compactHeader = card.compactHeader
            onClicked = onDebounceClick { onClick(card) }
            executePendingBindings()
        }
    }

    private companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Card>() {
            override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean {
                return oldItem == newItem
            }
        }
    }
}

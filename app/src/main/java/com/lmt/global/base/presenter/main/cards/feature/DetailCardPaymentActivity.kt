package com.lmt.global.base.presenter.main.cards.feature

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.lmt.global.base.R
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivityDetailCardPaymentBinding
import com.lmt.global.base.extension.applyStatusBarPadding
import com.lmt.global.base.extension.parcelable
import com.lmt.global.base.model.Card
import com.lmt.global.base.presenter.main.cards.CardsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailCardPaymentActivity : IActivity<ActivityDetailCardPaymentBinding, CardsViewModel>() {
    override fun provideViewModel() = viewModel<CardsViewModel>()
    override fun provideLayout() = R.layout.activity_detail_card_payment

    override fun initViews(savedInstanceState: Bundle?) {
        viewBinding.toolbar.applyStatusBarPadding()
        val card = intent.parcelable<Card>(EXTRA_CARD) ?: run {
            finish()
            return
        }
        renderCard(card)
    }

    override fun initListeners() {
        viewBinding.tvBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun renderCard(card: Card) = with(viewBinding.itemCard) {
        cardHolder = card.cardHolder
        maskedNumber = card.maskedNumber
        balance = card.balance
        cardColor = ContextCompat.getColor(this@DetailCardPaymentActivity, card.cardColorRes)
        lightCard = card.lightCard
        compactHeader = card.compactHeader
        executePendingBindings()
    }

    companion object {
        private const val EXTRA_CARD = "extra_card"

        fun createIntent(context: Context, card: Card): Intent =
            Intent(context, DetailCardPaymentActivity::class.java)
                .putExtra(EXTRA_CARD, card)
    }
}

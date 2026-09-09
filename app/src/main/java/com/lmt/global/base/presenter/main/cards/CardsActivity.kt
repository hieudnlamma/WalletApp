package com.lmt.global.base.presenter.main.cards

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.lmt.global.base.R
import com.lmt.global.base.common.IFragment
import com.lmt.global.base.databinding.ActivityCardsBinding
import com.lmt.global.base.extension.applyStatusBarPadding
import com.lmt.global.base.model.PaymentCard
import com.lmt.global.base.presenter.main.cards.adapter.CardsAdapter
import com.lmt.global.base.presenter.main.cards.adapter.OverlapCardDecoration
import com.lmt.global.base.presenter.main.cards.feature.DetailCardPaymentActivity
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class CardsActivity : IFragment<ActivityCardsBinding, CardsViewModel>() {
    private val cardsAdapter by lazy {
        CardsAdapter {
            startActivity(Intent(requireContext(), DetailCardPaymentActivity::class.java))
        }
    }

    override fun provideViewModel() = activityViewModel<CardsViewModel>()
    override fun provideLayout() = R.layout.activity_cards

    override fun initViews() {
        viewBinding.toolbar.applyStatusBarPadding()

        viewBinding.layoutCards.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cardsAdapter
            addItemDecoration(
                OverlapCardDecoration(
                    cardTopStep = resources.getDimensionPixelSize(R.dimen._44dp),
                )
            )
        }
        cardsAdapter.submitList(createCards())
    }

    private fun createCards(): List<PaymentCard> = listOf(
        PaymentCard(
            id = 1L,
            cardHolder = getString(R.string.abdullah_ghatasheh),
            maskedNumber = getString(R.string.card_number_back),
            balance = getString(R.string.card_balance_sample),
            cardColorRes = R.color.hues_purple_lavender,
            lightCard = true,
            compactHeader = true,
        ),
        PaymentCard(
            id = 2L,
            cardHolder = getString(R.string.abdullah_ghatasheh),
            maskedNumber = getString(R.string.card_number_middle),
            balance = getString(R.string.card_balance_sample),
            cardColorRes = R.color.hues_purple_majorelle_blue,
            lightCard = false,
            compactHeader = true,
        ),
        PaymentCard(
            id = 3L,
            cardHolder = getString(R.string.abdullah_ghatasheh),
            maskedNumber = getString(R.string.card_number_front),
            balance = getString(R.string.card_balance_sample),
            cardColorRes = R.color.hues_purple_indigo,
            lightCard = false,
            compactHeader = false,
        ),
    )

    override fun onDestroyView() {
        viewBinding.layoutCards.adapter = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = CardsActivity()
    }
}

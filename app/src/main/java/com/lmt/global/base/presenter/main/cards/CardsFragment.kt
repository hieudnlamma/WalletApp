package com.lmt.global.base.presenter.main.cards

import androidx.recyclerview.widget.LinearLayoutManager
import com.lmt.global.base.R
import com.lmt.global.base.common.IFragment
import com.lmt.global.base.databinding.FragmentCardsBinding
import com.lmt.global.base.extension.applyStatusBarMargin
import com.lmt.global.base.model.Card
import com.lmt.global.base.presenter.main.cards.adapter.CardsAdapter
import com.lmt.global.base.presenter.main.cards.adapter.OverlapCardDecoration
import com.lmt.global.base.presenter.main.cards.feature.DetailCardPaymentActivity
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class CardsFragment : IFragment<FragmentCardsBinding, CardsViewModel>() {
    private val cardsAdapter by lazy { CardsAdapter(::openCardDetails) }

    override fun provideViewModel() = activityViewModel<CardsViewModel>()
    override fun provideLayout() = R.layout.fragment_cards

    override fun initViews() {
        viewBinding.toolbar.applyStatusBarMargin()

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

    private fun createCards(): List<Card> = listOf(
        Card(
            id = "1",
            name = getString(R.string.abdullah_ghatasheh),
            cardNumber = "0000000000002312",
            balanceMinor = 235_400L,
            createdAt = 0L,
            cardColorRes = R.color.hues_purple_lavender,
            lightCard = true,
            compactHeader = true,
        ),
        Card(
            id = "2",
            name = getString(R.string.abdullah_ghatasheh),
            cardNumber = "0000000000005432",
            balanceMinor = 235_400L,
            createdAt = 0L,
            cardColorRes = R.color.hues_purple_majorelle_blue,
            lightCard = false,
            compactHeader = true,
        ),
        Card(
            id = "3",
            name = getString(R.string.abdullah_ghatasheh),
            cardNumber = "0000000000003245",
            balanceMinor = 235_400L,
            createdAt = 0L,
            cardColorRes = R.color.hues_purple_indigo,
            lightCard = false,
            compactHeader = false,
        ),
    )

    private fun openCardDetails(card: Card) {
        startActivity(DetailCardPaymentActivity.createIntent(requireContext(), card))
    }

    override fun onDestroyView() {
        viewBinding.layoutCards.adapter = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = CardsFragment()
    }
}

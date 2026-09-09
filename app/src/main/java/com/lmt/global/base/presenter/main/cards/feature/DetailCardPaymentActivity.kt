package com.lmt.global.base.presenter.main.cards.feature

import android.os.Bundle
import com.lmt.global.base.R
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivityDetailCardPaymentBinding
import com.lmt.global.base.extension.applyStatusBarPadding
import com.lmt.global.base.presenter.main.cards.CardsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailCardPaymentActivity : IActivity<ActivityDetailCardPaymentBinding, CardsViewModel>() {
    override fun provideViewModel() = viewModel<CardsViewModel>()
    override fun provideLayout() = R.layout.activity_detail_card_payment

    override fun initViews(savedInstanceState: Bundle?) {
        viewBinding.toolbar.applyStatusBarPadding()
    }

    override fun initListeners() {
        viewBinding.tvBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}

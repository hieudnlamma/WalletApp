package com.lmt.global.base.presenter.payment

import android.content.Intent
import android.os.Bundle
import com.lmt.global.base.R
import com.lmt.global.base.common.CommonViewModel
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivityPaymentOrTransferFailureBinding
import com.lmt.global.base.extension.onDebounceClick
import com.lmt.global.base.presenter.main.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class PaymentOrTransferFailureActivity :
    IActivity<ActivityPaymentOrTransferFailureBinding, CommonViewModel>() {

    override fun provideViewModel() = viewModel<CommonViewModel>()
    override fun provideLayout() = R.layout.activity_payment_or_transfer_failure

    override fun initViews(savedInstanceState: Bundle?) {
        if (intent.getBooleanExtra(EXTRA_INSUFFICIENT_BALANCE, false)) {
            viewBinding.tvFailDescription.setText(R.string.insufficient_balance)
        }
    }

    override fun initListeners() {
        viewBinding.btnBackToWallet.onDebounceClick {
            startActivity(Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            })
            finish()
        }
    }

    companion object {
        const val EXTRA_INSUFFICIENT_BALANCE = "extra_insufficient_balance"
    }
}

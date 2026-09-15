package com.lmt.global.base.presenter.payment

import android.content.Intent
import android.os.Bundle
import com.lmt.global.base.R
import com.lmt.global.base.common.CommonViewModel
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivityPaymentOrTransferSuccessBinding
import com.lmt.global.base.extension.onDebounceClick
import com.lmt.global.base.model.Transaction
import com.lmt.global.base.presenter.main.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class PaymentOrTransferSuccessActivity :
    IActivity<ActivityPaymentOrTransferSuccessBinding, CommonViewModel>() {

    override fun provideViewModel() = viewModel<CommonViewModel>()
    override fun provideLayout() = R.layout.activity_payment_or_transfer_success

    override fun initViews(savedInstanceState: Bundle?) = with(viewBinding) {
        val type = intent.getStringExtra(EXTRA_TYPE)
        val amountMinor = intent.getLongExtra(EXTRA_AMOUNT_MINOR, 0L)
        if (type == Transaction.TYPE_TRANSFER) {
            tvPaymentDone.setText(R.string.transfer_done)
            tvTimeJoined.setText(R.string.transfer_has_been_done_successfully)
        }
        tvBiller.text = if (type == Transaction.TYPE_PAY_BILL) {
            getString(R.string.biller)
        } else {
            getString(R.string.recipient)
        }
        tvValueBiller.text = intent.getStringExtra(EXTRA_TITLE).orEmpty()
        tvAmount.text = getString(R.string.amount)
        tvValueAmount.text = formatAmount(amountMinor)
        tvTransactionNumber.text = intent.getLongExtra(EXTRA_TRANSACTION_ID, 0L)
            .toString()
            .padStart(14, '0')
    }

    override fun initListeners() {
        viewBinding.btnBackToWallet.onDebounceClick { backToWallet() }
    }

    private fun backToWallet() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        })
        finish()
    }

    private fun formatAmount(amountMinor: Long): String =
        NumberFormat.getCurrencyInstance(Locale.US).apply {
            currency = Currency.getInstance("USD")
        }.format(BigDecimal.valueOf(amountMinor, 2))

    companion object {
        const val EXTRA_TRANSACTION_ID = "extra_transaction_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_AMOUNT_MINOR = "extra_amount_minor"
        const val EXTRA_TYPE = "extra_type"
    }
}
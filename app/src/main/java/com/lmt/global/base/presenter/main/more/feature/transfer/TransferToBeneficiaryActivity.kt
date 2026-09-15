package com.lmt.global.base.presenter.main.more.feature.transfer

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.lmt.global.base.R
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivityTransferToBeneficiaryBinding
import com.lmt.global.base.extension.collectLatestRepeatOnLifecycle
import com.lmt.global.base.extension.onDebounceClick
import com.lmt.global.base.presenter.payment.PaymentAction
import com.lmt.global.base.presenter.payment.PaymentEffect
import com.lmt.global.base.presenter.payment.PaymentOrTransferFailureActivity
import com.lmt.global.base.presenter.payment.PaymentOrTransferSuccessActivity
import com.lmt.global.base.presenter.payment.PaymentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransferToBeneficiaryActivity :
    IActivity<ActivityTransferToBeneficiaryBinding, PaymentViewModel>() {

    private var amountConfirmed = false

    override fun provideViewModel() = viewModel<PaymentViewModel>()

    override fun provideLayout() = R.layout.activity_transfer_to_beneficiary

    override fun initViews(savedInstanceState: Bundle?) {
        val toolbarPaddingTop = viewBinding.toolbar.paddingTop
        val rootPaddingBottom = viewBinding.root.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.root) { _, insets ->
            viewBinding.toolbar.updatePadding(
                top = toolbarPaddingTop + insets.getInsets(WindowInsetsCompat.Type.statusBars()).top,
            )
            viewBinding.root.updatePadding(
                bottom = rootPaddingBottom + insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom,
            )
            insets
        }
        ViewCompat.requestApplyInsets(viewBinding.root)
        viewBinding.numberKeyboard.setDecimalEnabled(true)
        viewBinding.tvUserName.text = intent.getStringExtra(EXTRA_RECIPIENT_NAME)
            ?: getString(R.string.ali_ahmed)
        viewBinding.tvPhoneNumber.text = intent.getStringExtra(EXTRA_RECIPIENT_PHONE)
            ?: getString(R.string._1_300_555_0161)
        amountConfirmed = savedInstanceState?.getBoolean(STATE_CONFIRMED) ?: false
        updateAmountState()
        onBackPressedDispatcher.addCallback(this) {
            if (!viewModel.isProcessing.value) finish()
        }
    }

    override fun initObservers() {
        super.initObservers()
        collectLatestRepeatOnLifecycle(viewModel.isProcessing) { isProcessing ->
            viewBinding.tvBack.isEnabled = !isProcessing
            viewBinding.edtAmount.isEnabled = !isProcessing
            viewBinding.btnSecurePayment.isEnabled = !isProcessing
            viewBinding.progressSecurePayment.isVisible = isProcessing
            viewBinding.btnSecurePayment.text = if (isProcessing) {
                ""
            } else {
                getString(R.string.secure_payment)
            }
            viewBinding.btnSecurePayment.icon = if (isProcessing) {
                null
            } else {
                ContextCompat.getDrawable(
                    this@TransferToBeneficiaryActivity,
                    R.drawable.icn_secure_payment,
                )
            }
        }
        collectLatestRepeatOnLifecycle(viewModel.effects) { effect ->
            handlePaymentEffect(effect)
        }
    }

    override fun initListeners() {
        viewBinding.tvBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        viewBinding.numberKeyboard.setOnNumberClickListener { digit ->
            amountConfirmed = false
            viewBinding.edtAmount.appendDigit(digit)
            updateAmountState()
        }
        viewBinding.numberKeyboard.setOnDeleteClickListener {
            amountConfirmed = false
            viewBinding.edtAmount.deleteLastDigit()
            updateAmountState()
        }
        viewBinding.btnDone.setOnClickListener {
            if (viewBinding.edtAmount.getAmount().signum() <= 0) {
                Toast.makeText(this, R.string.enter_positive_amount, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            amountConfirmed = true
            updateAmountState()
        }
        viewBinding.edtAmount.setOnClickListener {
            amountConfirmed = false
            updateAmountState()
        }
        viewBinding.btnSecurePayment.onDebounceClick {
            viewModel.onState(
                PaymentAction.Transfer(
                    recipientName = viewBinding.tvUserName.text.toString(),
                    recipientPhoneNumber = viewBinding.tvPhoneNumber.text.toString(),
                    amount = viewBinding.edtAmount.getAmount(),
                )
            )
        }
    }

    private fun handlePaymentEffect(effect: PaymentEffect) {
        when (effect) {
            is PaymentEffect.Succeeded -> {
                startActivity(Intent(this, PaymentOrTransferSuccessActivity::class.java).apply {
                    putExtra(PaymentOrTransferSuccessActivity.EXTRA_TRANSACTION_ID, effect.transactionId)
                    putExtra(PaymentOrTransferSuccessActivity.EXTRA_TITLE, effect.title)
                    putExtra(PaymentOrTransferSuccessActivity.EXTRA_AMOUNT_MINOR, effect.amountMinor)
                    putExtra(PaymentOrTransferSuccessActivity.EXTRA_TYPE, effect.type)
                })
                finish()
            }

            PaymentEffect.InsufficientBalance -> openFailure(insufficientBalance = true)
            PaymentEffect.Failed -> openFailure(insufficientBalance = false)
        }
    }

    private fun openFailure(insufficientBalance: Boolean) {
        startActivity(Intent(this, PaymentOrTransferFailureActivity::class.java).apply {
            putExtra(
                PaymentOrTransferFailureActivity.EXTRA_INSUFFICIENT_BALANCE,
                insufficientBalance,
            )
        })
        finish()
    }

    private fun updateAmountState() {
        viewBinding.layoutKeyboardPanel.isVisible = !amountConfirmed
        viewBinding.btnSecurePayment.isVisible = amountConfirmed
        viewBinding.viewOtpUnderline.setBackgroundColor(
            ContextCompat.getColor(
                this,
                if (amountConfirmed) R.color.neutrals_black_coral
                else R.color.hues_purple_ocean_blue,
            ),
        )
        // Keep confirmed amounts visible while hiding their editing caret.
        viewBinding.edtAmount.isFocusable = !amountConfirmed
        viewBinding.edtAmount.isFocusableInTouchMode = !amountConfirmed
        updateContentTopMargins()
        if (amountConfirmed) viewBinding.edtAmount.clearFocus()
        else viewBinding.edtAmount.requestFocus()
    }

    private fun updateContentTopMargins() {
        val marginRes = if (amountConfirmed) R.dimen._56dp else R.dimen._24dp
        val topMargin = resources.getDimensionPixelSize(marginRes)

        viewBinding.tvTransferTo.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            this.topMargin = topMargin
        }
        viewBinding.tvEnterAmount.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            this.topMargin = topMargin
        }
        viewBinding.llUser.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            this.topMargin = topMargin
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(STATE_CONFIRMED, amountConfirmed)
        super.onSaveInstanceState(outState)
    }

    companion object {
        private const val STATE_CONFIRMED = "amount_confirmed"
        const val EXTRA_RECIPIENT_NAME = "extra_recipient_name"
        const val EXTRA_RECIPIENT_PHONE = "extra_recipient_phone"
    }
}
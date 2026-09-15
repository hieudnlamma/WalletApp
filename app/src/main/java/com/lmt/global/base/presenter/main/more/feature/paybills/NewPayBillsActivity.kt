package com.lmt.global.base.presenter.main.more.feature.paybills

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
import com.lmt.global.base.databinding.ActivityNewPayBillsBinding
import com.lmt.global.base.extension.collectLatestRepeatOnLifecycle
import com.lmt.global.base.extension.onDebounceClick
import com.lmt.global.base.model.Transaction
import com.lmt.global.base.presenter.payment.PaymentAction
import com.lmt.global.base.presenter.payment.PaymentEffect
import com.lmt.global.base.presenter.payment.PaymentOrTransferFailureActivity
import com.lmt.global.base.presenter.payment.PaymentOrTransferSuccessActivity
import com.lmt.global.base.presenter.payment.PaymentViewModel
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

class NewPayBillsActivity : IActivity<ActivityNewPayBillsBinding, PaymentViewModel>() {

    private var amountConfirmed = false
    private var registrationNumber = ""

    override fun provideViewModel() = viewModel<PaymentViewModel>()

    override fun provideLayout() = R.layout.activity_new_pay_bills

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
        amountConfirmed = savedInstanceState?.getBoolean(STATE_CONFIRMED) ?: false
        registrationNumber = savedInstanceState?.getString(STATE_REGISTRATION_NUMBER)
            ?: createRegistrationNumber()
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
                    this@NewPayBillsActivity,
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
            viewModel.onState(PaymentAction.PayBill(createBillTransaction()))
        }
    }

    private fun createBillTransaction(): Transaction {
        val now = System.currentTimeMillis()
        val amount = viewBinding.edtAmount.getAmount()
        val formatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
            currency = Currency.getInstance(DEFAULT_CURRENCY_CODE)
        }
        val dateTime = formatDateTime(now)
        return Transaction(
            id = 0L,
            merchantName = viewBinding.tvUserName.text.toString(),
            dateTime = dateTime,
            amount = amount,
            currencyCode = DEFAULT_CURRENCY_CODE,
            merchantIconRes = R.drawable.icn_more_paybills,
            type = Transaction.TYPE_PAY_BILL,
            dueText = getString(R.string.due_amount, formatter.format(amount)),
            category = viewBinding.tvDetail.text.toString(),
            dueDate = dateTime,
            registrationNumber = registrationNumber,
            createdAt = now,
        )
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
        viewBinding.edtAmount.isFocusable = !amountConfirmed
        viewBinding.edtAmount.isFocusableInTouchMode = !amountConfirmed
        updateContentTopMargins()
        if (amountConfirmed) viewBinding.edtAmount.clearFocus()
        else viewBinding.edtAmount.requestFocus()
    }

    private fun updateContentTopMargins() {
        val marginRes = if (amountConfirmed) R.dimen._56dp else R.dimen._24dp
        val topMargin = resources.getDimensionPixelSize(marginRes)

        viewBinding.tvPayTo.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            this.topMargin = topMargin
        }
        viewBinding.tvEnterAmount.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            this.topMargin = topMargin
        }
        viewBinding.llUser.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            this.topMargin = topMargin
        }
    }

    private fun formatDateTime(timestamp: Long): String =
        Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN, Locale.ENGLISH))

    private fun createRegistrationNumber(): String =
        System.currentTimeMillis().toString().padStart(REGISTRATION_NUMBER_LENGTH, '0')

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(STATE_CONFIRMED, amountConfirmed)
        outState.putString(STATE_REGISTRATION_NUMBER, registrationNumber)
        super.onSaveInstanceState(outState)
    }

    private companion object {
        const val STATE_CONFIRMED = "amount_confirmed"
        const val STATE_REGISTRATION_NUMBER = "registration_number"
        const val DEFAULT_CURRENCY_CODE = "USD"
        const val DATE_TIME_PATTERN = "MMMM d, yyyy - HH:mm"
        const val REGISTRATION_NUMBER_LENGTH = 14
    }
}

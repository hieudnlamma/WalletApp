package com.lmt.global.base.view.bottom_sheet

import android.content.Intent
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.lmt.global.base.R
import com.lmt.global.base.common.IBottomSheetDialogFragment
import com.lmt.global.base.databinding.BottomSheetDetailSavedBillerBinding
import com.lmt.global.base.extension.onDebounceClick
import com.lmt.global.base.extension.collectLatestRepeatOnLifecycle
import com.lmt.global.base.model.Transaction
import com.lmt.global.base.presenter.payment.PaymentAction
import com.lmt.global.base.presenter.payment.PaymentEffect
import com.lmt.global.base.presenter.payment.PaymentOrTransferFailureActivity
import com.lmt.global.base.presenter.payment.PaymentOrTransferSuccessActivity
import com.lmt.global.base.presenter.payment.PaymentViewModel
import java.math.BigDecimal
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailSavedBillerBottomSheet :
    IBottomSheetDialogFragment<BottomSheetDetailSavedBillerBinding>(
        { inflater, resource, root, attachToRoot ->
            DataBindingUtil.inflate(inflater, resource, root, attachToRoot)
        },
    ) {

    private val paymentViewModel by viewModel<PaymentViewModel>()

    override fun provideLayout() = R.layout.bottom_sheet_detail_saved_biller

    override fun initViews() = with(binding) {
        val arguments = requireArguments()
        tvNameApp.text = arguments.getString(ARG_NAME).orEmpty()
        tvDatetime.text = arguments.getString(ARG_CATEGORY).orEmpty()
        tvSavedDate.text = arguments.getString(ARG_DUE_DATE).orEmpty()
        tvTransactionNumber.text = arguments.getString(ARG_REGISTRATION_NUMBER).orEmpty()
        ivAvatarApp.setImageResource(arguments.getInt(ARG_AVATAR))
        amount = arguments.getString(ARG_AMOUNT)?.toBigDecimalOrNull() ?: BigDecimal.ZERO
        currencyCode = arguments.getString(ARG_CURRENCY_CODE).orEmpty()
        executePendingBindings()
    }

    override fun initListeners() {
        binding.tvDone.onDebounceClick { dismiss() }
        binding.btnSecurePayment.onDebounceClick {
            paymentViewModel.onState(PaymentAction.PayBill(requireSavedBill()))
        }
    }

    override fun initObservers() {
        collectLatestRepeatOnLifecycle(paymentViewModel.isProcessing) { isProcessing ->
            isCancelable = !isProcessing
            binding.tvDone.isEnabled = !isProcessing
            binding.btnSecurePayment.isEnabled = !isProcessing
            binding.progressSecurePayment.isVisible = isProcessing
            binding.btnSecurePayment.text = if (isProcessing) {
                ""
            } else {
                getString(R.string.secure_payment)
            }
            binding.btnSecurePayment.icon = if (isProcessing) {
                null
            } else {
                requireContext().getDrawable(R.drawable.icn_secure_payment)
            }
        }
        collectLatestRepeatOnLifecycle(paymentViewModel.effects) { effect ->
            handlePaymentEffect(effect)
        }
    }

    private fun handlePaymentEffect(effect: PaymentEffect) {
        when (effect) {
            is PaymentEffect.Succeeded -> {
                startActivity(Intent(requireContext(), PaymentOrTransferSuccessActivity::class.java).apply {
                    putExtra(PaymentOrTransferSuccessActivity.EXTRA_TRANSACTION_ID, effect.transactionId)
                    putExtra(PaymentOrTransferSuccessActivity.EXTRA_TITLE, effect.title)
                    putExtra(PaymentOrTransferSuccessActivity.EXTRA_AMOUNT_MINOR, effect.amountMinor)
                    putExtra(PaymentOrTransferSuccessActivity.EXTRA_TYPE, effect.type)
                })
                dismiss()
            }

            PaymentEffect.InsufficientBalance -> openFailure(insufficientBalance = true)
            PaymentEffect.Failed -> openFailure(insufficientBalance = false)
        }
    }

    private fun openFailure(insufficientBalance: Boolean) {
        startActivity(Intent(requireContext(), PaymentOrTransferFailureActivity::class.java).apply {
            putExtra(
                PaymentOrTransferFailureActivity.EXTRA_INSUFFICIENT_BALANCE,
                insufficientBalance,
            )
        })
        dismiss()
    }

    private fun requireSavedBill(): Transaction {
        val arguments = requireArguments()
        return Transaction(
            id = 0L,
            merchantName = arguments.getString(ARG_NAME).orEmpty(),
            dateTime = arguments.getString(ARG_DUE_DATE).orEmpty(),
            amount = arguments.getString(ARG_AMOUNT)?.toBigDecimalOrNull() ?: BigDecimal.ZERO,
            currencyCode = arguments.getString(ARG_CURRENCY_CODE).orEmpty(),
            merchantIconRes = arguments.getInt(ARG_AVATAR),
            type = Transaction.TYPE_PAY_BILL,
            dueText = arguments.getString(ARG_DUE_TEXT),
            category = arguments.getString(ARG_CATEGORY),
            dueDate = arguments.getString(ARG_DUE_DATE),
            registrationNumber = arguments.getString(ARG_REGISTRATION_NUMBER),
        )
    }

    companion object {
        private const val ARG_NAME = "name"
        private const val ARG_AMOUNT = "amount"
        private const val ARG_CURRENCY_CODE = "currency_code"
        private const val ARG_CATEGORY = "category"
        private const val ARG_DUE_TEXT = "due_text"
        private const val ARG_DUE_DATE = "due_date"
        private const val ARG_REGISTRATION_NUMBER = "registration_number"
        private const val ARG_AVATAR = "avatar"

        fun newInstance(savedBill: Transaction) = DetailSavedBillerBottomSheet().apply {
            arguments = bundleOf(
                ARG_NAME to savedBill.merchantName,
                ARG_AMOUNT to savedBill.amount.toPlainString(),
                ARG_CURRENCY_CODE to savedBill.currencyCode,
                ARG_CATEGORY to savedBill.category,
                ARG_DUE_TEXT to savedBill.dueText,
                ARG_DUE_DATE to savedBill.dueDate,
                ARG_REGISTRATION_NUMBER to savedBill.registrationNumber,
                ARG_AVATAR to savedBill.merchantIconRes,
            )
        }
    }
}

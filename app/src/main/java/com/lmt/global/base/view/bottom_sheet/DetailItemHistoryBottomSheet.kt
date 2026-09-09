package com.lmt.global.base.view.bottom_sheet

import android.content.ClipData
import android.content.ClipboardManager
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import com.lmt.global.base.R
import com.lmt.global.base.common.IBottomSheetDialogFragment
import com.lmt.global.base.databinding.BottomSheetDetailItemHistoryBinding
import com.lmt.global.base.extension.onDebounceClick
import com.lmt.global.base.model.Transaction
import java.math.BigDecimal

class DetailItemHistoryBottomSheet :
    IBottomSheetDialogFragment<BottomSheetDetailItemHistoryBinding>(
        { inflater, resource, root, attachToRoot ->
            DataBindingUtil.inflate(inflater, resource, root, attachToRoot)
        },
    ) {

    override fun provideLayout() = R.layout.bottom_sheet_detail_item_history

    private val transactionNumber: String
        get() = requireArguments().getString(ARG_TRANSACTION_NUMBER).orEmpty()

    override fun initViews() = with(binding) {
        val arguments = requireArguments()
        val dateTime = arguments.getString(ARG_DATE_TIME).orEmpty()
        tvNameApp.text = arguments.getString(ARG_MERCHANT_NAME).orEmpty()
        tvDatetime.text = dateTime
        tvTransactionDate.text = dateTime
        tvTransactionNumber.text = transactionNumber
        ivAvatarApp.setImageResource(arguments.getInt(ARG_MERCHANT_ICON))
        amount = arguments.getString(ARG_AMOUNT)?.toBigDecimalOrNull() ?: BigDecimal.ZERO
        currencyCode = arguments.getString(ARG_CURRENCY_CODE).orEmpty()
        executePendingBindings()
    }

    override fun initListeners() {
        binding.tvDone.onDebounceClick { dismiss() }
        binding.btnCopyTransaction.onDebounceClick {
            val clipboard = requireContext().getSystemService(ClipboardManager::class.java)
            clipboard.setPrimaryClip(
                ClipData.newPlainText(getString(R.string.transaction_no), transactionNumber),
            )
        }
    }

    companion object {
        private const val ARG_MERCHANT_NAME = "merchant_name"
        private const val ARG_DATE_TIME = "date_time"
        private const val ARG_AMOUNT = "amount"
        private const val ARG_CURRENCY_CODE = "currency_code"
        private const val ARG_MERCHANT_ICON = "merchant_icon"
        private const val ARG_TRANSACTION_NUMBER = "transaction_number"

        fun newInstance(transaction: Transaction) = DetailItemHistoryBottomSheet().apply {
            arguments = bundleOf(
                ARG_MERCHANT_NAME to transaction.merchantName,
                ARG_DATE_TIME to transaction.dateTime,
                ARG_AMOUNT to transaction.amount.toPlainString(),
                ARG_CURRENCY_CODE to transaction.currencyCode,
                ARG_MERCHANT_ICON to transaction.merchantIconRes,
                ARG_TRANSACTION_NUMBER to transaction.id.toString().padStart(14, '0'),
            )
        }
    }
}

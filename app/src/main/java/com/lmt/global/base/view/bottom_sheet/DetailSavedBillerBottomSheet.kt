package com.lmt.global.base.view.bottom_sheet

import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import com.lmt.global.base.R
import com.lmt.global.base.common.IBottomSheetDialogFragment
import com.lmt.global.base.databinding.BottomSheetDetailSavedBillerBinding
import com.lmt.global.base.extension.onDebounceClick
import com.lmt.global.base.presenter.main.more.adapter.SavedBill
import java.math.BigDecimal

class DetailSavedBillerBottomSheet :
    IBottomSheetDialogFragment<BottomSheetDetailSavedBillerBinding>(
        { inflater, resource, root, attachToRoot ->
            DataBindingUtil.inflate(inflater, resource, root, attachToRoot)
        },
    ) {

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
    }

    companion object {
        private const val ARG_NAME = "name"
        private const val ARG_AMOUNT = "amount"
        private const val ARG_CURRENCY_CODE = "currency_code"
        private const val ARG_CATEGORY = "category"
        private const val ARG_DUE_DATE = "due_date"
        private const val ARG_REGISTRATION_NUMBER = "registration_number"
        private const val ARG_AVATAR = "avatar"

        fun newInstance(savedBill: SavedBill) = DetailSavedBillerBottomSheet().apply {
            arguments = bundleOf(
                ARG_NAME to savedBill.name,
                ARG_AMOUNT to savedBill.amount.toPlainString(),
                ARG_CURRENCY_CODE to savedBill.currencyCode,
                ARG_CATEGORY to savedBill.category,
                ARG_DUE_DATE to savedBill.dueDate,
                ARG_REGISTRATION_NUMBER to savedBill.registrationNumber,
                ARG_AVATAR to savedBill.avatarRes,
            )
        }
    }
}

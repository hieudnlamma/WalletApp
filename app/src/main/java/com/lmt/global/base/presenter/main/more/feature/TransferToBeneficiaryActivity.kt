package com.lmt.global.base.presenter.main.more.feature

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.lmt.global.base.R
import com.lmt.global.base.common.CommonViewModel
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivityTransferToBeneficiaryBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransferToBeneficiaryActivity :
    IActivity<ActivityTransferToBeneficiaryBinding, CommonViewModel>() {

    private var amountConfirmed = false

    override fun provideViewModel() = viewModel<CommonViewModel>()

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
        amountConfirmed = savedInstanceState?.getBoolean(STATE_CONFIRMED) ?: false
        updateAmountState()
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

    private companion object {
        const val STATE_CONFIRMED = "amount_confirmed"
    }
}

package com.lmt.global.base.presenter.main.more.feature

import android.os.Bundle
import com.lmt.global.base.R
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivityPayBillsBinding
import com.lmt.global.base.extension.applyStatusBarPadding
import com.lmt.global.base.presenter.main.more.MoreViewModel
import com.lmt.global.base.presenter.main.more.adapter.SavedBill
import com.lmt.global.base.presenter.main.more.adapter.SavedBillsAdapter
import com.lmt.global.base.view.bottom_sheet.DetailSavedBillerBottomSheet
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.math.BigDecimal

class PayBillsActivity : IActivity<ActivityPayBillsBinding, MoreViewModel>() {
    private val savedBillsAdapter = SavedBillsAdapter(::onSavedBillClick)

    override fun provideViewModel() = viewModel<MoreViewModel>()
    override fun provideLayout() = R.layout.activity_pay_bills

    override fun initViews(savedInstanceState: Bundle?) {
        viewBinding.toolbar.applyStatusBarPadding()
        setupSavedBills()
    }

    override fun initListeners() {
        viewBinding.tvBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupSavedBills() = with(viewBinding.rcvListSavedBillers) {
        adapter = savedBillsAdapter
        savedBillsAdapter.submitList(
            listOf(
                createSampleSavedBill(1L),
                createSampleSavedBill(2L),
                createSampleSavedBill(3L),
            ),
        )
    }

    private fun createSampleSavedBill(id: Long) = SavedBill(
        id = id,
        name = getString(R.string.electricity),
        dueText = getString(R.string.due_132_32),
        amount = BigDecimal("132.32"),
        currencyCode = "USD",
        category = getString(R.string.utility),
        dueDate = "December 29, 2022 - 12:32",
        registrationNumber = id.toString().padStart(14, '0'),
        avatarRes = R.drawable.icn_more_paybills,
    )

    private fun onSavedBillClick(savedBill: SavedBill) {
        DetailSavedBillerBottomSheet
            .newInstance(savedBill)
            .show(supportFragmentManager)
    }
}

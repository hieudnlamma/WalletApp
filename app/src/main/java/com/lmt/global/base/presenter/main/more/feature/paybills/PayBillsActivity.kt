package com.lmt.global.base.presenter.main.more.feature.paybills

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import com.lmt.global.base.R
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivityPayBillsBinding
import com.lmt.global.base.extension.applyStatusBarPadding
import com.lmt.global.base.extension.collectLatestRepeatOnLifecycle
import com.lmt.global.base.extension.matchesSearchQuery
import com.lmt.global.base.extension.onDebounceClick
import com.lmt.global.base.extension.onSearchQueryChanged
import com.lmt.global.base.model.Transaction
import com.lmt.global.base.presenter.main.more.adapter.SavedBillsAdapter
import com.lmt.global.base.view.bottom_sheet.DetailSavedBillerBottomSheet
import org.koin.androidx.viewmodel.ext.android.viewModel

class PayBillsActivity : IActivity<ActivityPayBillsBinding, PayBillsViewModel>() {
    private val savedBillsAdapter = SavedBillsAdapter(::onSavedBillClick)
    private var savedBills = emptyList<Transaction>()
    private var searchQuery = ""

    override fun provideViewModel() = viewModel<PayBillsViewModel>()
    override fun provideLayout() = R.layout.activity_pay_bills

    override fun initViews(savedInstanceState: Bundle?) {
        viewBinding.toolbar.applyStatusBarPadding()
        setupSavedBills()
    }

    override fun initListeners() {
        viewBinding.tvBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        viewBinding.imvAdd.onDebounceClick {
            startActivity(Intent(this, NewPayBillsActivity::class.java))
        }
        viewBinding.edtSearchPayBill.onSearchQueryChanged { query ->
            searchQuery = query
            renderSearchResults()
        }
    }

    override fun initObservers() {
        super.initObservers()
        collectLatestRepeatOnLifecycle(viewModel.savedBills) { bills ->
            savedBills = bills
            renderSearchResults()
        }
    }

    private fun setupSavedBills() = with(viewBinding.rcvListSavedBillers) {
        adapter = savedBillsAdapter
        savedBillsAdapter.submitList(emptyList())
    }

    private fun renderSearchResults() {
        val isSearching = searchQuery.isNotBlank()
        viewBinding.tvSaveBillers.isVisible = !isSearching
        savedBillsAdapter.submitList(
            savedBills.filter { bill -> bill.matchesSearchQuery(searchQuery) }
        )
    }

    private fun onSavedBillClick(savedBill: Transaction) {
        DetailSavedBillerBottomSheet
            .newInstance(savedBill)
            .show(supportFragmentManager)
    }
}

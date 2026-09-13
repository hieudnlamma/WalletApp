package com.lmt.global.base.presenter.main.history

import com.lmt.global.base.R
import com.lmt.global.base.common.IFragment
import com.lmt.global.base.databinding.FragmentHistoryBinding
import com.lmt.global.base.extension.applyStatusBarMargin
import com.lmt.global.base.extension.applyStatusBarPadding
import com.lmt.global.base.model.Transaction
import com.lmt.global.base.presenter.main.history.adapter.ContainerHistoryTransactionAdapter
import com.lmt.global.base.presenter.main.history.adapter.TransactionHistoryGroup
import com.lmt.global.base.view.bottom_sheet.DetailItemHistoryBottomSheet
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.math.BigDecimal

class HistoryFragment : IFragment<FragmentHistoryBinding, HistoryViewModel>() {
    private val historyAdapter = ContainerHistoryTransactionAdapter(::onTransactionClick)

    override fun provideViewModel() = activityViewModel<HistoryViewModel>()
    override fun provideLayout() = R.layout.fragment_history

    override fun initViews() {
        viewBinding.toolbar.applyStatusBarMargin()
        setupTransactionHistory()
    }

    private fun setupTransactionHistory() {
        viewBinding.rcvListTransactionsHistory.apply {
            adapter = historyAdapter
            isNestedScrollingEnabled = true
        }
        historyAdapter.submitList(
            listOf(
                TransactionHistoryGroup(
                    id = "today",
                    weekdayLabel = null,
                    dateLabel = getString(R.string.today),
                    transactions = listOf(
                        createMockTransaction(1L, "-35.23"),
                        createMockTransaction(2L, "120.00"),
                        createMockTransaction(3L, "-18.50"),
                    ),
                ),
                TransactionHistoryGroup(
                    id = "previous",
                    weekdayLabel = getString(R.string.thursday),
                    dateLabel = getString(R.string.yesterday),
                    transactions = listOf(
                        createMockTransaction(4L, "45.00"),
                        createMockTransaction(5L, "-22.75"),
                    ),
                ),
            ),
        )
    }

    private fun createMockTransaction(id: Long, amount: String) = Transaction(
        id = id,
        merchantName = getString(R.string.walmart),
        dateTime = getString(R.string.today_12_32),
        amount = BigDecimal(amount),
        currencyCode = "USD",
        merchantIconRes = R.drawable.img_app_default,
    )

    private fun onTransactionClick(transaction: Transaction) {
        DetailItemHistoryBottomSheet
            .newInstance(transaction)
            .show(parentFragmentManager)
    }

    override fun onDestroyView() {
        viewBinding.rcvListTransactionsHistory.adapter = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = HistoryFragment()
    }
}

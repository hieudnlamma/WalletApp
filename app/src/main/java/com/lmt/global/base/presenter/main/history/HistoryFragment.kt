package com.lmt.global.base.presenter.main.history

import androidx.core.view.isVisible
import com.lmt.global.base.R
import com.lmt.global.base.common.IFragment
import com.lmt.global.base.databinding.FragmentHistoryBinding
import com.lmt.global.base.extension.applyStatusBarMargin
import com.lmt.global.base.extension.collectLatestRepeatOnLifecycle
import com.lmt.global.base.extension.matchesSearchQuery
import com.lmt.global.base.extension.onSearchQueryChanged
import com.lmt.global.base.model.Transaction
import com.lmt.global.base.presenter.main.history.adapter.ContainerHistoryTransactionAdapter
import com.lmt.global.base.presenter.main.history.adapter.TransactionHistoryGroup
import com.lmt.global.base.view.bottom_sheet.DetailItemHistoryBottomSheet
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.util.Locale
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

class HistoryFragment : IFragment<FragmentHistoryBinding, HistoryViewModel>() {
    private val historyAdapter = ContainerHistoryTransactionAdapter(::onTransactionClick)
    private var allTransactions = emptyList<Transaction>()
    private var searchQuery = ""

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
    }

    override fun initListeners() {
        viewBinding.edtSearchHistory.onSearchQueryChanged { query ->
            searchQuery = query
            renderTransactions()
        }
    }

    override fun initObservers() {
        collectLatestRepeatOnLifecycle(viewModel.transactions) { transactions ->
            allTransactions = transactions
            renderTransactions()
        }
    }

    private fun renderTransactions() {
        val results = allTransactions.filter { transaction ->
            transaction.matchesSearchQuery(searchQuery)
        }

        val groups = groupTransactionsByDate(results)
        val isEmpty = groups.isEmpty()
        viewBinding.tvEmptyTransactions.isVisible = isEmpty
        viewBinding.rcvListTransactionsHistory.isVisible = !isEmpty

        historyAdapter.submitList(groups)
    }

    private fun groupTransactionsByDate(
        transactions: List<Transaction>,
    ): List<TransactionHistoryGroup> {
        val zoneId = ZoneId.systemDefault()
        val today = LocalDate.now(zoneId)
        return transactions
            .groupBy { Instant.ofEpochMilli(it.createdAt).atZone(zoneId).toLocalDate() }
            .map { (date, items) ->
                val (weekdayLabel, dateLabel) = when (date) {
                    today -> null to getString(R.string.today)
                    today.minusDays(1) -> null to getString(R.string.yesterday)
                    else -> date.format(
                        DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH)
                    ) to date.format(
                        DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)
                    )
                }
                TransactionHistoryGroup(
                    id = date.toString(),
                    weekdayLabel = weekdayLabel,
                    dateLabel = dateLabel,
                    transactions = items,
                )
            }
    }

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

package com.lmt.global.base.presenter.main.home

import android.content.Intent
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lmt.global.base.R
import com.lmt.global.base.common.IFragment
import com.lmt.global.base.databinding.FragmentHomeBinding
import com.lmt.global.base.extension.applyStatusBarPadding
import com.lmt.global.base.extension.collectLatestRepeatOnLifecycle
import com.lmt.global.base.extension.onDebounceClick
import com.lmt.global.base.model.Transaction
import com.lmt.global.base.presenter.main.MainActivity
import com.lmt.global.base.presenter.main.home.adapter.LatestTransactionsAdapter
import com.lmt.global.base.presenter.main.home.adapter.RecentTransfer
import com.lmt.global.base.presenter.main.home.adapter.RecentTransfersAdapter
import com.lmt.global.base.presenter.main.more.feature.transfer.TransferActivity
import com.lmt.global.base.presenter.profile.ProfileActivity
import com.lmt.global.base.view.bottom_sheet.DetailItemHistoryBottomSheet
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.text.NumberFormat
import java.util.Locale

class HomeFragment : IFragment<FragmentHomeBinding, HomeViewModel>() {
    private var recentTransfersView: RecyclerView? = null
    private var latestTransactionsView: RecyclerView? = null

    private val recentTransfersAdapter = RecentTransfersAdapter(
        onAddClick = ::onAddRecentTransfer,
        onTransferClick = ::onRecentTransferClick,
    )
    private val latestTransactionsAdapter = LatestTransactionsAdapter(::onLatestTransactionClick)

    override fun provideViewModel() = activityViewModel<HomeViewModel>()
    override fun provideLayout() = R.layout.fragment_home

    override fun initViews() {
        viewBinding.toolbar.applyStatusBarPadding()
        setupRecentTransfers()
        setupLatestTransactions()
        viewBinding.viewBalance.onTransferClicked = onDebounceClick {
            openTransfer()
        }

        viewBinding.llProfile.onDebounceClick {
            startActivity(
                Intent(requireContext(), ProfileActivity::class.java)
            )
        }

        viewBinding.viewLatestTransactions.tvViewAll.onDebounceClick {
            (requireActivity() as? MainActivity)?.openHistory()
        }

    }

    override fun initObservers() {
        collectLatestRepeatOnLifecycle(viewModel.balance) { balanceMinor ->
            renderBalance(balanceMinor)
        }
        collectLatestRepeatOnLifecycle(viewModel.transactions) { transactions ->
            latestTransactionsAdapter.submitData(transactions)
        }
        collectLatestRepeatOnLifecycle(viewModel.user) { user ->
            viewBinding.tvName.text = user?.fullName.orEmpty()
        }
        collectLatestRepeatOnLifecycle(viewModel.recentRecipients) { recipients ->
            recentTransfersAdapter.submitTransfers(
                recipients.map { recipient ->
                    RecentTransfer(
                        id = recipient.id,
                        name = recipient.name,
                        avatarRes = R.drawable.icn_avatar_default,
                    )
                }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshCurrentUser()
    }

    private fun setupRecentTransfers() {
        recentTransfersView = viewBinding.root.findViewById(R.id.rcvListRecentTransfers)
        recentTransfersView?.adapter = recentTransfersAdapter
        recentTransfersAdapter.submitTransfers(emptyList())
    }

    private fun onAddRecentTransfer() = openTransfer()

    private fun onRecentTransferClick(transfer: RecentTransfer) = Unit

    private fun openTransfer() {
        startActivity(Intent(requireContext(), TransferActivity::class.java))
    }

    private fun setupLatestTransactions() {
        latestTransactionsView = viewBinding.root.findViewById(R.id.rcvListLatestTransactions)
        latestTransactionsView?.adapter = latestTransactionsAdapter
    }

    private fun renderBalance(balanceMinor: Long) {
        val wholeAmount = balanceMinor / 100L
        val fraction = (balanceMinor % 100L).toString().padStart(2, '0')
        viewBinding.root.findViewById<TextView>(R.id.tvBalanceWhole).text =
            "$${NumberFormat.getIntegerInstance(Locale.US).format(wholeAmount)}"
        viewBinding.root.findViewById<TextView>(R.id.tvBalanceFraction).text = ".$fraction"
    }

    private fun onLatestTransactionClick(transaction: Transaction) {
        DetailItemHistoryBottomSheet
            .newInstance(transaction)
            .show(parentFragmentManager)
    }

    override fun onDestroyView() {
        recentTransfersView?.adapter = null
        recentTransfersView = null
        latestTransactionsView?.adapter = null
        latestTransactionsView = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}

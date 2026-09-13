package com.lmt.global.base.presenter.main.home

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.lmt.global.base.R
import com.lmt.global.base.common.IFragment
import com.lmt.global.base.databinding.FragmentHomeBinding
import com.lmt.global.base.extension.applyStatusBarPadding
import com.lmt.global.base.extension.onDebounceClick
import com.lmt.global.base.model.Transaction
import com.lmt.global.base.presenter.main.home.adapter.LatestTransactionsAdapter
import com.lmt.global.base.presenter.main.home.adapter.RecentTransfer
import com.lmt.global.base.presenter.main.home.adapter.RecentTransfersAdapter
import com.lmt.global.base.presenter.profile.ProfileActivity
import com.lmt.global.base.view.bottom_sheet.DetailItemHistoryBottomSheet
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.math.BigDecimal

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

        viewBinding.llProfile.onDebounceClick {
            startActivity(
                Intent(requireContext(), ProfileActivity::class.java)
            )
        }
    }

    private fun setupRecentTransfers() {
        recentTransfersView = viewBinding.root.findViewById(R.id.rcvListRecentTransfers)
        recentTransfersView?.adapter = recentTransfersAdapter
        recentTransfersAdapter.submitTransfers(
            listOf(
                RecentTransfer(
                    id = 1L,
                    name = getString(R.string.ali),
                    avatarRes = R.drawable.icn_avatar_default,
                ),
                RecentTransfer(
                    id = 2L,
                    name = getString(R.string.ali),
                    avatarRes = R.drawable.icn_avatar_default,
                ),
                RecentTransfer(
                    id = 3L,
                    name = getString(R.string.ali),
                    avatarRes = R.drawable.icn_avatar_default,
                ),
                RecentTransfer(
                    id = 4L,
                    name = getString(R.string.ali),
                    avatarRes = R.drawable.icn_avatar_default,
                ),
                RecentTransfer(
                    id = 5L,
                    name = getString(R.string.ali),
                    avatarRes = R.drawable.icn_avatar_default,
                ),
                RecentTransfer(
                    id = 6L,
                    name = getString(R.string.ali),
                    avatarRes = R.drawable.icn_avatar_default,
                ),
            ),
        )
    }

    private fun onAddRecentTransfer() = Unit

    private fun onRecentTransferClick(transfer: RecentTransfer) = Unit

    private fun setupLatestTransactions() {
        latestTransactionsView = viewBinding.root.findViewById(R.id.rcvListLatestTransactions)
        latestTransactionsView?.adapter = latestTransactionsAdapter
        latestTransactionsAdapter.submitList(
            listOf(
                Transaction(
                    id = 1L,
                    merchantName = getString(R.string.walmart),
                    dateTime = getString(R.string.today_12_32),
                    amount = BigDecimal("-35.23"),
                    currencyCode = "USD",
                    merchantIconRes = R.drawable.img_app_default,
                ),
                Transaction(
                    id = 2L,
                    merchantName = getString(R.string.walmart),
                    dateTime = getString(R.string.today_12_32),
                    amount = BigDecimal("120.00"),
                    currencyCode = "USD",
                    merchantIconRes = R.drawable.img_app_default,
                ),
                Transaction(
                    id = 3L,
                    merchantName = getString(R.string.walmart),
                    dateTime = getString(R.string.today_12_32),
                    amount = BigDecimal("-18.50"),
                    currencyCode = "USD",
                    merchantIconRes = R.drawable.img_app_default,
                ),
            ),
        )
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

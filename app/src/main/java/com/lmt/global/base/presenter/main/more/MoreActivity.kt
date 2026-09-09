package com.lmt.global.base.presenter.main.more

import android.content.Intent
import com.lmt.global.base.R
import com.lmt.global.base.common.IFragment
import com.lmt.global.base.databinding.ActivityMoreBinding
import com.lmt.global.base.extension.applyStatusBarPadding
import com.lmt.global.base.extension.onDebounceClick
import com.lmt.global.base.presenter.main.more.feature.AboutEwalletActivity
import com.lmt.global.base.presenter.main.more.feature.PayBillsActivity
import com.lmt.global.base.presenter.main.more.feature.TransferActivity
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class MoreActivity : IFragment<ActivityMoreBinding, MoreViewModel>() {
    override fun provideViewModel() = activityViewModel<MoreViewModel>()
    override fun provideLayout() = R.layout.activity_more

    override fun initViews () = with(viewBinding) {
        toolbar.applyStatusBarPadding()

        onPayBillsClicked = onDebounceClick {
            onPayBillsClick()
        }

        onTransferClicked = onDebounceClick {
            onTransferClick()
        }

        onTopUpClicked = onDebounceClick {
            onTopUpClick()
        }

        onWithdrawClicked = onDebounceClick {
            onWithdrawClick()
        }

        onAnalyticsClicked = onDebounceClick {
            onAnalyticsClick()
        }

        onHelpClicked = onDebounceClick {
            onHelpClick()
        }

        onContactUsClicked = onDebounceClick {
            onContactUsClick()
        }

        onAboutClicked = onDebounceClick {
            onAboutClick()
        }
    }

    companion object {
        fun newInstance() = MoreActivity()
    }

    private fun onPayBillsClick() {
        startActivity(
            Intent(requireContext(), PayBillsActivity::class.java)
        )
    }

    private fun onTransferClick() {
        startActivity(
            Intent(requireContext(), TransferActivity::class.java)
        )
    }

    private fun onTopUpClick() {
        // TODO navigate Top Up
    }

    private fun onWithdrawClick() {
        // TODO navigate Withdraw
    }

    private fun onAnalyticsClick() {
        // TODO navigate Analytics
    }

    private fun onHelpClick() {
        // TODO navigate Help
    }

    private fun onContactUsClick() {
        // TODO navigate Contact Us
    }

    private fun onAboutClick() {
        startActivity(
            Intent(requireContext(), AboutEwalletActivity::class.java)
        )
    }
}

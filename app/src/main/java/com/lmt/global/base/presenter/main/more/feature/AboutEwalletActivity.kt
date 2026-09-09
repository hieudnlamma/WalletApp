package com.lmt.global.base.presenter.main.more.feature

import android.os.Bundle
import com.lmt.global.base.R
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivityAboutEwalletBinding
import com.lmt.global.base.extension.applyStatusBarPadding
import com.lmt.global.base.presenter.main.more.MoreViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AboutEwalletActivity : IActivity<ActivityAboutEwalletBinding, MoreViewModel>() {
    override fun provideViewModel() = viewModel<MoreViewModel>()
    override fun provideLayout() = R.layout.activity_about_ewallet

    override fun initViews(savedInstanceState: Bundle?) {
        viewBinding.toolbar.applyStatusBarPadding()
    }

    override fun initListeners() {
        viewBinding.tvBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}

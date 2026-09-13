package com.lmt.global.base.presenter.profile

import android.os.Bundle
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import com.lmt.global.base.R
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivityProfileBinding
import com.lmt.global.base.extension.statusBars
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileActivity : IActivity<ActivityProfileBinding, ProfileViewModel>() {
    override fun provideViewModel() = viewModel<ProfileViewModel>()
    override fun provideLayout() = R.layout.activity_profile

    override fun initViews(savedInstanceState: Bundle?) {
        setupInsets()
    }

    private fun setupInsets() {
        setupApplyWindowInsetListener { insets ->
            viewBinding.toolbar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.statusBars().top
            }
        }
        ViewCompat.requestApplyInsets(window.decorView)
    }

    override fun initListeners() {
        viewBinding.tvBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}

package com.lmt.global.base.presenter.profile

import android.os.Bundle
import com.lmt.global.base.R
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivityProfileBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileActivity : IActivity<ActivityProfileBinding, ProfileViewModel>() {
    override fun provideViewModel() = viewModel<ProfileViewModel>()
    override fun provideLayout() = R.layout.activity_profile

    override fun initViews(savedInstanceState: Bundle?) {
    }

    override fun initListeners() {
        viewBinding.tvBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}

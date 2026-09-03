package com.lmt.global.base.presenter.splash

import android.annotation.SuppressLint
import android.os.Bundle
import com.lmt.global.base.R
import com.lmt.global.base.common.CommonViewModel
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivitySplashBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : IActivity<ActivitySplashBinding, CommonViewModel>() {

    override fun provideViewModel() = viewModel<CommonViewModel>()
    override fun provideLayout() = R.layout.activity_splash
    override fun initViews(savedInstanceState: Bundle?) {
        /* no-op */
    }
}

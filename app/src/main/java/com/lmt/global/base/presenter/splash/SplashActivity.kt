package com.lmt.global.base.presenter.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.core.view.postDelayed
import com.lmt.global.base.R
import com.lmt.global.base.common.CommonViewModel
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivitySplashBinding
import com.lmt.global.base.presenter.login.LoginActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.atomic.AtomicBoolean

@SuppressLint("CustomSplashScreen")
class SplashActivity : IActivity<ActivitySplashBinding, CommonViewModel>() {

    private val nextScreen = AtomicBoolean(false)

    override fun provideViewModel() = viewModel<CommonViewModel>()
    override fun provideLayout() = R.layout.activity_splash
    override fun isHideSystemBars(): Boolean = true
    override fun initViews(savedInstanceState: Bundle?) {
        enableConfigEdgeSystemBar(isFitsSystemWindows = false, isLight = false)
        viewBinding.root.postDelayed(3000L) {
            moveLogin()
        }
    }

    private fun moveLogin() {
        if (!nextScreen.compareAndSet(false, true)) {
            return
        }
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}

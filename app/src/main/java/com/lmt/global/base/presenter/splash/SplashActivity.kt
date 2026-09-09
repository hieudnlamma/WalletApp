package com.lmt.global.base.presenter.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.lmt.global.base.BuildConfig
import com.lmt.global.base.R
import com.lmt.global.base.common.CommonViewModel
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivitySplashBinding
import com.lmt.global.base.extension.isNetwork
import com.lmt.global.base.presenter.login.LoginActivity
import com.lmt.lmtech.ads.ads.ExpediteeAd
import com.lmt.lmtech.ads.billing.AppPurchase
import com.lmt.lmtech.ads.funtion.AdCallback
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.atomic.AtomicBoolean

@SuppressLint("CustomSplashScreen")
class SplashActivity : IActivity<ActivitySplashBinding, CommonViewModel>() {

    companion object {
        private const val SPLASH_MIN_DELAY_MS = 2_000L
        private const val INTER_SPLASH_TIMEOUT_MS = 20_000L
    }

    override fun provideViewModel() = viewModel<CommonViewModel>()
    override fun provideLayout() = R.layout.activity_splash

    private val mainHandler = Handler(Looper.getMainLooper())
    private val nextScreen = AtomicBoolean(false)
    private var moveRunnable: Runnable? = null

    override fun isHideSystemBars(): Boolean = true

    override fun initViews(savedInstanceState: Bundle?) {
        enableConfigEdgeSystemBar(isFitsSystemWindows = false, isLight = false)
        configManagement.fetchRemoteConfig {}
        showSplashThenMoveLogin()
    }

    private fun showSplashThenMoveLogin() {
        moveRunnable = Runnable {
            showInterSplashBeforeMoveLogin()
        }.also {
            mainHandler.postDelayed(it, SPLASH_MIN_DELAY_MS)
        }
    }

    private fun showInterSplashBeforeMoveLogin() {
        if (!isNetwork() || !configManagement.enableInterSplash || !configManagement.enableAdmobAd) {
            moveLogin()
            return
        }

        if (AppPurchase.getInstance().isPurchased(this)) {
            moveLogin()
            return
        }

        ExpediteeAd.getInstance().loadSplashInterstitialAds(
            this,
            BuildConfig.WALLET_INTER_SPLASH,
            INTER_SPLASH_TIMEOUT_MS,
            0L,
            object : AdCallback() {
                override fun onNextAction() {
                    moveLogin()
                }

                override fun onAdClosed() {
                    moveLogin()
                }

                override fun onAdFailedToLoad(i: LoadAdError?) {
                    moveLogin()
                }

                override fun onAdFailedToShow(adError: AdError?) {
                    moveLogin()
                }
            }
        )
    }

    private fun moveLogin() {
        if (!nextScreen.compareAndSet(false, true)) {
            return
        }

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        moveRunnable?.let(mainHandler::removeCallbacks)
        moveRunnable = null
        super.onDestroy()
    }
}

package com.lmt.global.base

import com.lmt.global.base.di.Feature
import com.lmt.global.base.di.allFeature
import com.lmt.global.base.model.Premium
import com.lmt.global.base.presenter.splash.SplashActivity
import com.lmt.lmtech.ads.admob.Admob
import com.lmt.lmtech.ads.admob.AppOpenManager
import com.lmt.lmtech.ads.ads.ExpediteeAd
import com.lmt.lmtech.ads.application.AdsMultiDexApplication
import com.lmt.lmtech.ads.billing.AppPurchase
import com.lmt.lmtech.ads.config.ExpediteeTechAdConfig
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : AdsMultiDexApplication() {

    companion object {
        private const val TAG = "CommonApp"
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        startKoin {
            androidContext(this@App)
            androidLogger()
            modules(allFeature().map(Feature::diModule))
        }

        setupAds()
        setupBilling()
    }

    private fun setupBilling() {
        Thread {
            AppPurchase.getInstance().initBilling(
                this@App, arrayListOf(), Premium.entries.map(Premium::id)
            )
        }.start()
    }

    private fun setupAds() = runCatching {
        val environment = if (BuildConfig.DEBUG) {
            ExpediteeTechAdConfig.ENVIRONMENT_DEVELOP
        } else {
            ExpediteeTechAdConfig.ENVIRONMENT_PRODUCTION
        }
        mExpediteeTechAdConfig = ExpediteeTechAdConfig(this, environment)

        // Optional: setup Adjust event
        mExpediteeTechAdConfig.facebookClientToken = resources.getString(R.string.facebook_client_token)
        mExpediteeTechAdConfig.intervalInterstitialAd = 20

        ExpediteeAd.getInstance().init(this, mExpediteeTechAdConfig)

        Admob.getInstance().setDisableAdResumeWhenClickAds(true)

        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity::class.java)
//        AppOpenManager.getInstance().disableAppResumeWithActivity(LanguageActivity::class.java)
//        AppOpenManager.getInstance().disableAppResumeWithActivity(OnboardingActivity::class.java)
        // AppOpenManager.getInstance().disableAppResumeWithActivity(PremiumActivity::class.java)
    }
}

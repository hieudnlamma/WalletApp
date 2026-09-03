package com.lmt.global.base.extension

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import permissions.dispatcher.PermissionUtils

fun Context?.showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

fun Context?.showToast(messageRes: Int, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, messageRes, length).show()
}

fun Context?.showToast(message: CharSequence, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

fun Context.drawable(@DrawableRes drawable: Int): Drawable? {
    return AppCompatResources.getDrawable(this, drawable)
}

fun Context.openWebBrowser(url: String) {
    val intent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse(url)
    )
    this.startActivity(intent)
}

fun Context.dpToPx(dp: Int): Float {
    return dp * resources.displayMetrics.density
}

fun Context.hasSelfPermission(vararg permissions: String): Boolean {
    return PermissionUtils.hasSelfPermissions(this, *permissions)
}

fun heightScreen() = Resources.getSystem().displayMetrics.heightPixels
fun widthScreen() = Resources.getSystem().displayMetrics.widthPixels

fun Context.isNetwork(): Boolean {
    val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return cm.activeNetworkInfo != null && cm.activeNetworkInfo?.isConnected == true
}
//
//fun Context.showInterstitialBeforeDoAction(
//    apInterstitialAd2FL: ApInterstitialAd?,
//    apInterstitialAdNormal: ApInterstitialAd?,
//    shouldShow: Boolean = true,
//    onReload: () -> Unit = {},
//    doAction: () -> Unit,
//) {
//    var myApInterstitialAd: ApInterstitialAd? = null
//    if (!isNetwork() || AppPurchase.getInstance().isPurchased) {
//        doAction.invoke()
//        return
//    }
//    if (!shouldShow) {
//        doAction.invoke()
//        return
//    }
//    if (!AdsManager.isEligibleShowInter()) {
//        doAction.invoke()
//        return
//    }
//    if (apInterstitialAd2FL != null && apInterstitialAd2FL.isReady) {
//        myApInterstitialAd = apInterstitialAd2FL
//    } else if (apInterstitialAdNormal != null && apInterstitialAdNormal.isReady) {
//        myApInterstitialAd = apInterstitialAdNormal
//    } else {
//        doAction.invoke()
//        return
//    }
//
//    DqhAd.getInstance().forceShowInterstitial(this, myApInterstitialAd, object : AdCallback() {
//        override fun onNextAction() {
//            super.onNextAction()
//            onReload.invoke()
//        }
//
//        override fun onAdClosed() {
//            super.onAdClosed()
//            AdsManager.onResetTimeShowAds()
//            doAction.invoke()
//        }
//
//        override fun onAdFailedToShow(adError: AdError?) {
//            super.onAdFailedToShow(adError)
//            doAction.invoke()
//        }
//
//        override fun onAdFailedToLoad(i: LoadAdError?) {
//            super.onAdFailedToLoad(i)
//            doAction.invoke()
//        }
//    })
//}
//
//fun Context.showInterstitialBeforeDoAction(
//    apInterstitialAd: ApInterstitialAd?,
//    shouldShow: Boolean = true,
//    onReload: () -> Unit = {},
//    doAction: () -> Unit
//) {
//    if (!isNetwork() || AppPurchase.getInstance().isPurchased) {
//        doAction.invoke()
//        return
//    }
//    if (!shouldShow) {
//        doAction.invoke()
//        return
//    }
//    if (!AdsManager.isEligibleShowInter()) {
//        doAction.invoke()
//        return
//    }
//    if (apInterstitialAd == null || apInterstitialAd.isNotReady) {
//        doAction.invoke()
//        return
//    }
//    DqhAd.getInstance().forceShowInterstitial(this, apInterstitialAd, object : AdCallback() {
//        override fun onNextAction() {
//            super.onNextAction()
//            onReload.invoke()
//        }
//
//        override fun onAdClosed() {
//            super.onAdClosed()
//            AdsManager.onResetTimeShowAds()
//            doAction.invoke()
//        }
//
//        override fun onAdFailedToShow(adError: AdError?) {
//            super.onAdFailedToShow(adError)
//            doAction.invoke()
//        }
//
//        override fun onAdFailedToLoad(i: LoadAdError?) {
//            super.onAdFailedToLoad(i)
//            doAction.invoke()
//        }
//    })
//}
//
//fun Context.forceShowInterstitialBeforeDoAction(
//    apInterstitialAd: ApInterstitialAd?,
//    shouldShow: Boolean = true,
//    onReload: () -> Unit = {},
//    doAction: () -> Unit
//) {
//    if (!isNetwork() || AppPurchase.getInstance().isPurchased) {
//        doAction.invoke()
//        return
//    }
//    if (!shouldShow) {
//        doAction.invoke()
//        return
//    }
//    if (apInterstitialAd == null || apInterstitialAd.isNotReady) {
//        doAction.invoke()
//        return
//    }
//    DqhAd.getInstance().forceShowInterstitial(this, apInterstitialAd, object : AdCallback() {
//        override fun onNextAction() {
//            super.onNextAction()
//            onReload.invoke()
//        }
//
//        override fun onAdClosed() {
//            super.onAdClosed()
//            AdsManager.onResetTimeShowAds()
//            doAction.invoke()
//        }
//
//        override fun onAdFailedToShow(adError: AdError?) {
//            super.onAdFailedToShow(adError)
//            doAction.invoke()
//        }
//
//        override fun onAdFailedToLoad(i: LoadAdError?) {
//            super.onAdFailedToLoad(i)
//            doAction.invoke()
//        }
//    })
//}

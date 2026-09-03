package com.lmt.lmtech.ads.event;

import android.content.Context;
import android.os.Bundle;

import com.lmt.lmtech.ads.config.ExpediteeTechAdConfig;
import com.lmt.lmtech.ads.funtion.AdType;
import com.google.android.gms.ads.AdValue;

public class ExpediteeLogEventManager {

    public static void logPaidAdImpression(Context context, AdValue adValue, String adUnitId, String mediationAdapterClassName, AdType adType) {
        logEventWithAds(context, (float) adValue.getValueMicros(), adValue.getPrecisionType(), adUnitId, mediationAdapterClassName, ExpediteeTechAdConfig.PROVIDER_ADMOB);
    }

    private static void logEventWithAds(Context context, float revenue, int precision, String adUnitId, String network, int mediationProvider) {
        // Chuyển đổi doanh thu sang USD
        double revenueUsd;

        Bundle params = new Bundle(); // Log ad value in micros.
        params.putDouble("valuemicros", revenue);
        params.putString("currency", "USD");
        params.putInt("precision", precision);
        params.putString("adunitid", adUnitId);
        params.putString("network", network);

        FirebaseAnalyticsUtil.logEventWithAds(context, params);

        FacebookEventUtils.logEventWithAds(context, params);
        FacebookEventUtils.logEventWithAdImpression(context, revenue / 1_000_000.0);

        // 20250401 Lam Add TikTok
        TikTokEventUtils.logEventWithAdImpression(context, revenue / 1_000_000.0 );

    }

    public static void logClickAdsEvent(Context context, String adUnitId) {
        Bundle bundle = new Bundle();
        bundle.putString("ad_unit_id", adUnitId);

        FirebaseAnalyticsUtil.logClickAdsEvent(context, bundle);
        FacebookEventUtils.logClickAdsEvent(context, bundle);
    }
}

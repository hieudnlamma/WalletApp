package com.lmt.lmtech.ads.application;

import android.app.Application;

import com.lmt.lmtech.ads.config.ExpediteeTechAdConfig;
import com.lmt.lmtech.ads.util.AppUtil;
import com.lmt.lmtech.ads.util.SharePreferenceUtils;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public abstract class ExpediteeAdsApplication extends Application {

    protected ExpediteeTechAdConfig mExpediteeTechAdConfig;
    protected List<String> listTestDevice;

    @Override
    public void onCreate() {
        super.onCreate();
        listTestDevice = new ArrayList<String>();
        mExpediteeTechAdConfig = new ExpediteeTechAdConfig(this);
        if (SharePreferenceUtils.getInstallTime(this) == 0) {
            SharePreferenceUtils.setInstallTime(this);
        }
        AppUtil.currentTotalRevenue001Ad = SharePreferenceUtils.getCurrentTotalRevenue001Ad(this);
    }

}

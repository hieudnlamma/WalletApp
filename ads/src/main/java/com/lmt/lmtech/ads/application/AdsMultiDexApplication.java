package com.lmt.lmtech.ads.application;

import androidx.multidex.MultiDexApplication;


import com.lmt.lmtech.ads.config.ExpediteeTechAdConfig;
import com.lmt.lmtech.ads.util.AppUtil;
import com.lmt.lmtech.ads.util.SharePreferenceUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class AdsMultiDexApplication extends MultiDexApplication {

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

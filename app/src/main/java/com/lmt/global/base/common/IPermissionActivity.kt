package com.lmt.global.base.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import androidx.databinding.ViewDataBinding
import com.lmt.global.base.helper.permission.IPermission
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import permissions.dispatcher.PermissionRequest

abstract class IPermissionActivity<VB : ViewDataBinding, VM : IViewModel<*>> : IActivity<VB, VM>() {

    protected val dialogHelper: DialogHelper by lazy { DialogHelper() }
    private val permissionImpl: IPermission by inject { parametersOf(this@IPermissionActivity) }

    @CallSuper
    override fun setupInit() {
        permissionImpl
    }

    override fun initViews(savedInstanceState: Bundle?) {
        /* no-op */
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    inner class DialogHelper {

        fun onShowRationaleDialog(
            request: PermissionRequest,
            @StringRes title: Int,
            @StringRes message: Int,
        ) {
//            PermissionDialog.newInstance(
//                titleRes = title,
//                messageRes = message,
//                onPositive = request::proceed,
//                onNegative = request::cancel
//            ).show(supportFragmentManager)
        }


        fun onNeverAskAgainDialog(
            @StringRes title: Int,
            @StringRes message: Int,
        ) {
//            PermissionDialog.newInstance(
//                titleRes = title,
//                messageRes = message,
//                positiveTextRes = R.string.to_settings,
//                negativeTextRes = R.string.cancel,
//                onPositive = {
//                    toSettingsByPackageName(packageName, this@IPermissionActivity)
//                },
//                onNegative = {}
//            ).show(supportFragmentManager)
        }
    }

    private fun toSettingsByPackageName(packageName: String, context: Context) {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            context.startActivity(this)
        }
    }
}

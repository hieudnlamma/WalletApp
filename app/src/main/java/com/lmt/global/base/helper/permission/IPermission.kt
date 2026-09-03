package com.lmt.global.base.helper.permission

import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.ktx.PermissionsRequester

typealias OnShowRationale = (request: PermissionRequest) -> Unit
typealias OnPermissionGranted = () -> Unit
typealias OnPermissionDenied = () -> Unit
typealias OnNeverAskAgain = () -> Unit

interface IPermission {

    fun requestPermission(
        vararg permissions: String,
        onShowRationale: OnShowRationale = {},
        onPermissionGranted: OnPermissionGranted,
        onPermissionDenied: OnPermissionDenied = {},
        onNeverAskAgain: OnNeverAskAgain = {},
    ): PermissionsRequester
}


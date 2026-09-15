package com.lmt.global.base.model

import androidx.annotation.DrawableRes

data class Recipient(
    val id: Long,
    val name: String,
    val phoneNumber: String,
    @DrawableRes val avatarRes: Int,
)

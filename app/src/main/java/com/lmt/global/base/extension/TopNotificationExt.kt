package com.lmt.global.base.extension

import android.app.Activity
import android.content.res.ColorStateList
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import androidx.core.widget.ImageViewCompat
import com.google.android.material.card.MaterialCardView
import com.lmt.global.base.R

enum class TopNotificationType(
    @ColorRes internal val accentColor: Int,
    @ColorRes internal val backgroundColor: Int,
    @DrawableRes internal val icon: Int,
) {
    INFO(R.color.hues_blue_celtic_blue, R.color.hues_blue_ghost_white, R.drawable.icn_error_circle),
    SUCCESS(R.color.hues_green_sea_green, R.color.hues_green_mint_cream, R.drawable.icn_check_circle),
    WARNING(R.color.hues_orange_golden, R.color.hues_orange_floral_white, R.drawable.icn_error_circle),
    ERROR(R.color.hues_red_golden_gate_bridge, R.color.hues_red_snow, R.drawable.icn_error_circle),
}

fun Activity.showTopNotification(
    message: CharSequence,
    type: TopNotificationType = TopNotificationType.INFO,
    actionText: CharSequence? = null,
    durationMillis: Long = DEFAULT_NOTIFICATION_DURATION,
    onAction: (() -> Unit)? = null,
) {
    val container = findViewById<FrameLayout>(android.R.id.content)
    container.findViewWithTag<View>(TOP_NOTIFICATION_TAG)?.let(container::removeView)

    val notification = LayoutInflater.from(this)
        .inflate(R.layout.view_top_notification, container, false) as MaterialCardView
    val accent = ContextCompat.getColor(this, type.accentColor)

    notification.apply {
        tag = TOP_NOTIFICATION_TAG
        setCardBackgroundColor(ContextCompat.getColor(this@showTopNotification, type.backgroundColor))
        strokeColor = accent
        importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
    }
    notification.findViewById<TextView>(R.id.tvNotificationMessage).text = message
    notification.findViewById<ImageView>(R.id.ivNotificationIcon).also { icon ->
        icon.setImageResource(type.icon)
        ImageViewCompat.setImageTintList(icon, ColorStateList.valueOf(accent))
    }

    notification.findViewById<TextView>(R.id.tvNotificationAction).apply {
        if (actionText != null && onAction != null) {
            text = actionText
            visibility = View.VISIBLE
            setOnClickListener {
                dismissTopNotification(container, notification)
                onAction()
            }
        }
    }

    val statusBarInset = ViewCompat.getRootWindowInsets(container)
        ?.getInsets(WindowInsetsCompat.Type.statusBars())
        ?.top ?: 0
    val margin = 12.dpToPx.toInt()
    container.addView(
        notification,
        FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            Gravity.TOP,
        ).apply {
            setMargins(16.dpToPx.toInt(), statusBarInset + margin, 16.dpToPx.toInt(), 0)
        }
    )

    notification.doOnLayout {
        it.translationY = -(it.height + statusBarInset + margin).toFloat()
        it.alpha = 0f
        it.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(SHOW_ANIMATION_DURATION)
            .setInterpolator(DecelerateInterpolator())
            .start()
        it.announceForAccessibility(message)
    }
    notification.postDelayed(
        { dismissTopNotification(container, notification) },
        durationMillis,
    )
}

private fun dismissTopNotification(container: FrameLayout, notification: View) {
    if (notification.parent !== container) return
    val topMargin = (notification.layoutParams as? FrameLayout.LayoutParams)?.topMargin ?: 0
    notification.animate()
        .translationY(-(notification.height + topMargin).toFloat())
        .alpha(0f)
        .setDuration(HIDE_ANIMATION_DURATION)
        .setInterpolator(AccelerateInterpolator())
        .withEndAction {
            if (notification.parent === container) container.removeView(notification)
        }
        .start()
}

private const val TOP_NOTIFICATION_TAG = "app_top_notification"
private const val DEFAULT_NOTIFICATION_DURATION = 4_000L
private const val SHOW_ANIMATION_DURATION = 280L
private const val HIDE_ANIMATION_DURATION = 220L

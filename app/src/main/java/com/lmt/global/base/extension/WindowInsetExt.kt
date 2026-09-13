package com.lmt.global.base.extension

import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding

internal fun View.applyStatusBarPadding() {
    val initialPaddingTop = paddingTop
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        view.updatePadding(top = initialPaddingTop + insets.statusBars().top)
        insets
    }
    ViewCompat.requestApplyInsets(this)
}

internal fun View.applyStatusBarMargin() {
    val initialMarginTop =
        (layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin ?: 0

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->

        view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin =
                initialMarginTop + insets.statusBars().top
        }

        insets
    }

    ViewCompat.requestApplyInsets(this)
}

internal fun WindowInsetsCompat.statusBars(ignore: Boolean = false): Insets {
    if (ignore) {
        return getInsetsIgnoringVisibility(WindowInsetsCompat.Type.statusBars())
    }
    return getInsets(WindowInsetsCompat.Type.statusBars())
}

internal fun WindowInsetsCompat.navigationBars(ignore: Boolean = false): Insets {
    if (ignore) {
        return getInsetsIgnoringVisibility(WindowInsetsCompat.Type.navigationBars())
    }
    return getInsets(WindowInsetsCompat.Type.navigationBars())
}

internal fun WindowInsetsCompat.systemBars(ignore: Boolean = false): Insets {
    if (ignore) {
        return getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars())
    }
    return getInsets(WindowInsetsCompat.Type.systemBars())
}

internal fun WindowInsetsCompat.ime(ignore: Boolean = false): Insets {
    if (ignore) {
        return getInsetsIgnoringVisibility(WindowInsetsCompat.Type.ime())
    }
    return getInsets(WindowInsetsCompat.Type.ime())
}

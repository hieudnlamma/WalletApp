package com.lmt.global.base.extension

import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

internal fun ViewPager2.registerOnPageSelected(onPageSelected: (position: Int) -> Unit) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            onPageSelected(position)
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
        }
    })
}

internal fun ViewPager2.registerPageTransformer(
    scaleY: Float = 0.8f,
    scaleX: Float = 0.9f,
    alpha: Float = 0.7f
) {
    val compositePageTransformer = CompositePageTransformer()
    compositePageTransformer.addTransformer(MarginPageTransformer(100))
    compositePageTransformer.addTransformer { view, position ->
        val r = 1 - abs(position)
        view.scaleY = scaleY + r * (1f - scaleY)
        view.scaleX = scaleX + r * (1f - scaleX)
        val absPosition = abs(position)
        view.alpha = 1.0f - alpha * absPosition
    }
    setPageTransformer(compositePageTransformer)
}

internal fun ViewPager2.nextPage(smoothScroll: Boolean = false) {
    val page = currentItem
    setCurrentItem(page + 1, smoothScroll)
}

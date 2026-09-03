package com.lmt.global.base.extension

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.smoothScrollToPositionCentered(position: Int) {
    try {
        if (position == 0 || position == ((adapter?.itemCount) ?: 0) - 1) return
        val layoutManager = this.layoutManager as? LinearLayoutManager ?: return
        val recyclerViewWidth = this.width
        val recyclerViewHeight = this.height
        val view = layoutManager.findViewByPosition(position)
        if (view != null) {
            val itemStartX = view.left
            val itemStartY = view.top
            val itemCenterX = itemStartX + view.width / 2
            val itemCenterY = itemStartY + view.height / 2
            val scrollOffsetX = itemCenterX - recyclerViewWidth / 2
            val scrollOffsetY = itemCenterY - recyclerViewHeight / 2
            this.smoothScrollBy(scrollOffsetX, scrollOffsetY)
        } else {
            layoutManager.scrollToPositionWithOffset(position, recyclerViewHeight / 2)
            post {
                val viewAfterScroll = layoutManager.findViewByPosition(position)
                viewAfterScroll?.let {
                    val itemCenterY = it.top + it.height / 2
                    val scrollOffsetY = itemCenterY - recyclerViewHeight / 2
                    this.smoothScrollBy(0, scrollOffsetY)
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun RecyclerView.smoothScrollToLastItem() {
    val itemCount = adapter?.itemCount ?: return
    if (itemCount == 0) return
    this.smoothScrollToPosition(itemCount - 1)
}


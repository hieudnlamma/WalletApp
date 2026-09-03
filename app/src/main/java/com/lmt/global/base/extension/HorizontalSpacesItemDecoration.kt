package com.lmt.global.base.extension

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalSpacesItemDecoration(
    private val space: Int,
    private val edgePadding: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        outRect.left = if (position == 0) edgePadding else space
        outRect.right = if (position == itemCount - 1) edgePadding else space
        outRect.top = 0
        outRect.bottom = 0
    }
}

fun RecyclerView.addHorizontalSpacesItemDecoration(spaceDp: Int, edgePaddingDp: Int = 0) {
    val spacePx = (spaceDp * resources.displayMetrics.density).toInt()
    val edgePaddingPx = (edgePaddingDp * resources.displayMetrics.density).toInt()
    addItemDecoration(HorizontalSpacesItemDecoration(spacePx, edgePaddingPx))
}
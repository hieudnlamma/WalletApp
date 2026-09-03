package com.lmt.global.base.extension

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(
    private val spanCount : Int,
    private val space : Int,
    private val edgePadding: Int = 0
): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) return

        val column = position % spanCount

        outRect.left = if (column == 0) edgePadding else space / 2
        outRect.right = if (column == spanCount - 1) edgePadding else space / 2

        outRect.top = if (position < spanCount) 0 else space

        outRect.bottom = 0
    }
}
fun RecyclerView.addGridSpacingItemDecoration(
    spanCount: Int,
    spaceDp: Int,
    edgePaddingDp: Int = 0
) {
    val spacePx = (spaceDp * resources.displayMetrics.density).toInt()
    val edgePaddingPx = (edgePaddingDp * resources.displayMetrics.density).toInt()
    addItemDecoration(GridSpacingItemDecoration(spanCount, spacePx, edgePaddingPx))
}
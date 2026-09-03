package com.lmt.global.base.extension

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SpacesItemDecorationExt(
    private val space: Int,
    private val includeEdge: Boolean = false
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val layoutManager = parent.layoutManager

        if (layoutManager is GridLayoutManager) {
            val spanCount = layoutManager.spanCount
            val column = position % spanCount

            if (includeEdge) {
                outRect.left = space - column * space / spanCount
                outRect.right = (column + 1) * space / spanCount
                if (position < spanCount) outRect.top = space
                outRect.bottom = space
            } else {
                outRect.left = column * space / spanCount
                outRect.right = space - (column + 1) * space / spanCount
                if (position >= spanCount) outRect.top = space
            }
        } else {
            outRect.left = if (includeEdge) space else 0
            outRect.right = if (includeEdge) space else 0
            outRect.bottom = space
            if (includeEdge && position == 0) outRect.top = space
        }
    }
}

fun RecyclerView.addSpacesItemDecoration(spaceDp: Int, includeEdge: Boolean = false) {
    val spacePx = (spaceDp * resources.displayMetrics.density).toInt()
    addItemDecoration(SpacesItemDecorationExt(spacePx, includeEdge))
}
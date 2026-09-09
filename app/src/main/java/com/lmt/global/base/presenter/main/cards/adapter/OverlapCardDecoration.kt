package com.lmt.global.base.presenter.main.cards.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class OverlapCardDecoration(
    private val cardTopStep: Int,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        val position = parent.getChildAdapterPosition(view)
        val isLastItem = position == RecyclerView.NO_POSITION || position == state.itemCount - 1
        val itemHeight = when {
            view.measuredHeight > 0 -> view.measuredHeight
            view.layoutParams.height > 0 -> view.layoutParams.height
            else -> cardTopStep
        }
        outRect.bottom = if (isLastItem) 0 else -(itemHeight - cardTopStep)
    }
}

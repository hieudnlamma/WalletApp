package com.lmt.global.base.presenter.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lmt.global.base.presenter.main.cards.CardsActivity
import com.lmt.global.base.presenter.main.history.HistoryActivity
import com.lmt.global.base.presenter.main.home.HomeActivity
import com.lmt.global.base.presenter.main.more.MoreActivity

class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            HOME -> HomeActivity.newInstance()
            HISTORY -> HistoryActivity.newInstance()
            CARDS -> CardsActivity.newInstance()
            MORE -> MoreActivity.newInstance()
            else -> HomeActivity.newInstance()
        }

    }

    override fun getItemCount(): Int = TAB_COUNT

    companion object {
        internal const val HOME = 0
        internal const val HISTORY = 1
        internal const val CARDS = 2
        internal const val MORE = 3
        internal const val TAB_COUNT = 4
    }

}

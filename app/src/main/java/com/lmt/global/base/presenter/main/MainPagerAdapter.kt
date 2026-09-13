package com.lmt.global.base.presenter.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lmt.global.base.presenter.main.cards.CardsFragment
import com.lmt.global.base.presenter.main.history.HistoryFragment
import com.lmt.global.base.presenter.main.home.HomeFragment
import com.lmt.global.base.presenter.main.more.MoreFragment

class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            HOME -> HomeFragment.newInstance()
            HISTORY -> HistoryFragment.newInstance()
            CARDS -> CardsFragment.newInstance()
            MORE -> MoreFragment.newInstance()
            else -> HomeFragment.newInstance()
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

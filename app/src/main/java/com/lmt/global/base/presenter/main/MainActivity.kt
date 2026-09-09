package com.lmt.global.base.presenter.main

import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager2.widget.ViewPager2
import com.lmt.global.base.R
import com.lmt.global.base.common.IActivity
import com.lmt.global.base.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : IActivity<ActivityMainBinding, MainViewModel>() {
    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            viewBinding.layoutBottomBar.currentIndex = position
            updateBottomBarFonts(position)
        }
    }

    override fun provideViewModel() = viewModel<MainViewModel>()
    override fun provideLayout() = R.layout.activity_main

    override fun initViews(savedInstanceState: Bundle?) {
        setupViewPager()
    }

    private fun setupViewPager() {
        viewBinding.viewPager.apply {
            adapter = MainPagerAdapter(this@MainActivity)
            offscreenPageLimit = 1
            isUserInputEnabled = false
            registerOnPageChangeCallback(pageChangeCallback)
        }

        viewBinding.layoutBottomBar.apply {
            currentIndex = viewBinding.viewPager.currentItem
            updateBottomBarFonts(viewBinding.viewPager.currentItem)
            onHomeClicked = View.OnClickListener {
                viewBinding.viewPager.setCurrentItem(MainPagerAdapter.HOME, false)
            }
            onHistoryClicked = View.OnClickListener {
                viewBinding.viewPager.setCurrentItem(MainPagerAdapter.HISTORY, false)
            }
            onCardsClicked = View.OnClickListener {
                viewBinding.viewPager.setCurrentItem(MainPagerAdapter.CARDS, false)
            }
            onMoreClicked = View.OnClickListener {
                viewBinding.viewPager.setCurrentItem(MainPagerAdapter.MORE, false)
            }
        }
    }

    private fun updateBottomBarFonts(selectedIndex: Int) {
        val regularTypeface = ResourcesCompat.getFont(this, R.font.sora_regular)
        val selectedTypeface = ResourcesCompat.getFont(this, R.font.sora_semibold)
        val tabLabels = with(viewBinding.layoutBottomBar) {
            listOf(tvHome, tvHistory, tvCards, tvMore)
        }

        tabLabels.forEachIndexed { index, textView ->
            textView.typeface = if (index == selectedIndex) selectedTypeface else regularTypeface
        }
    }

    override fun onDestroy() {
        viewBinding.viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
        super.onDestroy()
    }
}

package com.square.android.ui.fragment.intro

import android.view.View
import androidx.fragment.app.FragmentManager
import com.square.android.R
import com.square.android.data.pojo.IntroPage
import com.square.android.ui.fragment.introPage.IntroPageFragment

private val PAGES = listOf(
        IntroPage(R.string.intro_title_1, R.string.intro_content_1,
                R.drawable.intro_image_1),

        IntroPage(R.string.intro_title_2, R.string.intro_content_2,
                R.drawable.intro_image_2),

        IntroPage(R.string.intro_title_3, R.string.intro_content_3,
                R.drawable.intro_image_3)
)

class IntroPageAdapter(fragmentManager: FragmentManager, private val click: View.OnClickListener?) : androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {
    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        val page = PAGES[position]
        return IntroPageFragment.newInstance(page).apply {
            if (position < count-1) {
                onClickListener = click
            }
        }
    }

    override fun getCount() = PAGES.size
}
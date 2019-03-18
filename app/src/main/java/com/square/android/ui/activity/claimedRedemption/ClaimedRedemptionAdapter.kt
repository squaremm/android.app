package com.square.android.ui.activity.claimedRedemption

import com.square.android.App
import com.square.android.R
import com.square.android.ui.fragment.claimedActions.ClaimedActionsFragment
import com.square.android.ui.fragment.claimedCoupon.ClaimedCouponFragment

private const val ITEM_COUNT = 2

private var PAGE_TITLES_RES = listOf(R.string.page_coupon, R.string.page_actions)

private const val POSITION_COUPON = 0
private const val POSITION_ACTIONS = 1

class ClaimedRedemptionAdapter(fragmentManager: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {
    private val titles: List<String> = PAGE_TITLES_RES.map { App.getString(it) }


    override fun getItem(position: Int) =
            when (position) {
                POSITION_COUPON -> ClaimedCouponFragment()
                POSITION_ACTIONS -> ClaimedActionsFragment()
                else -> throw IllegalArgumentException("Illegal position: $position")
            }

    override fun getCount() = ITEM_COUNT

    override fun getPageTitle(position: Int) = titles[position]
}
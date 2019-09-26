package com.square.android.ui.activity.dinnerOffers

import com.square.android.data.pojo.OfferInfo
import com.square.android.ui.fragment.dinnerInfo.DinnerInfoFragment
import com.square.android.ui.fragment.dinnerOffer.DinnerOfferFragment

private const val ITEM_COUNT = 2

private const val POSITION_DINNER_OFFER = 0
private const val POSITION_DINNER_INFO = 1

class DinnerOfferDialogAdapter(fragmentManager: androidx.fragment.app.FragmentManager, val offerInfo: OfferInfo) : androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return when (position) {
            POSITION_DINNER_OFFER -> DinnerOfferFragment(offerInfo)
            POSITION_DINNER_INFO -> DinnerInfoFragment(offerInfo)
            else -> throw IllegalArgumentException("Unknown position: $position")
        }
    }

    override fun getCount() = ITEM_COUNT
}
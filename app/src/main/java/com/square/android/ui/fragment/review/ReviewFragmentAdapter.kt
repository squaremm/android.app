package com.square.android.ui.fragment.review

import com.square.android.data.pojo.Offer
import com.square.android.ui.fragment.reviewAction.ReviewActionFragment
import com.square.android.ui.fragment.reviewUpload.ReviewUploadFragment

private const val ITEM_COUNT = 2

private const val POSITION_ACTION = 0
private const val POSITION_UPLOAD = 1

class ReviewFragmentAdapter(fragmentManager: androidx.fragment.app.FragmentManager, val action: Offer.Action, val subActions: List<Offer.Action> = listOf(),
                            private var instaName: String, private var fbName: String) : androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return when (position) {
            POSITION_ACTION -> ReviewActionFragment(action, subActions, instaName, fbName)
            POSITION_UPLOAD -> ReviewUploadFragment(action.type)
            else -> throw IllegalArgumentException("Unknown position: $position")
        }
    }

    override fun getCount() = ITEM_COUNT
}
package com.square.android.ui.activity.placeDetail

import com.square.android.App
import com.square.android.R
import com.square.android.ui.fragment.aboutPlace.AboutPlaceFragment
import com.square.android.ui.fragment.booking.BookingFragment
import com.square.android.ui.fragment.offer.OfferFragment

private const val ITEM_COUNT = 3

private var PAGE_TITLES_RES = listOf(R.string.page_offer, R.string.page_book, R.string.page_about)

private const val POSITION_OFFER = 0
private const val POSITION_BOOKING = 1
private const val POSITION_ABOUT = 2

class PlaceDetailAdapter(fragmentManager: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {

    private val titles: List<String> = PAGE_TITLES_RES.map { App.getString(it) }

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return when (position) {
            POSITION_ABOUT -> AboutPlaceFragment()
            POSITION_OFFER -> OfferFragment()
            POSITION_BOOKING -> BookingFragment()
            else -> throw IllegalArgumentException("Unknown position: $position")
        }
    }

    override fun getCount() = ITEM_COUNT

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }
}
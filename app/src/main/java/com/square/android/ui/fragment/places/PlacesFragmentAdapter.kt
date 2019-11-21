package com.square.android.ui.fragment.places

import com.square.android.data.pojo.Place
import com.square.android.ui.fragment.map.MapFragment
import com.square.android.ui.fragment.placesList.PlacesListFragment

private const val ITEM_COUNT = 2
private const val POSITION_LIST = 0
private const val POSITION_MAP = 1

class PlacesFragmentAdapter(fragmentManager: androidx.fragment.app.FragmentManager, val places: MutableList<Place>) : androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return when (position) {
            POSITION_LIST -> PlacesListFragment(places)
            POSITION_MAP -> MapFragment(places)
            else -> throw IllegalArgumentException("Unknown position: $position")
        }
    }

    override fun getCount() = ITEM_COUNT
}
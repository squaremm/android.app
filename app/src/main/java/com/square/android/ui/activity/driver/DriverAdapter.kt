package com.square.android.ui.activity.driver

import com.square.android.ui.fragment.driver.DriverFragment
import com.square.android.ui.fragment.driverReturn.DriverReturnFragment

private const val ITEM_COUNT = 2

private const val POSITION_DRIVER = 0
private const val POSITION_DRIVER_BACK = 1

class DriverAdapter(fragmentManager: androidx.fragment.app.FragmentManager, val driverExtras: DriverExtras) : androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return when (position) {
            POSITION_DRIVER -> DriverFragment(driverExtras)
            POSITION_DRIVER_BACK -> DriverReturnFragment(driverExtras)
            else -> throw IllegalArgumentException("Unknown position: $position")
        }
    }

    override fun getCount() = ITEM_COUNT
}
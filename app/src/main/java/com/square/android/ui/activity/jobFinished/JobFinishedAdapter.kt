package com.square.android.ui.activity.jobFinished

import com.square.android.data.pojo.Job
import com.square.android.ui.fragment.entries.EntriesFragment
import com.square.android.ui.fragment.winner.WinnerFragment

private const val ITEM_COUNT = 2

private const val POSITION_WINNERS = 0
private const val POSITION_ENTRIES = 1

class JobFinishedAdapter(fragmentManager: androidx.fragment.app.FragmentManager, val job: Job? = null) : androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return when (position) {
            POSITION_WINNERS -> WinnerFragment(job)
            POSITION_ENTRIES -> EntriesFragment(job)
            else -> throw IllegalArgumentException("Unknown position: $position")
        }
    }

    override fun getCount() = ITEM_COUNT
}
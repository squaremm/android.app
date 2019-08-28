package com.square.android.ui.fragment.driverReturn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.square.android.R
import com.square.android.ui.activity.party.DriverExtras
import com.square.android.ui.fragment.BaseNoMvpFragment

class DriverReturnFragment(private val driverExtras: DriverExtras): BaseNoMvpFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_driver_return, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}
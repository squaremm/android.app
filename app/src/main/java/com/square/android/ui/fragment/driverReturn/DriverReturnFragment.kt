package com.square.android.ui.fragment.driverReturn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.square.android.R
import com.square.android.presentation.presenter.driverReturn.DriverReturnPresenter
import com.square.android.presentation.view.driverReturn.DriverReturnView
import com.square.android.ui.activity.driver.DriverExtras
import com.square.android.ui.fragment.BaseFragment

class DriverReturnFragment(private val driverExtras: DriverExtras): BaseFragment(), DriverReturnView {

    @InjectPresenter
    lateinit var presenter: DriverReturnPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_driver_return, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}
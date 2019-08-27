package com.square.android.ui.fragment.driver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.square.android.R
import com.square.android.presentation.presenter.driverFragment.DriverFragmentPresenter
import com.square.android.presentation.view.driverFragment.DriverFragmentView
import com.square.android.ui.activity.driver.DriverExtras
import com.square.android.ui.fragment.BaseFragment

class DriverFragment(private val driverExtras: DriverExtras): BaseFragment(), DriverFragmentView {

    @InjectPresenter
    lateinit var presenter: DriverFragmentPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_driver, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}
package com.square.android.ui.activity.driver

import android.os.Bundle
import android.os.Parcelable
import android.view.ViewGroup
import android.widget.FrameLayout
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.presentation.presenter.driver.DriverPresenter
import com.square.android.presentation.view.driver.DriverView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_driver.*
import ru.terrakok.cicerone.Navigator

@Parcelize
class DriverExtras(val driverIntervalsId: Long, val dinnerPlace: String?, val destination: String): Parcelable

const val DRIVER_EXTRAS = "DRIVER_EXTRAS"

class DriverActivity: BaseActivity(), DriverView{

    @InjectPresenter
    lateinit var presenter: DriverPresenter

    @ProvidePresenter
    fun providePresenter() = DriverPresenter(getDriverExtras())

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver)

        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)

        acDriverBack.setOnClickListener {presenter.finish()}

        //TODO load intervals first and then in showData()
        setUpPager()

    }

    private fun setUpPager() {
        acDriverPager.isPagingEnabled = false
        acDriverPager.adapter = DriverAdapter(supportFragmentManager, presenter.extras)

        //TODO check if changing fragments by clicking tabLayout's tabs is disabled. If not - disable it
        acDriverTabs.setupWithViewPager(acDriverPager)
        acDriverPager.offscreenPageLimit = 2
    }

    private fun getDriverExtras() = intent.getParcelableExtra<DriverExtras>(DRIVER_EXTRAS)
}
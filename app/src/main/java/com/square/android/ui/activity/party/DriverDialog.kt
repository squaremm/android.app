package com.square.android.ui.activity.party

import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.DialogFragment
import com.square.android.R
import kotlinx.android.synthetic.main.driver_dialog.view.*
import android.os.Bundle
import android.view.*
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.driver_dialog.*
import android.view.MotionEvent
import android.widget.LinearLayout
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.data.pojo.Place
import com.square.android.ui.fragment.driver.DriverFilledEvent
import com.square.android.ui.fragment.driver.DriverRadioEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject

class DriverExtras(val driveIntervals: List<Place.Interval>, val returnIntervals: List<Place.Interval>, val destination: String, val dinnerPlace: String? = null)

class DriverDialog(var driverExtras: DriverExtras, var mCancelable: Boolean = true): DialogFragment() {

    var currentPagerPosition = 0

    var needDriver: Boolean = true
    var departureLatLng: LatLng? = null
    var departureIntervalId: String? = null
    var driverFilled: Boolean = false

    private val eventBus: EventBus by inject()

    var needReturn: Boolean = true
    var pointOfReturn: String? = null
    var returnTimeframeId: Int? = null
    var returnFilled: Boolean = false

    init {
        eventBus.register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDriverRadioEvent(event: DriverRadioEvent) {
        driverRadioClicked(event.data)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDriverFilledEvent(event: DriverFilledEvent) {
        updateDriverFilled(event.data.driverIntervalId, event.data.latLng)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.setCancelable(mCancelable)
        dialog.window!!.setGravity(Gravity.CENTER)

        return inflater.inflate(R.layout.driver_dialog, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpPager()
        setUpPage(currentPagerPosition)

        acDriverBack.setOnClickListener {
            when (currentPagerPosition) {
                0 -> dialog.cancel()
                1 -> acDriverPager.setCurrentItem(0,true)
            }
        }

        acDriverBtn.setOnClickListener {
            when(currentPagerPosition){
                0 -> acDriverPager.setCurrentItem(1,true)
                1 -> confirmClicked()
            }
        }
    }

    private fun confirmClicked(){

    }

    private fun updateDriverFilled(intervalId: String?, latLng: LatLng?){
        this.driverFilled = intervalId != null && latLng != null

        acDriverBtn.isEnabled = driverFilled

        departureLatLng = latLng
        departureIntervalId = intervalId
    }

    private fun driverRadioClicked(needDriver: Boolean){
        this.needDriver = needDriver

        if(!needDriver){
            acDriverBtn.isEnabled = true
        } else{
            acDriverBtn.isEnabled = driverFilled
        }
    }

    private fun returnRadioClicked(needReturn: Boolean){
        this.needReturn = needReturn

        if(!needReturn){
            acDriverBtn.isEnabled = true
        } else{
            acDriverBtn.isEnabled = returnFilled
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val metrics = resources.displayMetrics
        val screenWidth = (metrics.widthPixels * 0.9).toInt()

        dialog.window?.setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun setUpPager() {
        view!!.acDriverPager.isPagingEnabled = false
        view!!.acDriverPager.adapter = DriverAdapter(childFragmentManager, driverExtras)

        view!!.acDriverTabs.setupWithViewPager(view!!.acDriverPager)
        val tabStrip = acDriverTabs.getChildAt(0) as LinearLayout
        for (i in 0 until tabStrip.childCount) {
            tabStrip.getChildAt(i).setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    return true
                }
            })
        }

        view!!.acDriverPager.offscreenPageLimit = 2

        acDriverPager.addOnPageChangeListener( object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) { }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }
            override fun onPageSelected(position: Int) {
                currentPagerPosition = position
                setUpPage(currentPagerPosition)
            }
        })
    }

    override fun onDestroy() {
        eventBus.unregister(this)
        super.onDestroy()
    }

    fun setUpPage(position: Int){
        when(position){
            0 -> {
                acDriverBtn.isEnabled = !needDriver || driverFilled
                acDriverBtn.text = getString(R.string.next)
                acDriverBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.button_arrow, 0)
                acDriverBtn.setPadding(resources.getDimensionPixelSize(R.dimen.customBtnPaddingStart),0, resources.getDimensionPixelSize(R.dimen.customBtnPaddingEnd),0)
            }
            1 -> {
                acDriverBtn.isEnabled = !needReturn || returnFilled
                acDriverBtn.text = getString(R.string.confirm)
                acDriverBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                acDriverBtn.setPadding(0,0,0,0)
            }
        }
    }
}
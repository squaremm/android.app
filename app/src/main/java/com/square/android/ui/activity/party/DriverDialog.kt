package com.square.android.ui.activity.party

import android.content.Intent
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
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.mapbox.mapboxsdk.geometry.LatLng
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject
import java.util.*
import com.google.android.libraries.places.api.Places
import com.mapbox.mapboxsdk.Mapbox.getApplicationContext
import com.square.android.Network.GOOGLE_PLACES_KEY
import com.google.android.libraries.places.widget.AutocompleteActivity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import com.square.android.data.pojo.DriverRide
import com.square.android.ui.fragment.driver.*
import com.square.android.ui.fragment.driverReturn.ReturnFilledEvent
import com.square.android.ui.fragment.driverReturn.ReturnLocationGottenEvent
import com.square.android.ui.fragment.driverReturn.ReturnRadioEvent

const val AUTOCOMPLETE_REQUEST_CODE = 666

class LocationEvent(val fromReturn: Boolean)

class DriverExtras(val driveIntervals: List<DriverRide>, val returnIntervals: List<DriverRide>, val destination: String, val dinnerPlace: String? = null, val isPremium: Boolean)

class LocationExtras(val latLng: LatLng? = null, val address: String?)

class DriverDialog(var driverExtras: DriverExtras, private val handler: Handler?, var mCancelable: Boolean = true): DialogFragment() {

    private var currentPagerPosition = 0

    private var needDriver: Boolean = true
    private var departureLatLng: LatLng? = null
    private var departureIntervalId: String? = null
    private var driverFilled: Boolean = false

    private var locationFromReturn = false

    private val eventBus: EventBus by inject()

    private var fields: List<com.google.android.libraries.places.api.model.Place.Field> = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME)

    private var needReturn: Boolean = true
    private var returnLatLng: LatLng? = null
    private var returnIntervalId: String? = null
    private var returnFilled: Boolean = false

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReturnRadioEvent(event: ReturnRadioEvent) {
        returnRadioClicked(event.data)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReturnFilledEvent(event: ReturnFilledEvent) {
        updateReturnFilled(event.data.returnIntervalId, event.data.latLng)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLocationEvent(event: LocationEvent) {
        locationFromReturn = event.fromReturn

        startLocationPicking()
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

        Places.initialize(getApplicationContext(), GOOGLE_PLACES_KEY)
        Places.createClient(context!!)
    }

    private fun confirmClicked(){
        handler?.confirmClicked(needDriver, departureLatLng, departureIntervalId, needReturn, returnLatLng, returnIntervalId)
        dialog.cancel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> data?.let {
                    val place = Autocomplete.getPlaceFromIntent(data)

                    if(locationFromReturn){
                        place.latLng?.let {
                            eventBus.post(ReturnLocationGottenEvent(LocationExtras(LatLng(it.latitude, it.longitude), place.address)))
                        }
                    } else{
                        place.latLng?.let {
                            eventBus.post(DriverLocationGottenEvent(LocationExtras(LatLng(it.latitude, it.longitude), place.address)))
                        }
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> { }
                RESULT_CANCELED -> { }
            }
        }
    }

    private fun startLocationPicking(){
        val intent: Intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(context!!)

        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    private fun updateDriverFilled(intervalId: String?, latLng: LatLng?){
        this.driverFilled = intervalId != null && latLng != null

        acDriverBtn.isEnabled = driverFilled

        departureLatLng = latLng
        departureIntervalId = intervalId
    }

    private fun updateReturnFilled(intervalId: String?, latLng: LatLng?){
        if(driverExtras.isPremium){
            this.returnFilled = intervalId != null && latLng != null
        } else{
            this.returnFilled = latLng != null
        }

        acDriverBtn.isEnabled = returnFilled

        returnLatLng = latLng
        returnIntervalId = intervalId
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
        val screenHeight = (metrics.heightPixels * 0.9).toInt()

        dialog.window?.setLayout(screenWidth, screenHeight)
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

    interface Handler {
        fun confirmClicked(needDriver: Boolean, departureLatLng: LatLng?, departureIntervalId: String?, needReturn: Boolean, returnLatLng: LatLng?, returnIntervalId: String?)
    }
}
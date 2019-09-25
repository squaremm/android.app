package com.square.android.ui.activity.event

import android.content.res.ColorStateList
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.square.android.R
import com.square.android.data.pojo.Place
import com.square.android.extensions.asDistance
import com.square.android.extensions.loadImage
import com.square.android.ui.activity.LocationActivity
import ru.terrakok.cicerone.Navigator
import android.os.Build
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.mapbox.mapboxsdk.geometry.LatLng
import com.square.android.SCREENS
import com.square.android.androidx.navigator.AppNavigator
import com.square.android.data.pojo.Event
import com.square.android.presentation.presenter.event.EventPresenter
import com.square.android.presentation.view.event.EventView
import com.square.android.ui.fragment.eventDetails.EventDetailsFragment
import com.square.android.ui.fragment.eventPlace.EventPlaceFragment
import kotlinx.android.synthetic.main.activity_event.*
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward

const val EXTRA_EVENT = "EXTRA_EVENT"

const val EXTRA_EVENT_PLACE = "EXTRA_EVENT_PLACE"

class EventExtras(val event: Event, val place: Place)

class EventActivity: LocationActivity(), EventView {

    @InjectPresenter
    lateinit var presenter: EventPresenter

    private var isCalculated = false

    private var titleMovePoint: Float = 0F

    private var titleAnimationWeight: Float = 0F

    private var titleMinHeight: Int = 0

    private var isStatusBarLight: Boolean = false

    private var timeframeSelected = false

    var placeOfferDialogShowing = false

    var placeFragment = false

    @ProvidePresenter
    fun providePresenter() = EventPresenter(intent.getParcelableExtra(EXTRA_EVENT), intent.getParcelableExtra(EXTRA_EVENT_PLACE))

    override fun provideNavigator(): Navigator = EventNavigator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        eventArrowBack.setOnClickListener {
            if(placeFragment){
                onBackPressed()
            } else{
                presenter.exit()
            }
        }

        eventAppBar.addOnOffsetChangedListener(
                AppBarLayout.OnOffsetChangedListener { appBarLayout, i ->
                    if(!isCalculated){
                        titleMovePoint = 1 - 0.9f
                        titleAnimationWeight =  1 / (1 - titleMovePoint)
                        isCalculated = true
                    }
                    updateViews(Math.abs(i / appBarLayout.totalScrollRange.toFloat()))
                })

        eventAddressCl.setOnClickListener {
            if(presenter.locationPoint?.latitude != null && presenter.locationPoint?.longitude != null && presenter.latLng != null){
                presenter.address?.let {
                    val uri = "https://www.google.com/maps/dir/?api=1&origin=${presenter.locationPoint!!.latitude},${presenter.locationPoint!!.longitude}&destination=$it&travelmode=walking"
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(uri)
                    val chooser = Intent.createChooser(intent, getString(R.string.select_an_app))
                    startActivity(chooser)
                }
            }
        }

        eventBookingBtn.setOnClickListener { presenter.bookClicked() }
    }

    private fun updateViews(offset: Float){
        when (offset) {
            in 0.555F..1F -> {

                if(!isStatusBarLight){
                    isStatusBarLight = true
                    setLightStatusBar(this)
                }

                eventArrowBack.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.black))
            }
            in 0F..0.555F -> {

                if(isStatusBarLight){
                    isStatusBarLight = false
                    clearLightStatusBar(this)
                }

                eventArrowBack.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.white))
            }
        }

        eventName.apply {
            when {
                offset > titleMovePoint -> {
                    val titleAnimationOffset = (offset - titleMovePoint) * titleAnimationWeight

                    val measuredMargin = Math.round(resources.getDimension(R.dimen.ac_place_default_margin) + ((resources.getDimension(R.dimen.backArrowSize) + resources.getDimension(R.dimen.backArrowMarginStart)) * titleAnimationOffset))
                    this.layoutParams.also {
                        (it as CollapsingToolbarLayout.LayoutParams).setMargins(Math.round(resources.getDimension(R.dimen.ac_place_default_margin)),0,measuredMargin,0)
                        this.requestLayout()
                    }
                    this.translationX = (resources.getDimension(R.dimen.backArrowSize) + resources.getDimension(R.dimen.backArrowMarginStart)) * titleAnimationOffset

                    this.height = Math.round(titleMinHeight + (resources.getDimension(R.dimen.toolbar_extra_space)* titleAnimationOffset))
                }
                else ->{
                    this.layoutParams.also {
                        translationX = 0f
                        (it as CollapsingToolbarLayout.LayoutParams).setMargins(Math.round(resources.getDimension(R.dimen.ac_place_default_margin)),0,Math.round(resources.getDimension(R.dimen.ac_place_default_margin)),0)
                    }
                }
            }
        }
    }

    override fun showProgress() {
        eventContainer.visibility = View.GONE
        eventProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        eventProgress.visibility = View.GONE
        eventContainer.visibility = View.VISIBLE
    }

    override fun showBookingProgress() {
        eventBookingBtn.visibility = View.GONE
        eventBookProgress.visibility = View.VISIBLE
    }

    fun hideBookingProgress() {
        eventBookProgress.visibility = View.GONE
        eventBookingBtn.visibility = View.VISIBLE
    }

    override fun showBottomView() {
        eventBookingCl.visibility = View.VISIBLE
        additionalPadding.visibility = View.VISIBLE
    }

    override fun hideBottomView() {
        additionalPadding.visibility = View.GONE
        eventBookingCl.visibility = View.GONE
    }

    override fun locationGotten(lastLocation: Location?) {
        presenter.locationGotten(lastLocation)
    }

    override fun showDistance(distance: Int?) {
        if (distance != null)  {
            val distanceFormatted = distance.asDistance()

            eventDistance.visibility = View.VISIBLE
            eventDistance.text = distanceFormatted
        } else {
            eventDistance.visibility = View.GONE
        }
    }

    override fun showAddress(address: String?) {
        eventAddress.text = address
    }

    override fun showData(place: Place) {
        showDistance(place.distance)
        showAddress(place.address)

        eventMainImage.loadImage(place.mainImage ?: (place.photos?.firstOrNull() ?: ""))

        eventName.text = place.name

        eventName.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                eventToolbar.apply {
                    this.layoutParams.also {
                        titleMinHeight = eventName.measuredHeight
                        eventName.height = titleMinHeight
                        it.height = Math.round(titleMinHeight + resources.getDimension(R.dimen.toolbar_extra_space))

                        eventCollapsing.layoutParams.apply {
                            this.height = Math.round(titleMinHeight + resources.getDimension(R.dimen.toolbar_image_height) + resources.getDimension(R.dimen.ac_place_default_margin))
                        }
                    }
                }
                eventName.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    fun disableButton(){
        timeframeSelected = false
        eventBookingBtn.isEnabled = false
    }

    fun setTimeframeSelected(selected: Boolean){
        timeframeSelected = selected

        eventBookingBtn.isEnabled = timeframeSelected
    }

    fun setEventBookingText(text: String){
        eventBookingText.text = text
    }

    fun showPlaceData(name: String, image: String, address: String, latLng: LatLng) {
        presenter.updateLocationAndAddress(latLng, address)

        eventMainImage.loadImage(image)

        eventName.text = name

        eventName.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                eventToolbar.apply {
                    this.layoutParams.also {
                        titleMinHeight = eventName.measuredHeight
                        eventName.height = titleMinHeight

                        it.height = Math.round(titleMinHeight + resources.getDimension(R.dimen.toolbar_extra_space))

                        eventCollapsing.layoutParams.apply {
                            this.height = Math.round(titleMinHeight + resources.getDimension(R.dimen.toolbar_image_height) + resources.getDimension(R.dimen.ac_place_default_margin))
                        }
                    }
                }
                eventName.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    //TODO fire when user navigates back to eventDetailsFragment(by himself or by clicking Select button)
    fun backToEvent(){
        presenter.updateLocationAndAddress(presenter.place.location.latLng(), presenter.place.address )

        eventMainImage.loadImage(presenter.place.mainImage ?: (presenter.place.photos?.firstOrNull() ?: ""))

        eventName.text = presenter.place.name

        eventName.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                eventToolbar.apply {
                    this.layoutParams.also {
                        titleMinHeight = eventName.measuredHeight
                        eventName.height = titleMinHeight
                        it.height = Math.round(titleMinHeight + resources.getDimension(R.dimen.toolbar_extra_space))

                        eventCollapsing.layoutParams.apply {
                            this.height = Math.round(titleMinHeight + resources.getDimension(R.dimen.toolbar_image_height) + resources.getDimension(R.dimen.ac_place_default_margin))
                        }
                    }
                }
                eventName.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    fun updateDateLabel(date: String) {
        eventBookingDate.text = date
    }

    private class EventNavigator(activity: FragmentActivity) : AppNavigator(activity, R.id.eventContainer) {

        override fun createActivityIntent(context: Context, screenKey: String, data: Any?) = null

        override fun createFragment(screenKey: String, data: Any?): Fragment? {
            return when (screenKey) {
                SCREENS.EVENT_DETAILS ->{
                    val extras = data as EventExtras
                    EventDetailsFragment.newInstance(extras.event, extras.place)
                }

                SCREENS.EVENT_PLACE -> EventPlaceFragment.newInstance(data as Place)
                else -> throw IllegalArgumentException("Unknown screen key: $screenKey")
            }
        }

        override fun setupFragmentTransactionAnimation(command: Command,
                                                       currentFragment: Fragment?,
                                                       nextFragment: Fragment,
                                                       fragmentTransaction: FragmentTransaction) {

            if(command is Forward){
                fragmentTransaction.setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right)
            } else{
                fragmentTransaction.setCustomAnimations(R.anim.fade_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.fade_out)
            }

        }
    }

    private fun setLightStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = activity.window.decorView.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity.window.decorView.systemUiVisibility = flags
        }
    }

    private fun clearLightStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = activity.window.decorView.systemUiVisibility
            flags = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity.window.decorView.systemUiVisibility = flags
        }
    }

    fun setOfferDialogShowing(showing: Boolean){
        placeOfferDialogShowing = showing
    }

    fun setIsPlaceFragment(isPlaceFragment: Boolean){
        placeFragment = isPlaceFragment
    }

    override fun onBackPressed() {
        if(placeFragment){
            if(!placeOfferDialogShowing){
                super.onBackPressed()
                placeFragment = false
                backToEvent()
            }

        } else{
            super.onBackPressed()
        }
    }
}
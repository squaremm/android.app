package com.square.android.ui.activity.party

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
import com.square.android.presentation.presenter.party.PartyPresenter
import com.square.android.presentation.view.party.PartyView
import com.square.android.ui.fragment.partyDetails.PartyDetailsFragment
import com.square.android.ui.fragment.partyPlace.PartyPlaceFragment
import kotlinx.android.synthetic.main.activity_party.*
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward

const val PARTY_EXTRA_ID = "EXTRA_ID"

const val EXTRA_PARTY = "EXTRA_PARTY"

const val EXTRA_PARTY_PLACE = "EXTRA_PARTY_PLACE"

class PartyActivity: LocationActivity(), PartyView {

    @InjectPresenter
    lateinit var presenter: PartyPresenter

    private var isCalculated = false

    private var titleMovePoint: Float = 0F

    private var titleAnimationWeight: Float = 0F

    private var titleMinHeight: Int = 0

    private var isStatusBarLight: Boolean = false

    private var timeframeSelected = false

    var placeOfferDialogShowing = false

    var placeFragment = false

    @ProvidePresenter
    fun providePresenter() = PartyPresenter(getId())

    override fun provideNavigator(): Navigator = PartyNavigator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_party)

        partyArrowBack.setOnClickListener {
            if(placeFragment){
                onBackPressed()
            } else{
                presenter.exit()
            }
        }

        partyAppBar.addOnOffsetChangedListener(
                AppBarLayout.OnOffsetChangedListener { appBarLayout, i ->
                    if(!isCalculated){
                        titleMovePoint = 1 - 0.9f
                        titleAnimationWeight =  1 / (1 - titleMovePoint)
                        isCalculated = true
                    }
                    updateViews(Math.abs(i / appBarLayout.totalScrollRange.toFloat()))
                })

        partyAddressCl.setOnClickListener {
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

        partyBookingBtn.setOnClickListener { presenter.bookClicked() }
    }

    private fun updateViews(offset: Float){
        when (offset) {
            in 0.555F..1F -> {

                if(!isStatusBarLight){
                    isStatusBarLight = true
                    setLightStatusBar(this)
                }

                partyArrowBack.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.black))
            }
            in 0F..0.555F -> {

                if(isStatusBarLight){
                    isStatusBarLight = false
                    clearLightStatusBar(this)
                }

                partyArrowBack.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.white))
            }
        }

        partyName.apply {
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
        partyContainer.visibility = View.GONE
        partyProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        partyProgress.visibility = View.GONE
        partyContainer.visibility = View.VISIBLE
    }

    override fun showBookingProgress() {
        partyBookingBtn.visibility = View.GONE
        partyBookProgress.visibility = View.VISIBLE
    }

    fun hideBookingProgress() {
        partyBookProgress.visibility = View.GONE
        partyBookingBtn.visibility = View.VISIBLE
    }

    override fun showBottomView() {
        partyBookingCl.visibility = View.VISIBLE
        additionalPadding.visibility = View.VISIBLE
    }

    override fun hideBottomView() {
        additionalPadding.visibility = View.GONE
        partyBookingCl.visibility = View.GONE
    }

    override fun locationGotten(lastLocation: Location?) {
        presenter.locationGotten(lastLocation)
    }

    override fun showDistance(distance: Int?) {
        if (distance != null)  {
            val distanceFormatted = distance.asDistance()

            partyDistance.visibility = View.VISIBLE
            partyDistance.text = distanceFormatted
        } else {
            partyDistance.visibility = View.GONE
        }
    }

    override fun showPartyData(party: Place) {
        partyMainImage.loadImage(party.mainImage ?: (party.photos?.firstOrNull() ?: ""))

        partyName.text = party.name

        partyName.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                partyToolbar.apply {
                    this.layoutParams.also {
                        titleMinHeight = partyName.measuredHeight
                        partyName.height = titleMinHeight
                        it.height = Math.round(titleMinHeight + resources.getDimension(R.dimen.toolbar_extra_space))

                        partyCollapsing.layoutParams.apply {
                            this.height = Math.round(titleMinHeight + resources.getDimension(R.dimen.toolbar_image_height) + resources.getDimension(R.dimen.ac_place_default_margin))
                        }
                    }
                }
                partyName.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    fun disableButton(){
        timeframeSelected = false
        partyBookingBtn.isEnabled = false
    }

    fun setTimeframeSelected(selected: Boolean){
        timeframeSelected = selected

        partyBookingBtn.isEnabled = timeframeSelected
    }

    fun setPartyBookingText(text: String){
        partyBookingText.text = text
    }

    fun showPlaceData(name: String, image: String, address: String, latLng: LatLng) {
        presenter.address = address
        presenter.latLng = latLng

        presenter.updateLocationInfo()

        partyMainImage.loadImage(image)

        partyName.text = name

        partyName.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                partyToolbar.apply {
                    this.layoutParams.also {
                        titleMinHeight = partyName.measuredHeight
                        partyName.height = titleMinHeight

                        it.height = Math.round(titleMinHeight + resources.getDimension(R.dimen.toolbar_extra_space))

                        partyCollapsing.layoutParams.apply {
                            this.height = Math.round(titleMinHeight + resources.getDimension(R.dimen.toolbar_image_height) + resources.getDimension(R.dimen.ac_place_default_margin))
                        }
                    }
                }
                partyName.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    //TODO fire when user navigates back to partyDetailsFragment(by himself or by clicking Select button)
    fun backToParty(){
        presenter.address = presenter.data!!.address
        presenter.latLng = presenter.data!!.location.latLng()

        presenter.updateLocationInfo()

        partyMainImage.loadImage(presenter.data!!.mainImage ?: (presenter.data!!.photos?.firstOrNull() ?: ""))

        partyName.text = presenter.data!!.name

        partyName.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                partyToolbar.apply {
                    this.layoutParams.also {
                        titleMinHeight = partyName.measuredHeight
                        partyName.height = titleMinHeight
                        it.height = Math.round(titleMinHeight + resources.getDimension(R.dimen.toolbar_extra_space))

                        partyCollapsing.layoutParams.apply {
                            this.height = Math.round(titleMinHeight + resources.getDimension(R.dimen.toolbar_image_height) + resources.getDimension(R.dimen.ac_place_default_margin))
                        }
                    }
                }
                partyName.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    fun updateDateLabel(date: String) {
        partyBookingDate.text = date
    }

    private class PartyNavigator(activity: FragmentActivity) : AppNavigator(activity, R.id.partyContainer) {

        override fun createActivityIntent(context: Context, screenKey: String, data: Any?) = null

        override fun createFragment(screenKey: String, data: Any?): Fragment? {
            return when (screenKey) {
                SCREENS.PARTY_DETAILS -> PartyDetailsFragment.newInstance(data as Place)
                SCREENS.PARTY_PLACE -> PartyPlaceFragment.newInstance(data as Place)
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
                backToParty()
            }

        } else{
            super.onBackPressed()
        }
    }

    private fun getId() = intent.getLongExtra(PARTY_EXTRA_ID, 0)
}
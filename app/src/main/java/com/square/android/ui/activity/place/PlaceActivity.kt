package com.square.android.ui.activity.place

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
import com.square.android.presentation.presenter.place.PlacePresenter
import com.square.android.presentation.view.place.PlaceView
import com.square.android.ui.activity.LocationActivity
import com.square.android.ui.base.SimpleNavigator
import kotlinx.android.synthetic.main.activity_place.*
import ru.terrakok.cicerone.Navigator
import android.os.Build
import android.app.Activity

const val PLACE_EXTRA_ID = "EXTRA_ID"

class PlaceActivity : LocationActivity(), PlaceView {
    @InjectPresenter
    lateinit var presenter: PlacePresenter

    var place: Place? = null

    private var isCalculated = false

    private var titleMovePoint: Float = 0F

    private var titleAnimationWeight: Float = 0F

    private var titleMinHeight: Int = 0

    private var isStatusBarLight: Boolean = false;

    @ProvidePresenter
    fun providePresenter() = PlacePresenter(getId())

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)

        placeArrowBack.setOnClickListener { onBackPressed() }

        placeAppBar.addOnOffsetChangedListener(
                AppBarLayout.OnOffsetChangedListener { appBarLayout, i ->
                    if(!isCalculated){
                        titleMovePoint = 1 - 0.9f
                        titleAnimationWeight =  1 / (1 - titleMovePoint)
                        isCalculated = true
                    }
                    updateViews(Math.abs(i / appBarLayout.totalScrollRange.toFloat()))
        })

        placeAddressCl.setOnClickListener {  }
    }

    private fun updateViews(offset: Float){
        when (offset) {
            in 0.555F..1F -> {

                if(!isStatusBarLight){
                    isStatusBarLight = true
                    setLightStatusBar(this)
                }

                placeArrowBack.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.black))
            }
            in 0F..0.555F -> {

                if(isStatusBarLight){
                    isStatusBarLight = false
                    clearLightStatusBar(this)
                }

                placeArrowBack.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.white))
            }
        }

        placeName.apply {
            when {
                offset > titleMovePoint -> {
                    val titleAnimationOffset = (offset - titleMovePoint) * titleAnimationWeight

                    var measuredMargin = Math.round(resources.getDimension(R.dimen.ac_place_default_margin) + ((resources.getDimension(R.dimen.backArrowSize) + resources.getDimension(R.dimen.backArrowMarginStart)) * titleAnimationOffset))
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

    override fun locationGotten(lastLocation: Location?) {
        presenter.locationGotten(lastLocation)
    }

    override fun showDistance(distance: Int?) {
        if (distance != null)  {
            val distanceFormatted = distance.asDistance()

            placeDistance.visibility = View.VISIBLE
            placeDistance.text = distanceFormatted
        } else {
            placeDistance.visibility = View.GONE
        }
    }

    override fun showData(place: Place) {
        this.place = place

        placeMainImage.loadImage(place.mainImage ?: (place.photos?.firstOrNull() ?: ""))

        //TODO delete
        placeName.text = "Don Giovanni's BBC Pizza Italy"

        //TODO uncomment
//        placeName.text = place.name

        placeName.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                    placeToolbar.apply {
                        this.layoutParams.also {
                            titleMinHeight = placeName.measuredHeight
                            placeName.height = titleMinHeight

                            it.height = Math.round(titleMinHeight + resources.getDimension(R.dimen.toolbar_extra_space))

                            placeCollapsing.layoutParams.apply {
                                this.height = Math.round(titleMinHeight + resources.getDimension(R.dimen.toolbar_image_height) + resources.getDimension(R.dimen.ac_place_default_margin))
                            }
                        }
                    }
                placeName.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        placeAddress.text = place.address
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

    private fun getId() = intent.getLongExtra(PLACE_EXTRA_ID, 0)
}
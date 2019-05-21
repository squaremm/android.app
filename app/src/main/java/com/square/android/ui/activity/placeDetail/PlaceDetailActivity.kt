package com.square.android.ui.activity.placeDetail

import android.location.Location
import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.Place
import com.square.android.extensions.asDistance
import com.square.android.extensions.loadFirstOrPlaceholder
import com.square.android.presentation.presenter.placeDetail.PlaceDetailPresenter
import com.square.android.presentation.view.placeDetail.PlaceDetailView
import com.square.android.ui.activity.LocationActivity
import com.square.android.ui.base.SimpleNavigator
import com.square.android.ui.base.tutorial.Tutorial
import com.square.android.ui.base.tutorial.TutorialService
import com.square.android.ui.base.tutorial.TutorialStep
import com.square.android.ui.base.tutorial.TutorialView
import kotlinx.android.synthetic.main.activity_place_detail.*
import ru.terrakok.cicerone.Navigator

const val PLACE_EXTRA_ID = "EXTRA_ID"

class PlaceDetailActivity : LocationActivity(TutorialService.TUTORIAL_1_PLACE), PlaceDetailView {
    @InjectPresenter
    lateinit var presenter: PlaceDetailPresenter

    var place: Place? = null

    @ProvidePresenter
    fun providePresenter() = PlaceDetailPresenter(getId())

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_detail)

        setSupportActionBar(placeDetailToolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        placeDetailFade.layoutParams.height = calculateFadeHeight()
    }

    override val tutorial: Tutorial?
        get() =  Tutorial.Builder()
                .addNextStep(TutorialStep(
                        // width percentage, height percentage for text with arrow
                        floatArrayOf(0.60f, 0.565f),
                        getString(R.string.tut_1_1),
                        TutorialStep.ArrowPos.BOTTOM,
                        R.drawable.arrow_bottom_left_x_top_right,
                        0.3f,
                        // marginStart dp, marginEnd dp, horizontal center of the transView in 0.0f - 1f, height of the transView in dp
                        // 0f,0f,0f,0f for covering entire screen
                        floatArrayOf(0f,0f,0.76f,88f),
                        1,
                        // delay before showing view in ms
                        0f))

                .setOnNextStepIsChangingListener(object: TutorialView.OnNextStepIsChangingListener{
                    override fun onNextStepIsChanging(targetStepNumber: Int) {
                        println("EEEE step number: "+ targetStepNumber)
                    }
                })
                .build()

    override fun locationGotten(lastLocation: Location?) {
        presenter.locationGotten(lastLocation)
    }

    override fun showDistance(distance: Int?) {
        if (distance != null)  {
            val distanceFormatted = distance.asDistance()

            placeDetailDistance.visibility = View.VISIBLE
            placeDetailDistance.text =distanceFormatted
        } else {
            placeDetailDistance.visibility = View.GONE
        }
    }


    override fun showData(place: Place) {
        this.place = place

        setUpPager()

        placeDetailAvatar.loadFirstOrPlaceholder(place.photos)

        placeDetailName.text = place.name
        placeDetailAddress.text = place.address
        placeDetailType.text = place.type
    }

    private fun setUpPager() {
        placeDetailPager.adapter = PlaceDetailAdapter(supportFragmentManager, place)
        placeDetailTab.setupWithViewPager(placeDetailPager)
        placeDetailPager.offscreenPageLimit = 2

    }

    private fun getId() = intent.getLongExtra(PLACE_EXTRA_ID, 0)
}

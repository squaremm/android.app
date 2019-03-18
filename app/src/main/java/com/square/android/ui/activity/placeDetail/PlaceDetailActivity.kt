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
import com.square.android.extensions.loadImage
import com.square.android.presentation.presenter.placeDetail.PlaceDetailPresenter
import com.square.android.presentation.view.placeDetail.PlaceDetailView
import com.square.android.ui.activity.LocationActivity
import com.square.android.ui.base.SimpleNavigator
import kotlinx.android.synthetic.main.activity_place_detail.*
import ru.terrakok.cicerone.Navigator

const val PLACE_EXTRA_ID = "EXTRA_ID"

class PlaceDetailActivity : LocationActivity(), PlaceDetailView {
    @InjectPresenter
    lateinit var presenter: PlaceDetailPresenter

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

    override fun locationGotten(lastLocation: Location?) {
        presenter.locationGotten(lastLocation)
    }

    override fun showDistance(distance: Int?) {
        if (distance != null)  {
            val distanceFormatted = getString(R.string.distance_format, distance.asDistance())

            placeDetailDistance.visibility = View.VISIBLE
            placeDetailDistance.text =distanceFormatted
        } else {
            placeDetailDistance.visibility = View.GONE
        }
    }


    override fun showData(place: Place) {
        setUpPager()

        placeDetailAvatar.loadFirstOrPlaceholder(place.photos)

        placeDetailName.text = place.name
        placeDetailAddress.text = place.address
        placeDetailType.text = place.type
    }

    private fun setUpPager() {
        placeDetailPager.adapter = PlaceDetailAdapter(supportFragmentManager)
        placeDetailTab.setupWithViewPager(placeDetailPager)
        placeDetailPager.offscreenPageLimit = 2
    }

    private fun getId() = intent.getLongExtra(PLACE_EXTRA_ID, 0)
}

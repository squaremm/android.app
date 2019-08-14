package com.square.android.ui.activity.place

import android.location.Location
import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
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

const val PLACE_EXTRA_ID = "EXTRA_ID"

class PlaceActivity : LocationActivity(), PlaceView {
    @InjectPresenter
    lateinit var presenter: PlacePresenter

    var place: Place? = null

    @ProvidePresenter
    fun providePresenter() = PlacePresenter(getId())

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)

        placeArrowBack.setOnClickListener { onBackPressed() }
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

        placeName.text = place.name
        placeAddress.text = place.address
    }

    private fun getId() = intent.getLongExtra(PLACE_EXTRA_ID, 0)
}
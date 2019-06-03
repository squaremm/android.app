package com.square.android.ui.fragment.places

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.square.android.R
import com.square.android.data.pojo.Place
import com.square.android.presentation.presenter.places.PlacesPresenter
import com.square.android.presentation.view.places.PlacesView
import com.square.android.ui.fragment.LocationFragment
import kotlinx.android.synthetic.main.fragment_places.*

class PlacesFragment : LocationFragment(), PlacesView, PlacesAdapter.Handler {

    @InjectPresenter(type = PresenterType.GLOBAL, tag = "PlacesPresenter")
    lateinit var presenter: PlacesPresenter

    private var adapter: PlacesAdapter? = null

    private var filtersDialog: FiltersDialog? = null

    override fun showProgress() {
        placesProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        placesProgress.visibility = View.GONE
    }

    override fun showPlaces(data: List<Place>, types: MutableList<String>) {
        placesList.visibility = View.VISIBLE

        adapter = PlacesAdapter(data, this)
        placesList.adapter = adapter

        filtersDialog = FiltersDialog(context!!, types){ presenter.saveClicked(it) }
    }

    override fun showBadge(number: Int) {
        if(number <= 0){
            placeBadge.visibility = View.GONE
        } else{
            placeBadge.visibility = View.VISIBLE
            placeBadge.text = if(number > 9) "9+" else number.toString()
        }
    }

    override fun updatePlaces(data: List<Place>) {
        adapter = PlacesAdapter(data, this)
        placesList.adapter = adapter
    }

    override fun updateDistances() {
        adapter?.updateDistances()
    }

    override fun locationGotten(lastLocation: Location?) {
        presenter.locationGotten(lastLocation)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_places, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placesList.setHasFixedSize(true)

        placesFilter.setOnClickListener{filtersDialog?.show(presenter.filteredTypes)}
        placeBadge.setOnClickListener{placesFilter.performClick()}

        showBadge(presenter.filteredTypes.size)
    }

    override fun itemClicked(position: Int) {
        presenter.itemClicked(position)
    }
}

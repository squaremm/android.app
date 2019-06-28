package com.square.android.ui.fragment.places

import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.square.android.R
import com.square.android.data.pojo.Place
import com.square.android.presentation.presenter.places.PlacesPresenter
import com.square.android.presentation.view.places.PlacesView
import com.square.android.ui.fragment.LocationFragment
import com.square.android.ui.fragment.map.MarginItemDecorator
import kotlinx.android.synthetic.main.fragment_places.*

class PlacesFragment: LocationFragment(), PlacesView, PlacesAdapter.Handler, FiltersAdapter.Handler {

    @InjectPresenter(type = PresenterType.GLOBAL, tag = "PlacesPresenter")
    lateinit var presenter: PlacesPresenter

    private var adapter: PlacesAdapter? = null

    private var filtersAdapter: FiltersAdapter? = null

    override fun showProgress() {
        placesProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        placesProgress.visibility = View.GONE
    }

    override fun showPlaces(data: List<Place>, types: MutableList<String>) {
        placesList.visibility = View.VISIBLE

        if(data.isNotEmpty()){
            placesFiltersRv.visibility = View.VISIBLE
            placesSearchLl.visibility = View.VISIBLE
        }

        adapter = PlacesAdapter(data, this)
        placesList.adapter = adapter

        filtersAdapter =  FiltersAdapter(types, this)

        placesFiltersRv.adapter = filtersAdapter
        placesFiltersRv.layoutManager = LinearLayoutManager(placesFiltersRv.context, RecyclerView.HORIZONTAL,false)
        placesFiltersRv.addItemDecoration(MarginItemDecorator(placesFiltersRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), false))
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

        placesSearch.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                presenter.searchTextChanged(s)
            }
        })

    }

    override fun itemClicked(position: Int) {
        presenter.itemClicked(position)
    }

    override fun setSelectedFilterItem(position: Int, contains: Boolean) {
        filtersAdapter?.setSelectedItem(position, contains)
    }

    override fun filterClicked(position: Int) {
        presenter.filterClicked(position)
    }

}

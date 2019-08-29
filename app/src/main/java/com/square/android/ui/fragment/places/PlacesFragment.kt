package com.square.android.ui.fragment.places

import android.content.res.ColorStateList
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
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
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat

class PlacesFragment: LocationFragment(), PlacesView, PlacesAdapter.Handler, FiltersAdapter.Handler {

    @InjectPresenter(type = PresenterType.GLOBAL, tag = "PlacesPresenter")
    lateinit var presenter: PlacesPresenter

    private var adapter: PlacesAdapter? = null

    private var filterDays = false
    private var filterTypes = false

    private var clearCanBeVisible = false

    private var filtersAdapter: FiltersAdapter? = null

    override fun showProgress() {
        placesProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        placesProgress.visibility = View.GONE
    }

    override fun showPlaces(data: List<Place>, types: MutableList<String>) {
        placesList.visibility = View.VISIBLE

        //TODO uncomment
//        if(data.isNotEmpty()){
//            placesFiltersRv.visibility = View.VISIBLE
//            placesSearchLl.visibility = View.VISIBLE
//        }
//
//        adapter = PlacesAdapter(data, this)
//        placesList.adapter = adapter
//

        filtersAdapter =  FiltersAdapter(types, this)

        placesFiltersTypesRv.adapter = filtersAdapter
        placesFiltersTypesRv.layoutManager = LinearLayoutManager(placesFiltersTypesRv.context, RecyclerView.HORIZONTAL,false)
        placesFiltersTypesRv.addItemDecoration(MarginItemDecorator(placesFiltersTypesRv.context.resources.getDimension(R.dimen.rv_item_decorator_4).toInt(), false))
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

                if(TextUtils.isEmpty(s)){
                    placesRemoveIcon.visibility = View.GONE
                    placesSearchIcon.visibility = View.VISIBLE
                } else{
                    placesSearchIcon.visibility = View.GONE
                    placesRemoveIcon.visibility = View.VISIBLE
                }
                presenter.searchTextChanged(s)
            }
        })

        if(presenter.initialized){ refreshViews() }

        placesIcDays.setOnClickListener {
            filterDays = filterDays.not()
            changeFiltering()
        }
        placesIcTypes.setOnClickListener {
            filterTypes = filterTypes.not()
            changeFiltering()
        }

        placesClear.setOnClickListener { presenter.clearFilters() }

        placesRemoveIcon.setOnClickListener { placesSearch.setText(null) }
    }

    private fun refreshViews(){
        if((filterDays && filterTypes) || (!filterDays && !filterTypes)){
            placesSearch.setText(presenter.searchText)
        } else{
            if(filterTypes){
                placesFiltersTypesRv?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        presenter.refreshRvForTypes()
                        placesFiltersTypesRv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }

            if(filterDays){
                placesFiltersDaysRv?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        presenter.refreshRvForDays()
                        placesFiltersDaysRv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }
        }
    }

    private fun changeFiltering(){
        placesIcDays.imageTintList = if(filterDays) ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.nice_pink))
        else ColorStateList.valueOf(ContextCompat.getColor(activity!!, android.R.color.black))

        placesIcTypes.imageTintList = if(filterTypes) ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.nice_pink))
        else ColorStateList.valueOf(ContextCompat.getColor(activity!!, android.R.color.black))

        if((filterDays && filterTypes) || (!filterDays && !filterTypes)){
            clearCanBeVisible = false

            placesFiltersTypesRv.visibility = View.GONE
            placesFiltersDaysRv.visibility = View.GONE
            placesSearchLl.visibility = View.VISIBLE

            presenter.changeFiltering(1)
        } else{
            clearCanBeVisible = true

            placesSearchLl.visibility = View.GONE

            placesFiltersTypesRv.visibility = if(filterTypes) View.VISIBLE else View.GONE
            placesFiltersDaysRv.visibility = if(filterDays) View.VISIBLE else View.GONE

            if(filterDays){
                presenter.changeFiltering(2)
            } else{
                presenter.changeFiltering(3)
            }
        }

        refreshViews()
    }

    override fun itemClicked(place: Place) {
        presenter.itemClicked(place)
    }

    override fun setSelectedFilterItem(position: Int, contains: Boolean) {
        filtersAdapter?.setSelectedItem(position, contains)
    }

    override fun filterClicked(position: Int) {
        presenter.filterClicked(position)
    }

}

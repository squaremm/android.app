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

class PlacesFragment: LocationFragment(), PlacesView, PlacesAdapter.Handler, FiltersAdapter.Handler, DaysShortAdapter.Handler {

    @InjectPresenter(type = PresenterType.GLOBAL, tag = "PlacesPresenter")
    lateinit var presenter: PlacesPresenter

    private var adapter: PlacesAdapter? = null

    private var filterDays = false
    private var filterTypes = false

    private var filtersAdapter: FiltersAdapter? = null

    private var daysAdapter: DaysShortAdapter? = null

    override fun showProgress() {
        placesProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        placesProgress.visibility = View.GONE
    }

    override fun showData(data: List<Place>, types: MutableList<String>, activatedItems: MutableList<String>, days: MutableList<String>) {
        placesList.visibility = View.VISIBLE

        //TODO uncomment
//        if(data.isNotEmpty()){
//            placesSearchLl.visibility = View.VISIBLE
//        }
//
//        adapter = PlacesAdapter(data, this)
//        placesList.adapter = adapter

        filtersAdapter = FiltersAdapter(types, this, activatedItems)

        placesFiltersTypesRv.adapter = filtersAdapter
        placesFiltersTypesRv.layoutManager = LinearLayoutManager(placesFiltersTypesRv.context, RecyclerView.HORIZONTAL,false)
        placesFiltersTypesRv.addItemDecoration(MarginItemDecorator(placesFiltersTypesRv.context.resources.getDimension(R.dimen.rv_item_decorator_4).toInt(), false))

        daysAdapter = DaysShortAdapter(days, this)
        placesFiltersDaysRv.adapter = daysAdapter
        placesFiltersDaysRv.layoutManager = LinearLayoutManager(placesFiltersDaysRv.context, RecyclerView.HORIZONTAL,false)
        placesFiltersDaysRv.addItemDecoration(MarginItemDecorator(placesFiltersDaysRv.context.resources.getDimension(R.dimen.rv_item_decorator_4).toInt(), false))
    }

    override fun updatePlaces(data: List<Place>) {
        adapter = PlacesAdapter(data, this)
        placesList.adapter = adapter
    }

    override fun updateFilters(activated: MutableList<String>) {
        filtersAdapter?.updateData(activated)
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

        //TODO when type filter is selected and filters list not even showed, when changing main tab to another and coming back here - "Clear all" is visible and I have no idea why

        if(presenter.initialized){
            filterDays = false
            filterTypes = false

            when(presenter.filteringMode){
                2 -> filterDays = true
                3 -> filterTypes = true
            }

            changeFiltering()
        }

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
                        presenter.refreshViewsForTypes()
                        placesFiltersTypesRv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }

            if(filterDays){
                placesFiltersDaysRv?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        presenter.refreshViewsForDays()
                        placesFiltersDaysRv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }
        }
    }

    private fun changeFiltering(){

        //TODO not working when coming back from another fragment
        hideClear()

        placesIcDays.imageTintList = if(filterDays) ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.nice_pink))
        else ColorStateList.valueOf(ContextCompat.getColor(activity!!, android.R.color.black))

        placesIcTypes.imageTintList = if(filterTypes) ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.nice_pink))
        else ColorStateList.valueOf(ContextCompat.getColor(activity!!, android.R.color.black))

        if((filterDays && filterTypes) || (!filterDays && !filterTypes)){

            placesFiltersTypesRv.visibility = View.GONE
            placesFiltersDaysRv.visibility = View.GONE
            placesSearchLl.visibility = View.VISIBLE

            presenter.changeFiltering(1)
        } else{
            placesSearchLl.visibility = View.GONE

            placesFiltersTypesRv.visibility = if(filterTypes) View.VISIBLE else View.GONE
            placesFiltersDaysRv.visibility = if(filterDays) View.VISIBLE else View.GONE

            if(filterDays){
                presenter.changeFiltering(2)
            } else{
                presenter.changeFiltering(3)

                if(presenter.shouldShowClear) showClear() else hideClear()
            }
        }

        refreshViews()
    }

    override fun hideClear() {
        placesClear.visibility = View.GONE
    }

    override fun showClear() {
        placesClear.visibility = View.VISIBLE
    }

    override fun itemClicked(place: Place) {
        presenter.itemClicked(place)
    }

    override fun setSelectedFilterItems(positions: List<Int>) {
        filtersAdapter?.setSelectedItems(positions)
    }

    override fun filterClicked(position: Int) {
        presenter.filterClicked(position)
    }

    override fun setSelectedDayItem(position: Int) {
        daysAdapter?.setSelectedItem(position)
    }

    override fun dayItemClicked(position: Int){
        presenter.dayClicked(position)
    }

}

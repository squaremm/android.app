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
import androidx.core.content.ContextCompat

class PlacesFragment: LocationFragment(), PlacesView, PlacesAdapter.Handler, FiltersAdapter.Handler, DaysShortAdapter.Handler {

    @InjectPresenter(type = PresenterType.GLOBAL, tag = "PlacesPresenter")
    lateinit var presenter: PlacesPresenter

    private var adapter: PlacesAdapter? = null

    private var filterDays = false
    private var filterTypes = false

    var ignoreText = false

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

        adapter = PlacesAdapter(data, this)
        placesList.adapter = adapter

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

    override fun updateFilters(types: MutableList<String>, activated: MutableList<String>, updateAll: Boolean) {
        if(updateAll){
            filtersAdapter = FiltersAdapter(types,this, activated)
            placesFiltersTypesRv.adapter = filtersAdapter
        }

        filtersAdapter!!.updateData(activated)
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

                if(!ignoreText){
                    presenter.searchTextChanged(s)
                } else{
                    ignoreText = false
                }
            }
        })

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

        if(presenter.initialized){
            when(presenter.filteringMode){
                1 ->{ filterDays = false
                    filterTypes = false
                    placesTypes.visibility = View.GONE
                    placesSearchLl.visibility = View.VISIBLE
                    placesFiltersDaysRv.visibility = View.GONE

                    ignoreText = true
                    placesSearch.setText(presenter.searchText)
                }
                2 -> { filterDays = true
                    filterTypes = false
                    placesTypes.visibility = View.GONE
                    placesSearchLl.visibility = View.GONE
                    placesFiltersDaysRv.visibility = View.VISIBLE
                }
                3 -> { filterDays = false
                    filterTypes = true
                    placesTypes.visibility = View.VISIBLE
                    placesSearchLl.visibility = View.GONE
                    placesFiltersDaysRv.visibility = View.GONE
                }
            }

            placesIcDays.imageTintList = if(filterDays) ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.nice_pink))
            else ColorStateList.valueOf(ContextCompat.getColor(activity!!, android.R.color.black))

            placesIcTypes.imageTintList = if(filterTypes) ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.nice_pink))
            else ColorStateList.valueOf(ContextCompat.getColor(activity!!, android.R.color.black))
        }

    }

    private fun changeFiltering(){
        placesIcDays.imageTintList = if(filterDays) ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.nice_pink))
        else ColorStateList.valueOf(ContextCompat.getColor(activity!!, android.R.color.black))

        placesIcTypes.imageTintList = if(filterTypes) ColorStateList.valueOf(ContextCompat.getColor(activity!!, R.color.nice_pink))
        else ColorStateList.valueOf(ContextCompat.getColor(activity!!, android.R.color.black))

        if((filterDays && filterTypes) || (!filterDays && !filterTypes)){
            placesTypes.visibility = View.GONE
            placesFiltersDaysRv.visibility = View.GONE
            placesSearchLl.visibility = View.VISIBLE
            presenter.changeFiltering(1)

        } else{
            placesSearchLl.visibility = View.GONE

            placesTypes.visibility = if(filterTypes) View.VISIBLE else View.GONE
            placesFiltersDaysRv.visibility = if(filterDays) View.VISIBLE else View.GONE

            if(filterDays){
                presenter.changeFiltering(2)
            } else{
                presenter.changeFiltering(3)
            }
        }
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

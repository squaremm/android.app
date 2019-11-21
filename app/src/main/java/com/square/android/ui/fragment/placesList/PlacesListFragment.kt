package com.square.android.ui.fragment.placesList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.Place
import com.square.android.presentation.presenter.placesList.PlacesListPresenter
import com.square.android.presentation.view.placesList.PlacesListView
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.map.MarginItemDecorator
import kotlinx.android.synthetic.main.fragment_places_list.*

class PlacesListFragment(var data: MutableList<Place>): BaseFragment(), PlacesListView, PlacesAdapter.Handler{

    @InjectPresenter
    lateinit var presenter: PlacesListPresenter

    @ProvidePresenter
    fun providePresenter() = PlacesListPresenter(data)

    private var adapter: PlacesAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_places_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placesList.setHasFixedSize(true)
    }

    override fun updateDistances() {
        adapter?.updateDistances()
    }

    override fun showData(data: List<Place>) {
        placesList.visibility = View.VISIBLE

        adapter = PlacesAdapter(data, this)
        placesList.adapter = adapter
        placesList.layoutManager = LinearLayoutManager(placesList.context, RecyclerView.VERTICAL,false)
        placesList.addItemDecoration(MarginItemDecorator(placesList.context.resources.getDimension(R.dimen.rv_item_decorator_12).toInt(), true))
    }

    override fun updatePlaces(data: List<Place>) {
        adapter = PlacesAdapter(data, this)
        placesList.adapter = adapter
    }

    override fun itemClicked(place: Place) {
        presenter.itemClicked(place)
    }
}
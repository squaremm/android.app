package com.square.android.ui.fragment.places

import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.DialogFragment
import com.square.android.R
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.square.android.data.pojo.City
import com.square.android.ui.fragment.map.MarginItemDecorator
import kotlinx.android.synthetic.main.dialog_cities.*

class DialogCities(var cities: List<City>, var selectedCity: City?, private val handler: Handler?, var mCancelable: Boolean = true): DialogFragment(), CitiesAdapter.Handler {

    private var selectedCityIndex = 0

    private var adapter: CitiesAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.setCancelable(mCancelable)
        dialog.window!!.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)

        return inflater.inflate(R.layout.dialog_cities, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = CitiesAdapter(cities, this)
        citiesRv.adapter = adapter
        citiesRv.layoutManager = LinearLayoutManager(citiesRv.context, RecyclerView.HORIZONTAL,false)
        citiesRv.addItemDecoration(MarginItemDecorator(citiesRv.context.resources.getDimension(R.dimen.rv_item_decorator_16).toInt(), false))

        selectedCity?.let {
            val city: City? = cities.firstOrNull{it.name == selectedCity!!.name}
            city?.let {
                selectedCityIndex = cities.indexOf(it)
                adapter?.setSelectedItem(selectedCityIndex)
            }
        }
    }

    override fun itemClicked(position: Int) {
        selectedCityIndex = position
        adapter?.setSelectedItem(selectedCityIndex)

        handler?.cityClicked(cities[selectedCityIndex])

        dialog.dismiss()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val metrics = resources.displayMetrics
        val screenWidth = metrics.widthPixels

        dialog.window?.setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    interface Handler {
        fun cityClicked(selectedCity: City)
    }
}
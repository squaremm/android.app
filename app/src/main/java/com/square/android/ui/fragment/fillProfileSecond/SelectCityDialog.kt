package com.square.android.ui.fragment.fillProfileSecond

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.R
import android.graphics.Color
import android.view.View
import kotlinx.android.synthetic.main.dialog_select_city.view.*

class SelectCityDialog(private val context: Context, private val cities: List<String>, private val onAction: (city: String) -> Unit): SelectCityAdapter.Handler {

    private var adapter: SelectCityAdapter? = null

    private var view: View? = null

    private var dialog: MaterialDialog? = null

    @SuppressLint("InflateParams")
    fun show(citySelected: String?) {

        val inflater = LayoutInflater.from(context)
        view = inflater.inflate(R.layout.dialog_select_city, null, false)

        dialog = MaterialDialog.Builder(context)
                .customView(view!!, false)
                .cancelable(true)
                .build()

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        adapter = SelectCityAdapter(cities, this)
        view!!.selectCityRv.adapter = adapter

        citySelected?.let {
            if(it.trim().isNotEmpty()){
                adapter?.setSelectedItem(cities.indexOf(it))
            }
        }

        dialog?.show()
    }

    override fun itemClicked(position: Int) {
        adapter?.setSelectedItem(position)

        dialog?.cancel()
        onAction.invoke(cities[position])
    }

}
package com.square.android.ui.fragment.places

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.square.android.R
import com.square.android.data.pojo.Filter
import kotlinx.android.synthetic.main.filters_dialog.view.*

class FiltersDialog(private val context: Context, types: MutableList<String>, private val onAction: (selectedTypes: List<String>) -> Unit) : FiltersAdapter.Handler, FiltersAdapter.HandlerClear {

    private var filtersAdapter: FiltersAdapter? = null

    private var filters: MutableList<Filter> = mutableListOf()

    private var filtersClear: TextView? = null

    init {
        for(type in types){
            filters.add(Filter().apply {text = type; activated = false})
        }

        filtersAdapter = FiltersAdapter(filters, this, this)
    }

    @SuppressLint("InflateParams")
    fun show(filteredTypes: List<String>) {

        if(filteredTypes.isEmpty()){
            for(filter in filters){
                filter.activated = false
            }
        }

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.filters_dialog, null, false)

        val dialog = MaterialDialog.Builder(context)
                .customView(view, false)
                .cancelable(true)
                .build()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        filtersClear = view.filtersClear

        filtersClear?.visibility = if(filters.filter { it.activated }.isNotEmpty()){
            View.VISIBLE
        } else{
            View.GONE
        }

        filtersClear!!.setOnClickListener {
            for(x in 0 until filters.size){
                filtersAdapter?.deactivate(x)
            }

            filtersClear?.visibility = View.GONE
        }

        view.filtersSave.setOnClickListener {
            dialog.dismiss()
            onAction.invoke(filters.filter {it.activated}.map{it.text}.toList())
        }

        view.filtersRv.layoutManager = GridLayoutManager(context, 3)
        view.filtersRv.adapter = filtersAdapter
        view.filtersRv.addItemDecoration(GridItemDecoration(3,view.filtersRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), false))

        dialog.show()
    }

    override fun itemClicked(position: Int) {
        filtersAdapter?.changeActivated(position)
    }

    override fun bindDone() {
        filtersClear?.visibility = if(filters.filter { it.activated }.isNotEmpty()){
            View.VISIBLE
        } else{
            View.GONE
        }
    }
}
package com.square.android.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.square.android.R
import kotlinx.android.extensions.LayoutContainer

private const val TYPE_EMPTY = R.layout.empty_card

abstract class BaseAdapter<T, V : BaseAdapter.BaseHolder<T>>(protected val data: List<T>)
    : androidx.recyclerview.widget.RecyclerView.Adapter<V>() {

    abstract fun getLayoutId(viewType: Int): Int

    abstract fun instantiateHolder(view: View): V

    open fun bindHolder(holder: V, position: Int) {
        holder.bind(data[position])
    }

    open fun bindHolder(holder: V, position: Int, payloads: MutableList<Any>) {
        bindHolder(holder, position)
    }

    open fun getViewType(position: Int): Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): V {
        val inflater = LayoutInflater.from(parent.context)

        val view = inflater.inflate(getLayoutIdInternal(viewType), parent, false)

        return instantiateHolder(view)
    }


    override fun getItemCount() = if (data.isEmpty()) 1 else data.size

    final override fun getItemViewType(position: Int) =
            if (!data.isEmpty()) {
                getViewType(position)
            } else {
                TYPE_EMPTY
            }

    final override fun onBindViewHolder(holder: V, position: Int, payloads: MutableList<Any>) {
        if (!data.isEmpty()) bindHolder(holder, position, payloads)
    }

    final override fun onBindViewHolder(holder: V, position: Int) {
        if (!data.isEmpty()) bindHolder(holder, position)
    }

    abstract class BaseHolder<T>(override val containerView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(containerView), LayoutContainer {
        abstract fun bind(item: T, vararg extras: Any?)
    }

    private fun getLayoutIdInternal(viewType: Int) =
            when (viewType) {
                TYPE_EMPTY -> viewType
                else -> getLayoutId(viewType)
            }
}
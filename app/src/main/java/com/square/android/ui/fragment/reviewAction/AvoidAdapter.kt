package com.square.android.ui.fragment.reviewAction

import android.view.View
import com.square.android.R
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_avoid.*

class AvoidAdapter(data: List<String>) :
        BaseAdapter<String, AvoidAdapter.Holder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.item_avoid

    override fun instantiateHolder(view: View): Holder {
        return Holder(view)
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: Holder, position: Int, payloads: MutableList<Any>) {
        onBindViewHolder(holder, position)
    }

    override fun bindHolder(holder: Holder, position: Int) {
        holder.bind(data[position])
    }

    class Holder(view: View) : BaseAdapter.BaseHolder<String>(view) {
        override fun bind(item: String, vararg extras: Any?) {

            itemAvoidText.text = item
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }
}
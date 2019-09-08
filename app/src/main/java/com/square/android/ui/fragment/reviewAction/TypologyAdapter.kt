package com.square.android.ui.fragment.reviewAction

import android.view.View
import com.square.android.R
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_picture_typology.*

class PictureTypology(val title: String, val value: String)

class TypologyAdapter(data: List<PictureTypology>) :
        BaseAdapter<PictureTypology, TypologyAdapter.Holder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.item_picture_typology

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

    class Holder(view: View) : BaseAdapter.BaseHolder<PictureTypology>(view) {
        override fun bind(item: PictureTypology, vararg extras: Any?) {
            itemTypologyTitle.text = item.title
            itemTypologyValue.text = item.value
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }
}
package com.square.android.ui.activity.place

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.PlaceExtra
import com.square.android.extensions.loadImageCenterInside
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_requirement.*

class RequirementsAdapter(data: List<PlaceExtra>,
                          private val handler: Handler?) : BaseAdapter<PlaceExtra, RequirementsAdapter.ViewHolder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.item_requirement

    override fun getItemCount() = data.size

    override fun bindHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
            onBindViewHolder(holder, position)
    }

    override fun instantiateHolder(view: View): ViewHolder = ViewHolder(view, handler)

    class ViewHolder(containerView: View,
                     var handler: Handler?) : BaseHolder<PlaceExtra>(containerView) {

        override fun bind(item: PlaceExtra, vararg extras: Any?) {
            requirementImage.loadImageCenterInside(url = item.image, placeholder = android.R.color.white)
            requirementType.text = item.type
            requirementValue.text = item.name

            requirementContainer.setOnClickListener {
                handler?.itemClicked(adapterPosition)
            }
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }

}
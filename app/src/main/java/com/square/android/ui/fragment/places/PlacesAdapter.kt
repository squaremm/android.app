package com.square.android.ui.fragment.places

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.Place
import com.square.android.extensions.asDistance
import com.square.android.extensions.loadFirstOrPlaceholder
import com.square.android.extensions.loadImage
import com.square.android.extensions.setTextCarryingEmpty
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.place_card.*

class PlacesAdapter(data: List<Place>,
                    private val handler: Handler) : BaseAdapter<Place, PlacesAdapter.PlacesHolder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.place_card

    override fun getItemCount() = data.size

    override fun instantiateHolder(view: View): PlacesHolder = PlacesHolder(view, handler)

    fun updateDistances() {
        notifyItemRangeChanged(0, data.size, DistancePayload)
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: PlacesHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }

        val item = data[position]

        payloads.filter { it is DistancePayload }
                .forEach { holder.bindDistance(item) }
    }

    class PlacesHolder(containerView: View,
                       handler: Handler) : BaseHolder<Place>(containerView) {

        init {
            containerView.setOnClickListener { handler.itemClicked(adapterPosition) }
        }

        override fun bind(item: Place, vararg extras: Any?) {
            placeInfoAddress.text = item.address
            placeInfoCredits.text = item.credits.toString()

            placeInfoImage.loadFirstOrPlaceholder(item.photos)

            placeInfoTitle.text = item.name

            placeInfoCredits.text = item.award.toString()

            bindDistance(item)
        }

        fun bindDistance(item: Place) {
            val distance = item.distance.asDistance()
            placeInfoDistance.setTextCarryingEmpty(distance)
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }
}

object DistancePayload
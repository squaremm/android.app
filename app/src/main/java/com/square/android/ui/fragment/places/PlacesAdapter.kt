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

    override fun instantiateHolder(view: View) = PlacesHolder(view, handler)

    fun updateDistances() {
        notifyItemRangeChanged(0, data.size, DistancePayload)
    }

    override fun bindHolder(holder: PlacesHolder, position: Int) {
        super.bindHolder(holder, position)
        holder.containerView.setOnClickListener { handler.itemClicked(data[position]) }
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

        override fun bind(item: Place, vararg extras: Any?) {
            placeInfoAddress.text = item.address
            if (item.mainImage != null) {
                placeInfoImage.loadImage(item.mainImage!!, R.color.placeholder)
            } else {
                placeInfoImage.loadFirstOrPlaceholder(item.photos)
            }
            placeInfoTitle.text =  placeInfoTitle.context.getString(R.string.place_name_comma, item.name)

            if(item.availableOfferSpots > 0){
                placeAvailabilityLabel.visibility = View.VISIBLE

                placeAvailabilityDay.text = item.availableOfferDay

                if(item.availableOfferSpots == 1){
                    placeAvailabilityText.text = placeAvailabilityText.context.getString(R.string.place_availability_one_format, item.type, item.availableOfferSpots)
                } else{
                    placeAvailabilityText.text = placeAvailabilityText.context.getString(R.string.place_availability_format, item.type, item.availableOfferSpots)
                }
            }

            bindDistance(item)
        }

        fun bindDistance(item: Place) {
            val distance = item.distance.asDistance()
            placeInfoDistance.setTextCarryingEmpty(distance)
        }
    }

    interface Handler {
        fun itemClicked(place: Place)
    }
}

object DistancePayload
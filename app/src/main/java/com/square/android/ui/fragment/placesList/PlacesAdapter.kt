package com.square.android.ui.fragment.placesList

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.square.android.R
import com.square.android.data.pojo.Place
import com.square.android.extensions.asDistance
import com.square.android.extensions.loadFirstOrPlaceholder
import com.square.android.extensions.loadImage
import com.square.android.extensions.setTextCarryingEmpty
import com.square.android.ui.base.BaseAdapter
import com.square.android.ui.fragment.map.MarginItemDecorator
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

            placeInfoTitle.text = item.name

            placeAvailableValue.text = if(item.availableOfferSpots > 0) item.availableOfferSpots.toString() else placeAvailableValue.context.getString(R.string.no)

            item.icons?.let {
                placeExtrasRv.visibility = View.VISIBLE
                placeExtrasRv.adapter = PlaceExtrasAdapter(it.extras)
                placeExtrasRv.layoutManager = LinearLayoutManager(placeExtrasRv.context, RecyclerView.HORIZONTAL,false)
                placeExtrasRv.addItemDecoration(MarginItemDecorator(placeExtrasRv.context.resources.getDimension(R.dimen.rv_item_decorator_minus_1).toInt(), false))
            }

            bindDistance(item)
        }

        fun bindDistance(item: Place) {
            val distance = item.distance.asDistance()
            placeDistance.setTextCarryingEmpty(distance)
        }
    }

    interface Handler {
        fun itemClicked(place: Place)
    }
}

object DistancePayload
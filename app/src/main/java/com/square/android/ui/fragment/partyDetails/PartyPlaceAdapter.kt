package com.square.android.ui.fragment.partyDetails

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import com.square.android.R
import com.square.android.data.pojo.Place
import com.square.android.extensions.loadImage
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_party_dinner.*
import org.jetbrains.anko.dimen

class PartyPlaceAdapter(data: List<Place>,
                           private val handler: Handler?) : BaseAdapter<Place, PartyPlaceAdapter.ViewHolder>(data) {

    var selectedItemPosition: Int? = null

    override fun getLayoutId(viewType: Int) = R.layout.item_party_dinner

    override fun getItemCount() = data.size

    override fun bindHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position], selectedItemPosition)
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }

        payloads.filter { it is SelectedPayload }
                .forEach { holder.bindSelected(data[position], selectedItemPosition) }
    }

    fun setSelectedItem(position: Int?) {
        if (position == null) return

        val previousPosition = selectedItemPosition
        selectedItemPosition = position

        previousPosition?.let { notifyItemChanged(it, SelectedPayload) }

        notifyItemChanged(position)
    }

    override fun instantiateHolder(view: View): ViewHolder = ViewHolder(view, handler)

    class ViewHolder(containerView: View,
                     var handler: Handler?) : BaseHolder<Place>(containerView) {

        override fun bind(item: Place, vararg extras: Any? ) {
            val selectedPosition = if(extras[0] == null) null else extras[0] as Int

            bindSelected(item, selectedPosition)

            var enabled = item.slots > 0

            if(!enabled){
                partyDinnerContainer.alpha = 0.3f
            }else{
                partyDinnerContainer.alpha = 1f
            }

            when(item.slots){
                1 -> partyDinnerSpots.text = partyDinnerSpots.context.getString(R.string.spot_one_format, item.slots)
                else -> partyDinnerSpots.text = partyDinnerSpots.context.getString(R.string.spot_format, item.slots)
            }

            partyDinnerName.text = item.name

            item.mainImage?.let {
                partyDinnerImg.loadImage(it , roundedCornersRadiusPx = partyDinnerImg.context.dimen(R.dimen.value_4dp))
            }

            partyDinnerContainer.setOnClickListener {
                if(enabled){
                    handler?.itemClicked(adapterPosition, enabled)
                }
            }
        }

        fun bindSelected(item: Place,selectedPosition: Int?) {
            if(selectedPosition == adapterPosition){
                partyDinnerCheckmark.visibility = View.VISIBLE
                partyDinnerImg.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(partyDinnerImg.context, R.color.nice_pink))
            } else{
                partyDinnerCheckmark.visibility = View.GONE
                partyDinnerImg.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(partyDinnerImg.context, android.R.color.white))
            }
        }
    }

    interface Handler {
        fun itemClicked(position: Int, enabled: Boolean)
    }

    object SelectedPayload
}
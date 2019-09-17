package com.square.android.ui.activity.eventDetails

import android.graphics.drawable.Drawable
import android.view.View
import com.square.android.R
import com.square.android.data.pojo.*
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_event_detail.*

class EventDetailsAdapter(data: List<EventDetail>,
                  private val handler: Handler?, private var dinnerStatus: String) : BaseAdapter<EventDetail, EventDetailsAdapter.ViewHolder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.item_event_detail

    override fun getItemCount() = data.size

    override fun bindHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        onBindViewHolder(holder, position)
    }

    override fun instantiateHolder(view: View): ViewHolder = ViewHolder(view, handler, dinnerStatus)

    fun updateDinnerStatus(status: String){
        dinnerStatus = status

        notifyDataSetChanged()
    }

    //TODO model will be changed later, on API update

    class ViewHolder(containerView: View,
                    var handler: Handler?, var dinnerStatus: String) : BaseHolder<EventDetail>(containerView) {

        override fun bind(item: EventDetail, vararg extras: Any? ) {
            itemEventDetailsIcon.visibility = View.GONE
            itemEventDetailsTransfer.visibility = View.GONE
            itemEventDetailsTransferLl.visibility = View.GONE
            itemEventDetailsName.visibility = View.GONE
            itemEventDetailsSecondary.visibility = View.GONE
            itemEventDetailsContainer.visibility = View.GONE
            itemEventDetailsDisabledLabel.visibility = View.GONE
            itemEventDetailsContainerDetails.visibility = View.GONE
            itemEventDetailsImage.alpha = 1f

            if(item.available){
                itemEventDetailsIcon.visibility = View.VISIBLE
            }

            val disabledLabelText = when(item.type){
                TYPE_DINNER -> itemEventDetailsDisabledLabel.context.getString(R.string.no_dinner_available)
                TYPE_ONE_WAY, TYPE_RETURN_TO -> itemEventDetailsDisabledLabel.context.getString(R.string.no_ride_available)
                else -> ""
            }

            if(item.type != TYPE_PARTY){
                if(item.highlighted){
                    // itemEventDetailsLine.setImageDrawable() to highlighted
                    // itemEventDetailsIcon.imageTintList to highlighted

                } else{
                    // itemEventDetailsLine.setImageDrawable() to normal
                    // itemEventDetailsIcon.imageTintList to normal
                }
            } else{
                if(item.highlighted && item.checkedIn){
                    // itemEventDetailsLine.setImageDrawable() to highlighted
                    // itemEventDetailsIcon.imageTintList to highlighted

                } else{
                    // itemEventDetailsLine.setImageDrawable() to normal
                    // itemEventDetailsIcon.imageTintList to normal
                }
            }

            when(item.type){
                TYPE_ONE_WAY, TYPE_PARTY, TYPE_RETURN_TO, TYPE_DINNER  -> {
                    // itemEventDetailsIcon.setImageDrawable() to circle

                    itemEventDetailsContainer.visibility = View.VISIBLE
                    itemEventDetailsName.visibility = View.VISIBLE

                    itemEventDetailsStatus.text = item.status
                    itemEventDetailsTime.text = item.interval
                    itemEventDetailsAddress.text = item.address

                    //TODO set drawables for image
//                    var drawable: Drawable? = when(item.type){
//                        TYPE_ONE_WAY -> itemEventDetailsImage.context.getString(R.drawable.)
//                        TYPE_PARTY -> itemEventDetailsImage.context.getString(R.drawable.)
//                        TYPE_RETURN_TO -> itemEventDetailsImage.context.getString(R.drawable.)
//                        TYPE_DINNER -> itemEventDetailsImage.context.getString(R.drawable.)
//                        else -> null
//                    }
//                    itemEventDetailsImage.setImageDrawable(drawable)

                    itemEventDetailsName.text = when(item.type){
                        TYPE_ONE_WAY -> itemEventDetailsName.context.getString(R.string.one_way_from)
                        TYPE_PARTY -> itemEventDetailsName.context.getString(R.string.party)
                        TYPE_RETURN_TO -> itemEventDetailsName.context.getString(R.string.return_to)
                        TYPE_DINNER -> itemEventDetailsName.context.getString(R.string.dinner)
                        else -> ""
                    }

                    if(item.type == TYPE_PARTY || (item.type == TYPE_DINNER && item.available)){
                        itemEventDetailsSecondary.visibility = View.VISIBLE
                        itemEventDetailsSecondary.text = item.placeName
                    }

                    if(!item.available){
                        itemEventDetailsName.isEnabled = false
                        itemEventDetailsContainer.isEnabled = false

                        if(item.type != TYPE_PARTY){
                            itemEventDetailsImage.alpha = 0.3f
                            itemEventDetailsDisabledLabel.text = disabledLabelText
                            itemEventDetailsDisabledLabel.visibility = View.VISIBLE

                        } else{
                            itemEventDetailsContainerDetails.visibility = View.VISIBLE
                        }
                    } else{
                        itemEventDetailsName.isEnabled = true
                        itemEventDetailsContainer.isEnabled = true

                        itemEventDetailsContainerDetails.visibility = View.VISIBLE

                        if(item.status.toLowerCase() == "not selected"){
                                itemEventDetailsImage.alpha = 0.3f
                                itemEventDetailsStatusLabel.isEnabled = false
                                itemEventDetailsStatus.isEnabled = false
                                itemEventDetailsTime.isEnabled = false
                                itemEventDetailsAddress.isEnabled = false
                        } else{
                                itemEventDetailsStatusLabel.isEnabled = true
                                itemEventDetailsStatus.isEnabled = true
                                itemEventDetailsTime.isEnabled = true
                                itemEventDetailsAddress.isEnabled = true
                        }
                    }

                    if(item.checkedIn){
                        itemEventDetailsContainer.isChecked = true
                    }

                    itemEventDetailsMainContainer.setOnClickListener {
                        if(!item.highlighted){
                            if(item.type != TYPE_PARTY){
                                handler?.itemClicked(adapterPosition)
                            } else{
                                if(!item.checkedIn){
                                    handler?.itemClicked(adapterPosition)
                                }
                            }
                        }
                    }
                }

                TYPE_TRANSFER -> {
                    // itemEventDetailsIcon.setImageDrawable() to square

                    itemEventDetailsTransfer.visibility = View.VISIBLE
                    itemEventDetailsTransferLl.visibility = View.VISIBLE

                    if(item.available){
                        itemEventDetailsArrow.visibility = View.VISIBLE
                        itemEventDetailsNameTo.visibility = View.VISIBLE

                        itemEventDetailsNameFrom.text = item.placeFrom
                        itemEventDetailsNameTo.text = item.placeTo

                        if(dinnerStatus.toLowerCase() == "not selected"){
                            itemEventDetailsTransfer.isEnabled = false
                            itemEventDetailsNameFrom.isEnabled = false
                            itemEventDetailsNameTo.isEnabled = false

                        } else{
                            itemEventDetailsTransfer.isEnabled = true
                            itemEventDetailsNameFrom.isEnabled = true
                            itemEventDetailsNameTo.isEnabled = true
                        }

                    } else{
                        itemEventDetailsTransfer.isEnabled = false
                        itemEventDetailsNameFrom.isEnabled = false
                        itemEventDetailsNameTo.isEnabled = false

                        itemEventDetailsArrow.visibility = View.GONE
                        itemEventDetailsNameTo.visibility = View.GONE

                        itemEventDetailsNameFrom.text = "-"
                    }
                }

            }
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }

    object SelectedPayload
}
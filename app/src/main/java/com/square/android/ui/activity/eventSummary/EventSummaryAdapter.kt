package com.square.android.ui.activity.eventSummary

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.*
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_event_summary.*

class EventSummaryAdapter(data: List<EventSummary>,
                          private val handler: Handler?, private var dinnerStatus: String) : BaseAdapter<EventSummary, EventSummaryAdapter.ViewHolder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.item_event_summary

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
                    var handler: Handler?, var dinnerStatus: String) : BaseHolder<EventSummary>(containerView) {

        override fun bind(item: EventSummary, vararg extras: Any? ) {
            itemEventSummaryIcon.visibility = View.GONE
            itemEventSummaryTransfer.visibility = View.GONE
            itemEventSummaryTransferLl.visibility = View.GONE
            itemEventSummaryName.visibility = View.GONE
            itemEventSummarySecondary.visibility = View.GONE
            itemEventSummaryContainer.visibility = View.GONE
            itemEventSummaryDisabledLabel.visibility = View.GONE
            itemEventSummaryContainerDetails.visibility = View.GONE
            itemEventSummaryImage.alpha = 1f

            if(item.available){
                itemEventSummaryIcon.visibility = View.VISIBLE
            }

            val disabledLabelText = when(item.type){
                TYPE_DINNER -> itemEventSummaryDisabledLabel.context.getString(R.string.no_dinner_available)
                TYPE_ONE_WAY, TYPE_RETURN_TO -> itemEventSummaryDisabledLabel.context.getString(R.string.no_ride_available)
                else -> ""
            }

            if(item.type != TYPE_PARTY){
                if(item.highlighted){
                    // itemEventSummaryLine.setImageDrawable() to highlighted
                    // itemEventSummaryIcon.imageTintList to highlighted

                } else{
                    // itemEventSummaryLine.setImageDrawable() to normal
                    // itemEventSummaryIcon.imageTintList to normal
                }
            } else{
                if(item.highlighted && item.checkedIn){
                    // itemEventSummaryLine.setImageDrawable() to highlighted
                    // itemEventSummaryIcon.imageTintList to highlighted

                } else{
                    // itemEventSummaryLine.setImageDrawable() to normal
                    // itemEventSummaryIcon.imageTintList to normal
                }
            }

            when(item.type){
                TYPE_ONE_WAY, TYPE_PARTY, TYPE_RETURN_TO, TYPE_DINNER  -> {
                    // itemEventSummaryIcon.setImageDrawable() to circle

                    itemEventSummaryContainer.visibility = View.VISIBLE
                    itemEventSummaryName.visibility = View.VISIBLE

                    itemEventSummaryStatus.text = item.status
                    itemEventSummaryTime.text = item.interval
                    itemEventSummaryAddress.text = item.address

                    //TODO set drawables for image
//                    var drawable: Drawable? = when(item.type){
//                        TYPE_ONE_WAY -> itemEventSummaryImage.context.getString(R.drawable.)
//                        TYPE_PARTY -> itemEventSummaryImage.context.getString(R.drawable.)
//                        TYPE_RETURN_TO -> itemEventSummaryImage.context.getString(R.drawable.)
//                        TYPE_DINNER -> itemEventSummaryImage.context.getString(R.drawable.)
//                        else -> null
//                    }
//                    itemEventSummaryImage.setImageDrawable(drawable)

                    itemEventSummaryName.text = when(item.type){
                        TYPE_ONE_WAY -> itemEventSummaryName.context.getString(R.string.one_way_from)
                        TYPE_PARTY -> itemEventSummaryName.context.getString(R.string.party)
                        TYPE_RETURN_TO -> itemEventSummaryName.context.getString(R.string.return_to)
                        TYPE_DINNER -> itemEventSummaryName.context.getString(R.string.dinner)
                        else -> ""
                    }

                    if(item.type == TYPE_PARTY || (item.type == TYPE_DINNER && item.available)){
                        itemEventSummarySecondary.visibility = View.VISIBLE
                        itemEventSummarySecondary.text = item.placeName
                    }

                    if(!item.available){
                        itemEventSummaryName.isEnabled = false
                        itemEventSummaryContainer.isEnabled = false

                        if(item.type != TYPE_PARTY){
                            itemEventSummaryImage.alpha = 0.3f
                            itemEventSummaryDisabledLabel.text = disabledLabelText
                            itemEventSummaryDisabledLabel.visibility = View.VISIBLE

                        } else{
                            itemEventSummaryContainerDetails.visibility = View.VISIBLE
                        }
                    } else{
                        itemEventSummaryName.isEnabled = true
                        itemEventSummaryContainer.isEnabled = true

                        itemEventSummaryContainerDetails.visibility = View.VISIBLE

                        if(item.status.toLowerCase() == "not selected"){
                                itemEventSummaryImage.alpha = 0.3f
                                itemEventSummaryStatusLabel.isEnabled = false
                                itemEventSummaryStatus.isEnabled = false
                                itemEventSummaryTime.isEnabled = false
                                itemEventSummaryAddress.isEnabled = false
                        } else{
                                itemEventSummaryStatusLabel.isEnabled = true
                                itemEventSummaryStatus.isEnabled = true
                                itemEventSummaryTime.isEnabled = true
                                itemEventSummaryAddress.isEnabled = true
                        }
                    }

                    if(item.checkedIn){
                        itemEventSummaryContainer.isChecked = true
                    }

                    itemEventSummaryMainContainer.setOnClickListener {
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
                    // itemEventSummaryIcon.setImageDrawable() to square

                    itemEventSummaryTransfer.visibility = View.VISIBLE
                    itemEventSummaryTransferLl.visibility = View.VISIBLE

                    if(item.available){
                        itemEventSummaryArrow.visibility = View.VISIBLE
                        itemEventSummaryNameTo.visibility = View.VISIBLE

                        itemEventSummaryNameFrom.text = item.placeFrom
                        itemEventSummaryNameTo.text = item.placeTo

                        if(dinnerStatus.toLowerCase() == "not selected"){
                            itemEventSummaryTransfer.isEnabled = false
                            itemEventSummaryNameFrom.isEnabled = false
                            itemEventSummaryNameTo.isEnabled = false

                        } else{
                            itemEventSummaryTransfer.isEnabled = true
                            itemEventSummaryNameFrom.isEnabled = true
                            itemEventSummaryNameTo.isEnabled = true
                        }

                    } else{
                        itemEventSummaryTransfer.isEnabled = false
                        itemEventSummaryNameFrom.isEnabled = false
                        itemEventSummaryNameTo.isEnabled = false

                        itemEventSummaryArrow.visibility = View.GONE
                        itemEventSummaryNameTo.visibility = View.GONE

                        itemEventSummaryNameFrom.text = "-"
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
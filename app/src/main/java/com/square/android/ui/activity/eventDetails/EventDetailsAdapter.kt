package com.square.android.ui.activity.eventDetails

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.*
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_event_detail.*

class EventDetailsAdapter(data: List<EventDetail>,
                  private val handler: Handler?) : BaseAdapter<EventDetail, EventDetailsAdapter.ViewHolder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.item_event_detail

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
                    handler: Handler?) : BaseHolder<EventDetail>(containerView) {

        init {
            containerView.setOnClickListener { handler?.itemClicked(adapterPosition) }
        }

        override fun bind(item: EventDetail, vararg extras: Any? ) {
            itemEventDetailsIcon.visibility = View.GONE
            itemEventDetailsTransfer.visibility = View.GONE
            itemEventDetailsTransferLl.visibility = View.GONE
            itemEventDetailsName.visibility = View.GONE
            itemEventDetailsSecondary.visibility = View.GONE
            itemEventDetailsContainer.visibility = View.GONE
            itemEventDetailsNoDinnerLabel.visibility = View.GONE
            itemEventDetailsContainerDetails.visibility = View.GONE

            when(item.type){
                TYPE_ONE_WAY -> {
                    itemEventDetailsContainer.visibility = View.VISIBLE
                    itemEventDetailsName.visibility = View.VISIBLE
                    itemEventDetailsSecondary.visibility = View.VISIBLE

                    //TODO ...
                }

                TYPE_DINNER -> {

                }

                TYPE_TRANSFER -> {

                }

                TYPE_PARTY -> {

                }

                TYPE_RETURN_TO -> {

                }
            }

        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }

    object SelectedPayload
}
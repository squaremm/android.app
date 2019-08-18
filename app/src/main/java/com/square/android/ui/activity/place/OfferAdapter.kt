package com.square.android.ui.activity.place

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.OfferInfo
import com.square.android.extensions.loadImage
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_offer.*
import org.jetbrains.anko.dimen

class OfferAdapter(data: List<OfferInfo>,
                   private val handler: Handler?) : BaseAdapter<OfferInfo, OfferAdapter.OfferHolder>(data) {
    private var selectedItemPosition: Int? = null

    override fun getLayoutId(viewType: Int) = R.layout.item_offer

    override fun getItemCount() = data.size

    override fun bindHolder(holder: OfferHolder, position: Int) {
        holder.bind(data[position], selectedItemPosition)
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: OfferHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }

        payloads.filter { it is SelectedPayload }
                .forEach { holder.bindSelected(selectedItemPosition) }
    }

    fun setSelectedItem(position: Int?) {
        if (position == null) return
        selectedItemPosition = position
    }

    override fun instantiateHolder(view: View): OfferHolder = OfferHolder(view, handler)

    class OfferHolder(containerView: View,
                      handler: Handler?) : BaseHolder<OfferInfo>(containerView) {

        init {
            containerView.setOnClickListener { handler?.itemClicked(adapterPosition) }
        }

        override fun bind(item: OfferInfo, vararg extras: Any?) {
            val selectedPosition = extras.first() as Int?

            itemOfferName.text = item.name
            itemOfferCredits.text =  itemOfferCredits.context.getString(R.string.credits_format_lowercase,item.price)


            println("CECECE: mainimage:"+item.mainImage+" photo:"+ item.photo)

            itemOfferImv.loadImage((item.mainImage ?: item.photo) ?: "", roundedCornersRadiusPx = itemOfferImv.context.dimen(R.dimen.value_4dp))

            //TODO what to do with timeframes and itemOfferContainer alpha?
//            if (item.timeframes.isNullOrEmpty()){

//                offerTimeframesRv.visibility = View.GONE
//            } else {
//                offerTimeframesRv.visibility = View.VISIBLE
//
//                offerTimeframesRv.adapter = TimeframeAdapter(item.timeframes!!)
//                offerTimeframesRv.layoutManager = LinearLayoutManager(offerTimeframesRv.context, RecyclerView.HORIZONTAL,false)
//                offerTimeframesRv.addItemDecoration(MarginItemDecorator(offerTimeframesRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), false))
//            }
        }

        fun bindSelected(selectedPosition: Int?) {
            val isActive = selectedPosition == adapterPosition

            itemOfferContainer.isActivated = isActive
        }

    }

    interface Handler {
        fun itemClicked(position: Int)
    }

    object SelectedPayload
}
package com.square.android.ui.activity.place

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.OfferInfo
import com.square.android.extensions.loadImage
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_offer.*
import org.jetbrains.anko.dimen

class OfferAdapter(data: List<OfferInfo>,
                   private val handler: Handler?, private val hideCredits: Boolean = false) : BaseAdapter<OfferInfo, OfferAdapter.OfferHolder>(data) {

    private var offers: List<Long>? = null

    override fun getLayoutId(viewType: Int) = R.layout.item_offer

    override fun getItemCount() = data.size

    override fun bindHolder(holder: OfferHolder, position: Int) {
        holder.bind(data[position], offers)
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: OfferHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }

        payloads.filter { it is AlphaPayload }
                .forEach { holder.bindAlpha(data[position],offers) }
    }

    fun updateAlpha(offers: List<Long>?) {
        this.offers = offers

        notifyItemRangeChanged(0,data.size, AlphaPayload)
    }

    override fun instantiateHolder(view: View): OfferHolder = OfferHolder(view, handler, hideCredits)

    class OfferHolder(containerView: View,
                      handler: Handler?, var hideCredits: Boolean = false) : BaseHolder<OfferInfo>(containerView) {

        init {
            containerView.setOnClickListener { handler?.itemClicked(adapterPosition) }
        }

        override fun bind(item: OfferInfo, vararg extras: Any?) {
            val offers = extras.first() as List<Long>?

            itemOfferName.text = item.name

            if(hideCredits){
                itemOfferCredits.visibility = View.GONE
            } else{
                itemOfferCredits.text = itemOfferCredits.context.getString(R.string.credits_format_lowercase, item.price)
            }

            itemOfferImv.loadImage((item.mainImage ?: item.photo)
                    ?: "", roundedCornersRadiusPx = itemOfferImv.context.dimen(R.dimen.value_4dp))

            bindAlpha(item, offers)
        }

        fun bindAlpha(item: OfferInfo, offers: List<Long>?) {
            if (!offers.isNullOrEmpty()) {
                if (!offers.contains(item.id)) {
                    itemOfferContainer.alpha = 0.3f
                    return
                }
            }

            itemOfferContainer.alpha = 1f
        }

    }

    interface Handler {
        fun itemClicked(position: Int)
    }

    object AlphaPayload
}
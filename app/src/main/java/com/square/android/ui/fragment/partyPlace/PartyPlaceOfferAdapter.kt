package com.square.android.ui.fragment.partyPlace

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.OfferInfo
import com.square.android.extensions.loadImage
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_offer.*
import org.jetbrains.anko.dimen

class PartyPlaceOfferAdapter(data: List<OfferInfo>,
                   private val handler: Handler?) : BaseAdapter<OfferInfo, PartyPlaceOfferAdapter.OfferHolder>(data) {

    private var selectedList: MutableList<Int> = mutableListOf()

    var clickedItemPosition: Int? = null

    override fun getLayoutId(viewType: Int) = R.layout.item_offer

    override fun getItemCount() = data.size

    override fun bindHolder(holder: OfferHolder, position: Int) {
        holder.bind(data[position], clickedItemPosition)
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: OfferHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }

        payloads.filter { it is ClickedPayload }
                .forEach { holder.bindAlpha(clickedItemPosition) }

        onBindViewHolder(holder, position)
        return
    }

    fun itemClicked(position: Int) {
        clickedItemPosition = position

        notifyItemChanged(clickedItemPosition!!, ClickedPayload)
    }

    override fun instantiateHolder(view: View): OfferHolder = OfferHolder(view, handler, selectedList)

    class OfferHolder(containerView: View,
                      handler: Handler?,var selectedList: MutableList<Int>) : BaseHolder<OfferInfo>(containerView) {

        init {
            containerView.setOnLongClickListener {
                handler?.itemLongClicked(adapterPosition)
                true
            }

            containerView.setOnClickListener {
                if(adapterPosition in selectedList){
                    selectedList.remove(adapterPosition)
                } else{
                    selectedList.add(adapterPosition)
                }

                handler?.itemClicked(adapterPosition)
            }
        }

        override fun bind(item: OfferInfo, vararg extras: Any?) {
            val selectedPosition = if(extras[0] == null) null else extras[0] as Int

            itemOfferName.text = item.name

            itemOfferCredits.text = itemOfferCredits.context.getString(R.string.credits_format_lowercase, item.price)

            itemOfferImv.loadImage((item.mainImage ?: item.photo)
                    ?: "", roundedCornersRadiusPx = itemOfferImv.context.dimen(R.dimen.value_4dp))

            bindAlpha(selectedPosition)
        }

        fun bindAlpha(position: Int?) {
            position?.let {
                itemOfferContainer.alpha = if (it in selectedList) 1f else 0.3f
            }
        }
    }

    interface Handler {
        fun itemLongClicked(position: Int)

        fun itemClicked(position: Int)
    }

    object ClickedPayload

}
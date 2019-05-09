package com.square.android.ui.fragment.offer

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.OfferInfo
import com.square.android.data.pojo.TYPE_INSTAGRAM_POST
import com.square.android.data.pojo.TYPE_INSTAGRAM_STORY
import com.square.android.extensions.loadImage
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.offer_card.*


class OfferAdapter(data: List<OfferInfo>,
                   private val handler: Handler?) : BaseAdapter<OfferInfo, OfferAdapter.OfferHolder>(data) {
    private var selectedItemPosition: Int? = null

    override fun getLayoutId(viewType: Int) = R.layout.offer_card

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

//        val previousPosition = selectedItemPosition
        selectedItemPosition = position

//        previousPosition?.let { notifyItemChanged(it, SelectedPayload) }
//
//        notifyItemChanged(position)
    }

    override fun instantiateHolder(view: View): OfferHolder = OfferHolder(view, handler)

    class OfferHolder(containerView: View,
                      handler: Handler?) : BaseHolder<OfferInfo>(containerView) {

        init {
            containerView.setOnClickListener { handler?.itemClicked(adapterPosition) }
        }

        override fun bind(item: OfferInfo, vararg extras: Any?) {
            val selectedPosition = extras.first() as Int?

            bindSelected(selectedPosition)

            offerTitle.text = item.name
            offerPrice.text = item.price.toString()

//            offerComponents.text = item.compositionAsList()
//            val instagramPostCredits = item.credits[TYPE_INSTAGRAM_POST] ?: 0
//            val instagramStoryCredits = item.credits[TYPE_INSTAGRAM_STORY] ?: 0
//            offerStory.text = instagramStoryCredits.toString()
//            offerPost.text = instagramPostCredits.toString()

            offerImage.loadImage(item.photo)
        }

        fun bindSelected(selectedPosition: Int?) {
            val isActive = selectedPosition == adapterPosition

            offerContainer.isActivated = isActive
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }

    object SelectedPayload
}
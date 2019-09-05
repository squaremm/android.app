package com.square.android.ui.fragment.review

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.*
import com.square.android.extensions.loadImageInside
import com.square.android.extensions.makeBlackWhite
import com.square.android.extensions.removeFilters
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_review.*

class ReviewAdapter(data: List<Offer.Action>, private val handler: Handler?) :
        BaseAdapter<Offer.Action, ReviewAdapter.ReviewHolder>(data) {

    var selectedItems: MutableList<Int> = mutableListOf()

    override fun getLayoutId(viewType: Int) = R.layout.item_review

    override fun instantiateHolder(view: View): ReviewHolder {
        return ReviewHolder(view, handler)
    }

    //TODO fire when action attempts >= maxAttempts
    //TODO in ClaimedActions, actions will be sent individually, not in a list
    // for ClaimedActions
    fun disableAction(position: Int) {
        data[position].enabled = false

        notifyItemChanged(position, ReviewStatePayload)
    }

    //TODO fire when item added to filled actions to send / deleted from filled actions to send
    //TODO in ReviewFragment, actions will be sent in a list, all at once. Then fragment will be closed
    // for ReviewFragment
    fun changeSelection(position: Int) {
        if(position in selectedItems){
            selectedItems.remove(position)
        } else{
            selectedItems.add(position)
        }

        notifyItemChanged(position, SelectedPayload)
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: ReviewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }

        payloads.forEach {
            when (it) {
                is ReviewStatePayload -> holder.bindEnableState(data[position])
                is SelectedPayload -> holder.bindSelected(selectedItems)
            }
        }
    }

    override fun bindHolder(holder: ReviewHolder, position: Int) {
        holder.bind(data[position])
    }

    class ReviewHolder(view: View,
                       private val handler: Handler?) : BaseAdapter.BaseHolder<Offer.Action>(view) {
        override fun bind(item: Offer.Action, vararg extras: Any?) {

            var d = when(item.type){
                //TODO there will be more types - facebook review, facebook story etc
                TYPE_FACEBOOK_POST -> R.drawable.facebook_logo
                TYPE_INSTAGRAM_POST, TYPE_INSTAGRAM_STORY -> R.drawable.instagram_logo
                TYPE_TRIP_ADVISOR -> R.drawable.trip_advisor_logo
                TYPE_GOOGLE_PLACES -> R.drawable.google_logo
                TYPE_YELP -> R.drawable.yelp_logo

                //TODO update this drawable
                TYPE_PICTURE -> R.drawable.add_photo
                else -> null
            }

            d?.let {
                reviewItemLogo.loadImageInside(it)
            }

            reviewItemTitle.text = item.displayName
            reviewItemCoins.text = "+${item.credits}"

            bindEnableState(item)
        }

        fun bindEnableState(item: Offer.Action) {
            if (item.enabled) {
                bindEnabled()

                containerView.setOnClickListener { handler?.itemClicked(adapterPosition) }
            } else {
                containerView.setOnClickListener(null)

                bindDisabled()
            }
        }

        private fun bindEnabled() {
            reviewItemLogo.removeFilters()

            reviewItemTitle.isChecked = true
            reviewItemCoins.isChecked = true
            reviewItemCreditsLabel.isChecked = true
        }

        private fun bindDisabled() {
            reviewItemLogo.makeBlackWhite()

            reviewItemTitle.isChecked = false
            reviewItemCoins.isChecked = false
            reviewItemCreditsLabel.isChecked = false
        }

        fun bindSelected(selectedItems: MutableList<Int>) {
            reviewContainer.isActivated = selectedItems.contains(adapterPosition)
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }
}

object ReviewStatePayload
object SelectedPayload
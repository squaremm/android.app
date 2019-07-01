package com.square.android.ui.fragment.review

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.ReviewType
import com.square.android.extensions.loadImageInside
import com.square.android.extensions.makeBlackWhite
import com.square.android.extensions.removeFilters
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.review_card.*

private const val COINS_DEFAULT = 0

class ReviewAdapter(data: List<ReviewType>,
                    private val credits: Map<String, Int>,
                    private val handler: Handler?) :
        BaseAdapter<ReviewType, ReviewAdapter.ReviewHolder>(data) {

    private var selectedItemPosition: Int? = null

    override fun getLayoutId(viewType: Int) = R.layout.review_card

    override fun instantiateHolder(view: View): ReviewHolder {
        return ReviewHolder(view, handler)
    }

    fun disableReviewType(position: Int) {
        data[position].enabled = false

        notifyItemChanged(position, ReviewStatePayload)
    }

    fun setSelectedItem(position: Int?) {
        if (position == null) return

        val previousPosition = selectedItemPosition
        selectedItemPosition = position

        previousPosition?.let { notifyItemChanged(it, SelectedPayload) }

        notifyItemChanged(position, SelectedPayload)
    }

    fun clearSelection() {
        val previousPosition = selectedItemPosition

        selectedItemPosition = null

        previousPosition?.let { notifyItemChanged(it, SelectedPayload) }
    }

    @Suppress("ForEachParameterNotUsed")
    override fun bindHolder(holder: ReviewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }

        val item = data[position]

        payloads.forEach {
            when (it) {
                is ReviewStatePayload -> holder.bindEnableState(item)
                is SelectedPayload -> holder.bindSelected(selectedItemPosition)
            }
        }
    }

    override fun bindHolder(holder: ReviewHolder, position: Int) {
        val reviewType = data[position]
        val coins = credits[reviewType.key] ?: COINS_DEFAULT

        holder.bind(reviewType, coins)
    }

    class ReviewHolder(view: View,
                       private val handler: Handler?) : BaseAdapter.BaseHolder<ReviewType>(view) {
        override fun bind(item: ReviewType, vararg extras: Any?) {
            val coins = extras[0] as Int

            reviewItemLogo.loadImageInside(item.imageRes)
            reviewItemTitle.setText(item.titleRes)
            reviewItemCoins.text = "+$coins"

            bindEnableState(item)
        }

        fun bindEnableState(item: ReviewType) {
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

//            reviewItemCoins.setTextColorRes(R.color.colorPrimary)
//            reviewItemTitle.setTextColorRes(R.color.primary_text)
//            reviewItemDescription.setTextColorRes(R.color.secondary_text)
        }

        private fun bindDisabled() {
            reviewItemLogo.makeBlackWhite()

            val textColor = R.color.disabled_text_color

////            reviewItemDescription.setTextColorRes(textColor)
//            reviewItemTitle.setTextColorRes(textColor)
//            reviewItemCoins.setTextColorRes(textColor)
        }

        fun bindSelected(selectedPosition: Int?) {
            val isActive = selectedPosition == adapterPosition

            reviewContainer.isActivated = isActive
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }
}

object ReviewStatePayload
object SelectedPayload
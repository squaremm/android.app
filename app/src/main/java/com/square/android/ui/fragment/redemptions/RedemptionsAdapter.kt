package com.square.android.ui.fragment.redemptions

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.RedemptionInfo
import com.square.android.extensions.loadImage
import com.square.android.extensions.makeBlackWhite
import com.square.android.extensions.removeFilters
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.redemption_card_active.*
import kotlinx.android.synthetic.main.redemption_header.*

private const val TYPE_HEADER = R.layout.redemption_header
private const val TYPE_REDEMPTION = R.layout.redemption_card_active
private const val TYPE_CLAIMED_REDEMPTION = R.layout.redemption_card_claimed
private const val TYPE_CLOSED_REDEMPTION = R.layout.redemption_card_closed

class RedemptionsAdapter(data: List<Any>, private val handler: Handler)
    : BaseAdapter<Any, RedemptionsAdapter.RedemptionHolder>(data) {

    override fun getLayoutId(viewType: Int) = viewType

    override fun instantiateHolder(view: View): RedemptionHolder = RedemptionHolder(view, handler)

    override fun getViewType(position: Int): Int {
        val item = data[position]

        return when (item) {
            is RedemptionInfo -> {
                when {
                    item.closed -> TYPE_CLOSED_REDEMPTION
                    item.claimed -> TYPE_CLAIMED_REDEMPTION
                    else -> TYPE_REDEMPTION
                }
            }
            else -> TYPE_HEADER
        }
    }

    fun removeItem(position: Int) {
        notifyItemRemoved(position)
    }


    class RedemptionHolder(containerView: View, handler: Handler) : BaseHolder<Any>(containerView) {
        init {
            redemptionCancel?.setOnClickListener {
                handler.cancelClicked(adapterPosition)
            }

            redemptionClaim?.setOnClickListener {
                handler.claimClicked(adapterPosition)
            }

            containerView.setOnClickListener {
                if (itemViewType == TYPE_CLAIMED_REDEMPTION) {
                    handler.claimedItemClicked(adapterPosition)
                }
            }
        }

        override fun bind(item: Any, vararg extras: Any?) {
            when (item) {
                is RedemptionInfo -> bindRedemption(item)
                is String -> bindHeader(item)
            }
        }

        private fun bindRedemption(redemptionInfo: RedemptionInfo) {
            if (redemptionInfo.closed) {
                redemptionImage.makeBlackWhite()
            } else {
                redemptionImage.removeFilters()
            }

            redemptionTitle.text = redemptionInfo.place.name
            redemptionAddress.text = redemptionInfo.place.address
            redemptionImage.loadImage(redemptionInfo.place.photo)
        }

        private fun bindHeader(header: String) {
            redemptionHeader.text = header
        }
    }

    interface Handler {
        fun claimClicked(position: Int)

        fun cancelClicked(position: Int)

        fun claimedItemClicked(position: Int);
    }
}
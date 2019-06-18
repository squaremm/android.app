package com.square.android.ui.fragment.campaigns

import android.view.View
import androidx.core.content.ContextCompat
import com.square.android.R
import com.square.android.data.pojo.CampaignInfo
import com.square.android.extensions.loadImage
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_campaign.*

class CampaignsAdapter(data: List<CampaignInfo>,
                       private val handler: Handler) : BaseAdapter<CampaignInfo, CampaignsAdapter.JobsHolder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.item_campaign

    override fun getItemCount() = data.size

    override fun instantiateHolder(view: View): JobsHolder = JobsHolder(view, handler)

    class JobsHolder(containerView: View,
                       handler: Handler) : BaseHolder<CampaignInfo>(containerView) {

        init {
            campaignContainer.setOnClickListener {handler.itemClicked(adapterPosition)}
        }

        override fun bind(item: CampaignInfo, vararg extras: Any?) {

            campaignTitle.text = item.title

            when(item.type){
                "gifting" ->{
                    campaignType.text = campaignType.context.getString(R.string.gifting_campaign)
                    campaignType.background = ContextCompat.getDrawable(campaignType.context, R.drawable.round_bg_pink_pinkish)
                }
                "influencer" ->{
                    campaignType.text = campaignType.context.getString(R.string.influencer_campaign)
                    campaignType.background = ContextCompat.getDrawable(campaignType.context, R.drawable.round_bg_purple_purpleish)
                }
            }

            item.mainImage?.let { campaignImage.loadImage(it)}

            campaignEndedLabel.visibility = if(item.hasWinner) View.VISIBLE else View.GONE
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }
}
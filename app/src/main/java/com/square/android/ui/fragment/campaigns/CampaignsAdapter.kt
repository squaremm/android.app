package com.square.android.ui.fragment.campaigns

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.CampaignInfo
import com.square.android.extensions.loadImage
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_campaign.*

class CampaignsAdapter(data: List<CampaignInfo>,
                           private val handler: Handler?) : BaseAdapter<CampaignInfo, CampaignsAdapter.CampaignHolder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.item_campaign

    override fun getItemCount() = data.size

    override fun bindHolder(holder: CampaignHolder, position: Int) {
        holder.bind(data[position])
    }

    //TODO BaseAdapter TYPE_EMPTY layout not showing when data is empty

    override fun instantiateHolder(view: View): CampaignHolder = CampaignHolder(view, handler)

    class CampaignHolder(containerView: View,
                               handler: Handler?) : BaseHolder<CampaignInfo>(containerView) {

        init {
            containerView.setOnClickListener { handler?.itemClicked(adapterPosition) }
        }

        override fun bind(item: CampaignInfo, vararg extras: Any?) {

            //TODO no hashtag, date(start, end), extra icons, logo
            //TODO when to tell if award is included?

            item.mainImage?.let { campaignImage.loadImage(it)}

            if(item.daysToStart > 0 ){
                futureCampaignContainer.visibility = View.VISIBLE
                campaignFName.text = item.title

                //TODO will there be more types?
                when(item.type){
                    "gifting" ->{
                        campaignFType.text = campaignFType.context.resources.getString(R.string.gifting)
                    }
                    "influencer" ->{
                        campaignFType.text = campaignFType.context.resources.getString(R.string.influencer)
                    }
                }
            } else{
                normalCampaignContainer.visibility = View.VISIBLE
                campaignNLogoContainer.visibility = View.VISIBLE
                campaignNName.text = item.title

                //TODO is it proper closed indicator?
                campaignClosedView.visibility = if(item.hasWinner) View.VISIBLE else View.GONE

                //TODO will there be more types?
                when(item.type){
                    "gifting" ->{
                        campaignNType.text = campaignNType.context.resources.getString(R.string.gifting)
                    }
                    "influencer" ->{
                        campaignNType.text = campaignNType.context.resources.getString(R.string.influencer)
                    }
                }
            }
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }

}
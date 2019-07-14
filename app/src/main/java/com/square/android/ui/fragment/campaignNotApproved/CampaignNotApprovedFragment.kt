package com.square.android.ui.fragment.campaignNotApproved

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.presentation.presenter.campaignNotApproved.CampaignNotApprovedPresenter
import com.square.android.presentation.view.campaignNotApproved.CampaignNotApprovedView
import kotlinx.android.synthetic.main.fragment_campaign_not_approved.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.square.android.data.pojo.Campaign
import com.square.android.ui.activity.campaignDetails.EXTRA_CAMPAIGN
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.entries.SquareImagesAdapter
import com.square.android.ui.fragment.map.MarginItemDecorator
import com.square.android.ui.fragment.places.GridItemDecoration
import org.jetbrains.anko.bundleOf

class CampaignNotApprovedFragment: BaseFragment(), CampaignNotApprovedView {

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(campaign: Campaign): CampaignNotApprovedFragment {
            val fragment = CampaignNotApprovedFragment()

            val args = bundleOf(EXTRA_CAMPAIGN to campaign)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: CampaignNotApprovedPresenter

    @ProvidePresenter
    fun providePresenter() = CampaignNotApprovedPresenter(getCampaign())

    private var rewardsAdapter: RewardsAdapter? = null
    private var winnerAdapter: RewardsAdapter? = null
    private var modelTypeAdapter: SquareWrapWidthImagesAdapter? = null
    private var moodboardAdapter: SquareImagesAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_campaign_not_approved, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notApprovedJoinBtn.setOnClickListener { presenter.joinClicked() }
    }

    override fun showData(campaign: Campaign) {
        Log.e("LOL", campaign.toString())
        if(campaign.isParticipant){
            notApprovedJoinBtn.isEnabled = false
            notApprovedJoinBtn.text = getString(R.string.waiting_for_acceptance)
            notApprovedJoinBtn.isAllCaps = false
            notApprovedJoinBtn.setTextColor(ContextCompat.getColor(notApprovedJoinBtn.context, R.color.nice_pink))
        } else if (campaign.hasWinner) {
            notApprovedJoinBtn.isEnabled = false
            notApprovedJoinBtn.text = getString(R.string.ended)
            notApprovedJoinBtn.isAllCaps = false
            notApprovedJoinBtn.setTextColor(Color.WHITE)
            notApprovedJoinBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(notApprovedJoinBtn.context, R.color.black_trans_75))
        } else{
            if(!campaign.isJoinable){
                notApprovedJoinBtn.visibility = View.GONE
            }

            if(TextUtils.isEmpty(campaign.description)){
                cvDescription.visibility = View.GONE
            } else{
                cvDescriptionText.text = campaign.description
            }

            val rewards: List<Campaign.Reward>? = if(campaign.rewards.isNullOrEmpty()) null else campaign.rewards!!.filter { it.isGlobal }

            if(rewards.isNullOrEmpty()){
                cvRewards.visibility = View.GONE
            } else{
                rewards.firstOrNull{ it.type == "credit" }?.let {
                    cvRewardsCredits.text = it.value.toString()

                } ?: run {
                    cvRewardsLl.visibility = View.GONE
                    cvRewardsDivider.visibility = View.GONE
                }

                rewards.filter { it.type != "credit" }.let {
                    if(it.isNullOrEmpty()){
                        cvRewardsDivider.visibility =  View.GONE
                        cvRewardsRv.visibility =  View.GONE
                    } else{
                        rewardsAdapter = RewardsAdapter(it, null)
                        cvRewardsRv.adapter = rewardsAdapter
                        cvRewardsRv.layoutManager = LinearLayoutManager(cvRewardsRv.context, RecyclerView.VERTICAL,false)
                        cvRewardsRv.addItemDecoration(MarginItemDecorator(cvRewardsRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), true))
                    }
                }
            }

            val winnerRewards: List<Campaign.Reward>? = if(campaign.rewards.isNullOrEmpty()) null else campaign.rewards!!.filter {!it.isGlobal}

            if(winnerRewards.isNullOrEmpty()){
                cvWinner.visibility = View.GONE
            } else{

                winnerRewards.firstOrNull{ it.type == "credit" }?.let {
                    cvWinnerCredits.text = it.value.toString()

                } ?: run {
                    cvWinnerLl.visibility = View.GONE
                    cvWinnerDivider.visibility = View.GONE
                }

                winnerRewards.filter { it.type != "credit" }.let {
                    if(it.isNullOrEmpty()){
                        cvWinnerDivider.visibility =  View.GONE
                        cvWinnerRv.visibility =  View.GONE
                    } else{
                        winnerAdapter = RewardsAdapter(it, null, true)
                        cvWinnerRv.adapter = winnerAdapter
                        cvWinnerRv.layoutManager = LinearLayoutManager(cvWinnerRv.context, RecyclerView.VERTICAL,false)
                        cvWinnerRv.addItemDecoration(MarginItemDecorator(cvWinnerRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), true))
                    }
                }
            }

            cvDeadlinesDaysValue1.text = campaign.daysToStart.toString()
            cvDeadlinesDaysValue2.text = campaign.daysToPicture.toString()
            cvDeadlinesDaysValue3.text = campaign.daysToInstagramPicture.toString()

            if(campaign.exampleImages.isNullOrEmpty()){
                cvModel.visibility = View.GONE
            } else{
                modelTypeAdapter = SquareWrapWidthImagesAdapter(campaign.exampleImages?.map { it.url }!!, null)
                cvModelRv.adapter = modelTypeAdapter
                cvModelRv.layoutManager = LinearLayoutManager(cvModelRv.context, RecyclerView.HORIZONTAL,false)
                cvModelRv.addItemDecoration(MarginItemDecorator(cvModelRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), false))
            }

            if(campaign.tasks.isNullOrEmpty()){
                cvTask.visibility = View.GONE
            } else {
                campaign.tasks!!.firstOrNull{ it.type == "photo" }?.let {
                    cvTaskStories.text = if(it.count == 1) it.count.toString() + " "+ getString(R.string.story) else it.count.toString() + " "+ getString(R.string.stories_lowercase)

                } ?: run {
                    cvTaskStoriesLl.visibility = View.GONE
                }

                campaign.tasks!!.firstOrNull{ it.type == "post" }?.let {
                    cvTaskPosts.text = if(it.count == 1) it.count.toString() + " "+ getString(R.string.ig_post) else it.count.toString() + " "+ getString(R.string.ig_posts)

                } ?: run {
                    cvTaskPostsLl.visibility = View.GONE
                }

                val ss = SpannableString(getString(R.string.when_publishing_tag)+" @"+(campaign.title).toLowerCase())
                ss.setSpan(ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.nice_pink)), ss.length - (campaign.title.length +1) , ss.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                cvTaskTag.text = ss
            }

            if(campaign.moodboardImages.isNullOrEmpty()){
                cvMood.visibility = View.GONE
            } else{
                moodboardAdapter = SquareImagesAdapter(campaign.moodboardImages!!.map { it.url }, null)

                cvMoodRv.layoutManager = GridLayoutManager(context!!, 3)
                cvMoodRv.adapter = moodboardAdapter
                cvMoodRv.addItemDecoration(GridItemDecoration(3, cvMoodRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), false))
            }

            //TODO: what should be inside cvHowPager?
            cvHow.visibility = View.GONE
        }

        hideProgress()
    }

    override fun showProgress() {
        svContent.visibility = View.GONE
        notApprovedProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        notApprovedProgress.visibility = View.GONE
        svContent.visibility = View.VISIBLE
    }

    override fun showThanks() {
        notApprovedJoinBtn.visibility = View.GONE
        cvDescription.visibility = View.GONE
        cvRewards.visibility = View.GONE
        cvWinner.visibility = View.GONE
        cvDeadlines.visibility = View.GONE
        cvModel.visibility = View.GONE
        cvTask.visibility = View.GONE
        cvMood.visibility = View.GONE
        cvHow.visibility = View.GONE

        cvThanks.visibility = View.VISIBLE
    }

    private fun getCampaign() = arguments?.getParcelable(EXTRA_CAMPAIGN) as Campaign
}

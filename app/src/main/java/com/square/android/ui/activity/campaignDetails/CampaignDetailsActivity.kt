package com.square.android.ui.activity.campaignDetails

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.Campaign
import com.square.android.presentation.presenter.campaignDetails.CampaignDetailsPresenter
import com.square.android.presentation.view.campaignDetails.CampaignDetailsView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import kotlinx.android.synthetic.main.activity_campaign_details.*
import ru.terrakok.cicerone.Navigator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.square.android.extensions.loadImage
import com.square.android.ui.activity.campaigns.CAMPAIGN_EXTRA_ID
import com.square.android.ui.fragment.entries.SquareImagesAdapter
import com.square.android.ui.fragment.map.MarginItemDecorator
import com.square.android.ui.fragment.places.GridItemDecoration

class CampaignDetailsActivity: BaseActivity(), CampaignDetailsView {

    @InjectPresenter
    lateinit var presenter: CampaignDetailsPresenter

    @ProvidePresenter
    fun providePresenter() = CampaignDetailsPresenter(getCampaignId())

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    private var rewardsAdapter: RewardsAdapter? = null
    private var winnerAdapter: RewardsAdapter? = null
    private var modelTypeAdapter: SquareWrapWidthImagesAdapter? = null
    private var moodboardAdapter: SquareImagesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campaign_details)

        campaignBack.setOnClickListener {onBackPressed()}
        campaignJoinBtn.setOnClickListener {presenter.joinClicked()}
    }

    override fun showData(campaign: Campaign) {

        campaignName.text = campaign.name
        campaign.mainImage?.let {campaignBg.loadImage(it)}

        if(campaign.participated){
            showThanks()

        } else{
            if(TextUtils.isEmpty(campaign.description)){
                cvDescription.visibility = View.GONE
            } else{
                cvDescriptionText.text = campaign.description
            }

            if(campaign.credits <= 0 && campaign.rewards.isNullOrEmpty()){
                cvRewards.visibility = View.GONE
            } else{

                if(campaign.credits <= 0){
                    cvRewardsLl.visibility = View.GONE
                    cvRewardsDivider.visibility = View.GONE
                } else{
                    cvRewardsCredits.text = campaign.credits.toString()
                }

                if(campaign.rewards.isNullOrEmpty()){
                    cvRewardsDivider.visibility =  View.GONE
                    cvRewardsRv.visibility =  View.GONE
                } else{
                    rewardsAdapter = RewardsAdapter(campaign.rewards!!, null)
                    cvRewardsRv.adapter = rewardsAdapter
                    cvRewardsRv.layoutManager = LinearLayoutManager(cvRewardsRv.context, RecyclerView.VERTICAL,false)
                    cvRewardsRv.addItemDecoration(MarginItemDecorator(cvRewardsRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), true))
                }
            }

            if(campaign.winnerRewards.isNullOrEmpty()){
                cvWinner.visibility = View.GONE
            } else{
                winnerAdapter = RewardsAdapter(campaign.winnerRewards!!, null, true)
                cvWinnerRv.adapter = winnerAdapter
                cvWinnerRv.layoutManager = LinearLayoutManager(cvWinnerRv.context, RecyclerView.VERTICAL,false)
                cvWinnerRv.addItemDecoration(MarginItemDecorator(cvWinnerRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), true))
            }

            cvDeadlinesDaysValue1.text = campaign.participateDays.toString()
            cvDeadlinesDaysValue2.text = campaign.uploadPicsDays.toString()
            cvDeadlinesDaysValue3.text = campaign.uploadIgDays.toString()

            if(campaign.modelTypeImages.isNullOrEmpty()){
                cvModel.visibility = View.GONE
            } else{
                modelTypeAdapter = SquareWrapWidthImagesAdapter(campaign.modelTypeImages!!, null)
                cvModelRv.adapter = modelTypeAdapter
                cvModelRv.layoutManager = LinearLayoutManager(cvModelRv.context, RecyclerView.HORIZONTAL,false)
                cvModelRv.addItemDecoration(MarginItemDecorator(cvModelRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), false))
            }

            if(campaign.storiesRequired <= 0 && campaign.postsRequired <= 0){
                cvTask.visibility = View.GONE
            } else{
                if(campaign.storiesRequired <= 0){
                    cvTaskStoriesLl.visibility = View.GONE
                } else {
                    cvTaskStories.text = if(campaign.storiesRequired == 1) campaign.storiesRequired.toString() + " "+ getString(R.string.story) else campaign.storiesRequired.toString() + " "+ getString(R.string.stories_lowercase)
                }

                if(campaign.postsRequired <= 0){
                    cvTaskPostsLl.visibility = View.GONE
                } else {
                    cvTaskPosts.text = if(campaign.postsRequired == 1) campaign.postsRequired.toString() + " "+ getString(R.string.ig_post) else campaign.postsRequired.toString() + " "+ getString(R.string.ig_posts)
                }

                val ss = SpannableString(getString(R.string.when_publishing_tag)+" @"+(campaign.name).toLowerCase())

                ss.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.nice_pink)), ss.length - (campaign.name.length +1) , ss.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                cvTaskTag.text = ss
            }

            if(campaign.moodboardImages.isNullOrEmpty()){
                cvMood.visibility = View.GONE
            } else{
                moodboardAdapter = SquareImagesAdapter(campaign.moodboardImages!!, null)

                cvMoodRv.layoutManager = GridLayoutManager(this, 3)
                cvMoodRv.adapter = moodboardAdapter
                cvMoodRv.addItemDecoration(GridItemDecoration(3,cvMoodRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), false))
            }

            //TODO: what should be inside cvHowPager?
            cvHow.visibility = View.GONE
        }

    }

    override fun showProgress() {
        svContent.visibility = View.GONE
        campaignProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        campaignProgress.visibility = View.GONE
        svContent.visibility = View.VISIBLE
    }

    override fun showThanks() {
        campaignJoinBtn.visibility = View.GONE
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

    private fun getCampaignId() = intent.getLongExtra(CAMPAIGN_EXTRA_ID, 0)
}

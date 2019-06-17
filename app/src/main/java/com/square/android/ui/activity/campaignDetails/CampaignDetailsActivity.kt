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
import com.square.android.data.pojo.OldCampaign
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

    override fun showData(oldCampaign: OldCampaign) {

        campaignName.text = oldCampaign.name
        oldCampaign.mainImage?.let {campaignBg.loadImage(it)}

        if(oldCampaign.participated){
            showThanks()

        } else{
            if(TextUtils.isEmpty(oldCampaign.description)){
                cvDescription.visibility = View.GONE
            } else{
                cvDescriptionText.text = oldCampaign.description
            }

            if(oldCampaign.credits <= 0 && oldCampaign.rewards.isNullOrEmpty()){
                cvRewards.visibility = View.GONE
            } else{

                if(oldCampaign.credits <= 0){
                    cvRewardsLl.visibility = View.GONE
                    cvRewardsDivider.visibility = View.GONE
                } else{
                    cvRewardsCredits.text = oldCampaign.credits.toString()
                }

                if(oldCampaign.rewards.isNullOrEmpty()){
                    cvRewardsDivider.visibility =  View.GONE
                    cvRewardsRv.visibility =  View.GONE
                } else{
                    rewardsAdapter = RewardsAdapter(oldCampaign.rewards!!, null)
                    cvRewardsRv.adapter = rewardsAdapter
                    cvRewardsRv.layoutManager = LinearLayoutManager(cvRewardsRv.context, RecyclerView.VERTICAL,false)
                    cvRewardsRv.addItemDecoration(MarginItemDecorator(cvRewardsRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), true))
                }
            }

            if(oldCampaign.winnerRewards.isNullOrEmpty()){
                cvWinner.visibility = View.GONE
            } else{
                winnerAdapter = RewardsAdapter(oldCampaign.winnerRewards!!, null, true)
                cvWinnerRv.adapter = winnerAdapter
                cvWinnerRv.layoutManager = LinearLayoutManager(cvWinnerRv.context, RecyclerView.VERTICAL,false)
                cvWinnerRv.addItemDecoration(MarginItemDecorator(cvWinnerRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), true))
            }

            cvDeadlinesDaysValue1.text = oldCampaign.participateDays.toString()
            cvDeadlinesDaysValue2.text = oldCampaign.uploadPicsDays.toString()
            cvDeadlinesDaysValue3.text = oldCampaign.uploadIgDays.toString()

            if(oldCampaign.modelTypeImages.isNullOrEmpty()){
                cvModel.visibility = View.GONE
            } else{
                modelTypeAdapter = SquareWrapWidthImagesAdapter(oldCampaign.modelTypeImages!!, null)
                cvModelRv.adapter = modelTypeAdapter
                cvModelRv.layoutManager = LinearLayoutManager(cvModelRv.context, RecyclerView.HORIZONTAL,false)
                cvModelRv.addItemDecoration(MarginItemDecorator(cvModelRv.context.resources.getDimension(R.dimen.rv_item_decorator_8).toInt(), false))
            }

            if(oldCampaign.storiesRequired <= 0 && oldCampaign.postsRequired <= 0){
                cvTask.visibility = View.GONE
            } else{
                if(oldCampaign.storiesRequired <= 0){
                    cvTaskStoriesLl.visibility = View.GONE
                } else {
                    cvTaskStories.text = if(oldCampaign.storiesRequired == 1) oldCampaign.storiesRequired.toString() + " "+ getString(R.string.story) else oldCampaign.storiesRequired.toString() + " "+ getString(R.string.stories_lowercase)
                }

                if(oldCampaign.postsRequired <= 0){
                    cvTaskPostsLl.visibility = View.GONE
                } else {
                    cvTaskPosts.text = if(oldCampaign.postsRequired == 1) oldCampaign.postsRequired.toString() + " "+ getString(R.string.ig_post) else oldCampaign.postsRequired.toString() + " "+ getString(R.string.ig_posts)
                }

                val ss = SpannableString(getString(R.string.when_publishing_tag)+" @"+(oldCampaign.name).toLowerCase())

                ss.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.nice_pink)), ss.length - (oldCampaign.name.length +1) , ss.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                cvTaskTag.text = ss
            }

            if(oldCampaign.moodboardImages.isNullOrEmpty()){
                cvMood.visibility = View.GONE
            } else{
                moodboardAdapter = SquareImagesAdapter(oldCampaign.moodboardImages!!, null)

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

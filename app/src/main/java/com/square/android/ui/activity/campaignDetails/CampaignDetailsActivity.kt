package com.square.android.ui.activity.campaignDetails

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.square.android.data.pojo.ImageAspect
import com.square.android.extensions.loadImage
import com.square.android.ui.activity.campaigns.CAMPAIGN_EXTRA_ID
import com.square.android.ui.fragment.entries.SquareImagesAdapter
import com.square.android.ui.fragment.map.MarginItemDecorator
import com.squareup.picasso.Picasso

class CampaignDetailsActivity: BaseActivity(), CampaignDetailsView {

    @InjectPresenter
    lateinit var presenter: CampaignDetailsPresenter

    @ProvidePresenter
    fun providePresenter() = CampaignDetailsPresenter(getCampaignId())

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    private var rewardsAdapter: RewardsAdapter? = null
    private var winnerAdapter: RewardsAdapter? = null
    private var modelTypeAdapter: SquareImagesAdapter? = null

    private var moodListIndex = -1

    var actualIndex = 0

    var moodboardImages: List<String>? = null

    var loadedImages: MutableList<ImageAspect> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campaign_details)


//        campaignDetailsBack.setOnClickListener {onBackPressed()}
//        campaignDetailsParticipate.setOnClickListener {presenter.participateClicked()}
    }

    override fun showData(campaign: Campaign) {
        campaignProgress.visibility = View.VISIBLE
        svContent.visibility = View.VISIBLE

        campaign.mainImage?.let {campaignBg.loadImage(it)}

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
            modelTypeAdapter = SquareImagesAdapter(campaign.modelTypeImages!!, null)
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

            val ss = SpannableString(getString(R.string.when_publishing_tag)+" @"+campaign.name)

            //TODO test if ok
            ss.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.nice_pink)), ss.length - campaign.name.length , ss.length -1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            cvTaskTag.text = ss
        }

        if(campaign.moodboardImages.isNullOrEmpty()){
            cvMood.visibility = View.GONE
        } else{
            moodboardImages = campaign.moodboardImages

            loadRatio()
        }

        //TODO: what should be inside cvHowPager?
        cvHow.visibility = View.GONE
    }

    val target = object: com.squareup.picasso.Target {

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            loadedImages.add(ImageAspect().apply {
                imageUrl = moodboardImages!![moodListIndex]

                bitmap?.let {
                    aspectType = getAspectType(it)
                } ?: kotlin.run { deleteImage = true }

            })

            loadRatio()
        }

        override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
            loadedImages.add(ImageAspect().apply {
                deleteImage = true
            })

            loadRatio()
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) { }
    }

    private fun getAspectType(bitmap: Bitmap): Int{
        var type = 0

        if(bitmap.width == bitmap.height){
            type = ASPECT_TYPE_SQUARE
        }
        if (bitmap.width > bitmap.height){
            type = ASPECT_TYPE_HORIZONTAL
        }
        if(bitmap.height > bitmap.width){
            type = ASPECT_TYPE_VERTICAL
        }

        return type
    }

    private fun loadRatio(){
        moodListIndex++

        if(moodListIndex < moodboardImages!!.size - 1){
            Picasso.get().load(moodboardImages!![moodListIndex]).into(target)
        } else{
            onRatiosLoaded()
        }
    }

    private fun onRatiosLoaded(){
        loadedImages.removeAll {it.deleteImage}

        if(!loadedImages.isEmpty()){
            assignImagesSize()
        } else{
            cvMood.visibility = View.GONE
        }
    }

    private fun onImagesAssigned(){
        cvMoodProgress.visibility = View.GONE
        cvMoodRv.visibility = View.VISIBLE


        //TODO: create item_aspect_image layout (match parent width and 16:9 aspect ratio)
        //TODO: assign images to MoodboardAdapter
        //TODO: assign adapter to cvMoodRv
        //TODO: assign StaggeredGridLayoutManager with 3 spans, vertical
        //TODO: assign 4dp margin decorator(all sides)

    }

    private fun assignImagesSize(){
        val actual = loadedImages.getOrNull(actualIndex)
        val forward1 = loadedImages.getOrNull(actualIndex+1)
        val forward2 = loadedImages.getOrNull(actualIndex+2)
//        val forward3 = loadedImages.getOrNull(actualIndex+3)

        var imagesLeft = 0

        if(actual == null){}
        else if(forward1 == null){ imagesLeft = 1 }
        else if(forward2 == null){ imagesLeft = 2 }
        else { imagesLeft = 3}

//        else if(forward3 == null){ imagesLeft = 3 }
//        else{ imagesLeft = 4 }

        when(imagesLeft){
            0 ->{
                onImagesAssigned()
            }

            1->{
                when(actual!!.aspectType){
                    ASPECT_TYPE_SQUARE -> actual.arrangeType = TYPE_HALF_FULL
                    ASPECT_TYPE_HORIZONTAL -> actual.arrangeType = TYPE_FULL
                    ASPECT_TYPE_VERTICAL -> actual.arrangeType = TYPE_25_WIDTH_FULL
                }

            }

            2 ->{
                when(actual!!.aspectType){

                    ASPECT_TYPE_SQUARE -> {
                        actual.arrangeType = TYPE_HALF_FULL
                        forward1!!.arrangeType = TYPE_HALF_FULL
                    }

                    ASPECT_TYPE_HORIZONTAL -> {
                        when(forward1!!.aspectType){

                            ASPECT_TYPE_SQUARE ->{
                                actual.arrangeType = TYPE_HALF_FULL
                                forward1.arrangeType = TYPE_HALF_FULL
                            }

                            ASPECT_TYPE_HORIZONTAL ->{
                                actual.arrangeType = TYPE_HALF_FULL
                                forward1.arrangeType = TYPE_HALF_FULL
                            }

                            ASPECT_TYPE_VERTICAL ->{
                                actual.arrangeType = TYPE_75_WIDTH_FULL
                                forward1.arrangeType = TYPE_25_WIDTH_FULL
                            }
                        }
                    }

                    ASPECT_TYPE_VERTICAL -> {
                        when(forward1!!.aspectType){
                            ASPECT_TYPE_SQUARE ->{
                                forward1.arrangeType = TYPE_HALF_FULL
                                actual.arrangeType = TYPE_HALF_FULL
                            }

                            ASPECT_TYPE_HORIZONTAL -> {
                                actual.arrangeType = TYPE_25_WIDTH_FULL
                                forward1.arrangeType = TYPE_75_WIDTH_FULL
                            }

                            ASPECT_TYPE_VERTICAL -> {
                                forward1.arrangeType = TYPE_HALF_FULL
                                actual.arrangeType = TYPE_HALF_FULL
                            }

                        }
                    }
                }
            }

            3 ->{
                var reRun = false

                when(actual!!.aspectType){

                    ASPECT_TYPE_SQUARE -> {
                        actual.arrangeType = TYPE_HALF_FULL

                        if(forward1!!.aspectType == ASPECT_TYPE_HORIZONTAL && forward2!!.aspectType == ASPECT_TYPE_HORIZONTAL){
                            forward1.arrangeType = TYPE_QUARTER
                            forward2.arrangeType = TYPE_QUARTER
                        }
                        else if(forward1!!.aspectType == ASPECT_TYPE_VERTICAL && forward2!!.aspectType == ASPECT_TYPE_VERTICAL){
                            forward1.arrangeType = TYPE_25_WIDTH_FULL
                            forward2.arrangeType = TYPE_25_WIDTH_FULL
                        }
                        else{
                            forward1.arrangeType = TYPE_HALF_FULL

                            actualIndex += 2
                            reRun = true
                        }
                    }

                    ASPECT_TYPE_HORIZONTAL -> {

                        if(forward1!!.aspectType == ASPECT_TYPE_SQUARE || forward2!!.aspectType == ASPECT_TYPE_SQUARE ){
                            actual.arrangeType = TYPE_HALF_FULL
                            forward1.arrangeType = TYPE_HALF_FULL

                            actualIndex += 2
                            reRun = true
                        }
                        else if(forward1!!.aspectType == ASPECT_TYPE_HORIZONTAL && forward2!!.aspectType == ASPECT_TYPE_HORIZONTAL ){
                            actual.arrangeType = TYPE_HALF_FULL

                            forward1.arrangeType = TYPE_QUARTER
                            forward2.arrangeType = TYPE_QUARTER
                        }
                        else if(forward1!!.aspectType == ASPECT_TYPE_VERTICAL && forward2!!.aspectType == ASPECT_TYPE_VERTICAL ){
                            actual.arrangeType = TYPE_HALF_FULL

                            forward1.arrangeType = TYPE_25_WIDTH_FULL
                            forward2.arrangeType = TYPE_25_WIDTH_FULL
                        }
                        else if(forward1!!.aspectType == ASPECT_TYPE_VERTICAL && forward2!!.aspectType == ASPECT_TYPE_HORIZONTAL){
                            actual.arrangeType = TYPE_75_WIDTH_FULL

                            forward1.arrangeType = TYPE_25_WIDTH_FULL

                            actualIndex += 2
                            reRun = true
                        }
                        else if(forward1!!.aspectType == ASPECT_TYPE_HORIZONTAL && forward2!!.aspectType == ASPECT_TYPE_VERTICAL){
                            actual.arrangeType = TYPE_HALF_FULL

                            forward1.arrangeType = TYPE_HALF_FULL

                            actualIndex += 2
                            reRun = true
                        }
                    }

                    ASPECT_TYPE_VERTICAL -> {

                        if(forward1!!.aspectType == ASPECT_TYPE_SQUARE || forward2!!.aspectType == ASPECT_TYPE_SQUARE){
                            actual.arrangeType = TYPE_HALF_FULL
                            forward1.arrangeType = TYPE_HALF_FULL

                            actualIndex += 2
                            reRun = true
                        }

                        else if(forward1!!.aspectType == ASPECT_TYPE_HORIZONTAL && forward2!!.aspectType == ASPECT_TYPE_HORIZONTAL ){
                            actual.arrangeType = TYPE_25_WIDTH_FULL
                            forward1.arrangeType = TYPE_75_WIDTH_FULL

                            actualIndex += 2
                            reRun = true
                        }
                        else if(forward1!!.aspectType == ASPECT_TYPE_VERTICAL && forward2!!.aspectType == ASPECT_TYPE_VERTICAL ){
                            actual.arrangeType = TYPE_HALF_FULL

                            forward1.arrangeType = TYPE_25_WIDTH_FULL
                            forward2.arrangeType = TYPE_25_WIDTH_FULL
                        }
                        else if(forward1!!.aspectType == ASPECT_TYPE_VERTICAL && forward2!!.aspectType == ASPECT_TYPE_HORIZONTAL){
                            actual.arrangeType = TYPE_25_WIDTH_FULL

                            forward1.arrangeType = TYPE_25_WIDTH_FULL
                            forward2.arrangeType = TYPE_HALF_FULL
                        }
                        else if(forward1!!.aspectType == ASPECT_TYPE_HORIZONTAL && forward2!!.aspectType == ASPECT_TYPE_VERTICAL){
                            actual.arrangeType = TYPE_25_WIDTH_FULL

                            forward1.arrangeType = TYPE_HALF_FULL
                            forward2.arrangeType = TYPE_25_WIDTH_FULL
                        }

                    }
                }

                if(!reRun){
                    actualIndex += 3
                }

                assignImagesSize()
            }

            //TODO ??? 4 ->{ }  ???

        }

    }

    private fun getCampaignId() = intent.getLongExtra(CAMPAIGN_EXTRA_ID, 0)
}

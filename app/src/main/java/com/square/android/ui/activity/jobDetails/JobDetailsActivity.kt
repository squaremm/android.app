package com.square.android.ui.activity.jobDetails

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.Job
import com.square.android.presentation.presenter.jobDetails.JobDetailsPresenter
import com.square.android.presentation.view.jobDetails.JobDetailsView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.activity.jobs.JOB_EXTRA_ID
import com.square.android.ui.base.SimpleNavigator
import kotlinx.android.synthetic.main.activity_job_details.*
import ru.terrakok.cicerone.Navigator
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.AppBarLayout
import com.square.android.extensions.loadImage
import kotlinx.android.synthetic.main.activity_place_detail.*

class JobDetailsActivity: BaseActivity(), JobDetailsView {

    @InjectPresenter
    lateinit var presenter: JobDetailsPresenter

    @ProvidePresenter
    fun providePresenter() = JobDetailsPresenter(getJobId())

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    private val PERCENTAGE_TO_COLOR = 65
    private var mMaxScrollSize: Int = 0
    private var mIsElementWhite: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_details)

        setSupportActionBar(jobDetailsToolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        placeDetailAppbar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {

                if (mMaxScrollSize == 0)
                    mMaxScrollSize = placeDetailAppbar.totalScrollRange

                val currentScrollPercentage = Math.abs(p1) * 100 / mMaxScrollSize

                if (currentScrollPercentage >= PERCENTAGE_TO_COLOR) {
                    if (!mIsElementWhite) {
                        mIsElementWhite = true
                        jobDetailsName.setTextColor(ContextCompat.getColor(jobDetailsName.context, R.color.nice_pink))
                        jobDetailsBack.imageTintList = ColorStateList.valueOf(Color.BLACK)
                        placeDetailToolbar.setBackgroundColor(ContextCompat.getColor(placeDetailToolbar.context, android.R.color.transparent))
                        jobDetailsJobType.visibility = View.INVISIBLE
                    }
                }

                if (currentScrollPercentage < PERCENTAGE_TO_COLOR) {
                    if (mIsElementWhite) {
                        mIsElementWhite = false
                        jobDetailsName.setTextColor(ContextCompat.getColor(jobDetailsName.context, R.color.white))
                        jobDetailsBack.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(jobDetailsBack.context, R.color.white))
                        placeDetailToolbar.setBackgroundColor(ContextCompat.getColor(placeDetailToolbar.context, R.color.black_trans_30))
                        jobDetailsJobType.visibility = View.VISIBLE
                    }
                }
            }
        })

        jobDetailsBack.setOnClickListener {onBackPressed()}
        jobDetailsParticipate.setOnClickListener {presenter.participateClicked()}
    }

    override fun showData(job: Job) {

        when(job.type){
            1 ->{
                jobDetailsJobType.text = jobDetailsJobType.context.getString(R.string.gifting_campaign)
                jobDetailsJobType.background = ContextCompat.getDrawable(jobDetailsJobType.context, R.drawable.round_bg_pink_pinkish)
            }
            2 ->{
                jobDetailsJobType.text = jobDetailsJobType.context.getString(R.string.influencer_campaign)
                jobDetailsJobType.background = ContextCompat.getDrawable(jobDetailsJobType.context, R.drawable.round_bg_purple_purpleish)
            }
        }

        job.mainImage?.let {jobDetailsImage.loadImage(it)}

        jobDetailsName.text = job.name

//        jobDetailsDescription.text = job.description
//        jobDetailsRewards.text = job.rewards
//        job.rewardImage?.let {jobDetailsRewardImage.loadImage(it)}
//        jobDetailsDaysLeft.text = job.daysLeft
//        jobDetailsTaskLeft.text = job.taskname
//        jobDetailsTaskRight.text = job.tasks
//        jobDetailsHowItWorks.text = job.howItWorks
    }

    private fun getJobId() = intent.getLongExtra(JOB_EXTRA_ID, 0)
}

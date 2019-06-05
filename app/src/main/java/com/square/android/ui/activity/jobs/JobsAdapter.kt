package com.square.android.ui.activity.jobs

import android.view.View
import androidx.core.content.ContextCompat
import com.square.android.R
import com.square.android.data.pojo.Job
import com.square.android.extensions.loadImage
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_job.*

class JobsAdapter(data: List<Job>,
                    private val handler: Handler) : BaseAdapter<Job, JobsAdapter.JobsHolder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.item_job

    override fun getItemCount() = data.size

    override fun instantiateHolder(view: View): JobsHolder = JobsHolder(view, handler)

    class JobsHolder(containerView: View,
                       handler: Handler) : BaseHolder<Job>(containerView) {

        init {
            jobContainer.setOnClickListener {handler.itemClicked(adapterPosition)}
        }

        override fun bind(item: Job, vararg extras: Any?) {

            jobTitle.text = item.name

            when(item.type){
                1 ->{
                    jobType.text = jobType.context.getString(R.string.gifting_campaign)
                    jobType.background = ContextCompat.getDrawable(jobType.context, R.drawable.round_bg_pink_pinkish)
                }
                2 ->{
                    jobType.text = jobType.context.getString(R.string.influencer_campaign)
                    jobType.background = ContextCompat.getDrawable(jobType.context, R.drawable.round_bg_purple_purpleish)
                }
            }

            item.mainImage?.let { jobImage.loadImage(it)}
        }
    }

    interface Handler {
        fun itemClicked(position: Int)
    }
}
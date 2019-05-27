package com.square.android.ui.activity.tutorialVideos

import android.view.View
import com.square.android.R
import com.square.android.data.pojo.TutorialVideo
import com.square.android.extensions.loadImage
import com.square.android.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.item_tutorial_video.*

class TutorialVideoAdapter(data: List<TutorialVideo>,
                   private val handler: Handler?) : BaseAdapter<TutorialVideo, TutorialVideoAdapter.TutorialVideoHolder>(data) {

    override fun getLayoutId(viewType: Int) = R.layout.item_tutorial_video

    override fun getItemCount() = data.size

    override fun bindHolder(holder: TutorialVideoHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun instantiateHolder(view: View): TutorialVideoHolder = TutorialVideoHolder(view, handler)

    class TutorialVideoHolder(containerView: View,
                      var handler: Handler?) : BaseHolder<TutorialVideo>(containerView) {

        override fun bind(item: TutorialVideo, vararg extras: Any?) {

            itemTutVideoFr.setOnClickListener {handler?.itemClicked(item.videoUrl)}

            itemTutVideoLabel.text = item.title
            itemTutVideoImage.loadImage(item.thumbnailUrl, roundedCornersRadiusPx = itemTutVideoImage.context.resources.getDimension(R.dimen.value_4dp).toInt())
        }
    }

    interface Handler {
        fun itemClicked(videoUrl: String)
    }

}
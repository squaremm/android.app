package com.square.android.ui.activity.tutorialVideos

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.TutorialVideo
import com.square.android.presentation.presenter.tutorialVideos.TutorialVideosPresenter
import com.square.android.presentation.view.tutorialVideos.TutorialVideosView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import com.square.android.ui.fragment.map.MarginItemDecorator
import kotlinx.android.synthetic.main.activity_tutorial_videos.*
import ru.terrakok.cicerone.Navigator


class TutorialVideosActivity: BaseActivity(), TutorialVideosView, TutorialVideoAdapter.Handler{

    private var adapter: TutorialVideoAdapter? = null

    @InjectPresenter
    lateinit var presenter: TutorialVideosPresenter

    @ProvidePresenter
    fun providePresenter() = TutorialVideosPresenter()

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial_videos)


        tutorialVideosBack.setOnClickListener{onBackPressed()}
    }

    override fun showData(data: List<TutorialVideo>) {
        adapter = TutorialVideoAdapter(data, this)

        tutorialVideosRv.adapter = adapter
        tutorialVideosRv.layoutManager = LinearLayoutManager(tutorialVideosRv.context, RecyclerView.VERTICAL,false)
        tutorialVideosRv.addItemDecoration(MarginItemDecorator(tutorialVideosRv.context.resources.getDimension(R.dimen.rv_item_decorator_16).toInt(), vertical = true))
    }

    override fun itemClicked(videoUrl: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
        startActivity(browserIntent)
    }

}
package com.square.android.ui.activity.jobFinished

import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.Job
import com.square.android.extensions.loadImage
import com.square.android.presentation.presenter.jobFinished.JobFinishedPresenter
import com.square.android.presentation.view.jobFinished.JobFinishedView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import kotlinx.android.synthetic.main.activity_job_finished.*
import ru.terrakok.cicerone.Navigator

const val JOB_EXTRA_ID = "JOB_EXTRA_ID"

class JobFinishedActivity: BaseActivity(), JobFinishedView {

    @InjectPresenter
    lateinit var presenter: JobFinishedPresenter

    @ProvidePresenter
    fun providePresenter() = JobFinishedPresenter(getId())

    var job: Job? = null

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_finished)

        finishedBack.setOnClickListener {presenter.exit()}
    }

    override fun showData(job: Job) {
        this.job = job

        setUpPager()

        job.mainImage?.let { finishedBg.loadImage(it)}

        finishedName.text = job.name
    }

    private fun setUpPager() {
        finishedPager.adapter = JobFinishedAdapter(supportFragmentManager, job)
        finishedTab.setupWithViewPager(finishedPager)
        finishedPager.offscreenPageLimit = 2
    }

    private fun getId() = intent.getLongExtra(JOB_EXTRA_ID, 0)
}
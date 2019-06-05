package com.square.android.ui.activity.jobs

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.data.pojo.Job
import com.square.android.presentation.presenter.jobs.JobsPresenter
import com.square.android.presentation.view.jobs.JobsView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import com.square.android.ui.fragment.map.MarginItemDecorator
import kotlinx.android.synthetic.main.activity_jobs.*
import ru.terrakok.cicerone.Navigator

const val JOB_EXTRA_ID = "JOB_EXTRA_ID"
class JobsActivity: BaseActivity(), JobsView, JobsAdapter.Handler {

    @InjectPresenter
    lateinit var presenter: JobsPresenter

    @ProvidePresenter
    fun providePresenter() = JobsPresenter()

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    private var adapter: JobsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_jobs)
    }

    override fun showJobs(data: List<Job>) {

        adapter = JobsAdapter(data, this)

        jobsList.adapter = adapter

        jobsList.addItemDecoration(MarginItemDecorator(jobsList.context.resources.getDimension(R.dimen.rv_item_decorator_16).toInt(),true,
                jobsList.context.resources.getDimension(R.dimen.rv_item_decorator_12).toInt(),
                jobsList.context.resources.getDimension(R.dimen.rv_item_decorator_16).toInt()
        ))
    }

    override fun showProgress() {
        jobsProgress.visibility = View.VISIBLE
        jobsList.visibility = View.INVISIBLE
    }

    override fun hideProgress() {
        jobsProgress.visibility = View.INVISIBLE
        jobsList.visibility = View.VISIBLE
    }

    override fun itemClicked(position: Int) {
        presenter.itemClicked(position)
    }

}
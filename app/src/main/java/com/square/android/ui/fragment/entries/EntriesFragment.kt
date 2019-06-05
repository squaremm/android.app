package com.square.android.ui.fragment.entries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.square.android.R
import com.square.android.data.pojo.Job
import com.square.android.presentation.presenter.entries.EntriesPresenter
import com.square.android.presentation.view.entries.EntriesView
import com.square.android.ui.fragment.BaseFragment


class EntriesFragment(private val job: Job?): BaseFragment(), EntriesView {

    @InjectPresenter
    lateinit var presenter: EntriesPresenter

    private var adapter: EntriesAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_page_entries, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO load entry image with roundedCornersRadiusPx = context!!.dimen(R.dimen.value_4dp)


        //TODO load job entries to adapter and setup rv with adapter, add GridLayout(3 columns) and GridMargin decorator with total padding 8
    }

}
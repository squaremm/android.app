package com.square.android.ui.fragment.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.square.android.R
import com.square.android.presentation.view.intro.IntroView
import com.square.android.presentation.presenter.intro.IntroPresenter

import com.arellomobile.mvp.presenter.InjectPresenter

import com.square.android.ui.fragment.BaseFragment
import kotlinx.android.synthetic.main.fragment_intro.*

class IntroFragment : BaseFragment(), IntroView {
    @InjectPresenter
    lateinit var presenter: IntroPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_intro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpPager()
    }

    private fun setUpPager() {
        val adapter = IntroPageAdapter(childFragmentManager, View.OnClickListener {introPager.currentItem = introPager.currentItem+1})

        introPager.adapter = adapter
        introTabs.setupWithViewPager(introPager)
    }
}

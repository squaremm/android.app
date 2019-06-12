package com.square.android.ui.activity.passEligible

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.presentation.presenter.passEligible.PassEligiblePresenter
import com.square.android.presentation.view.passEligible.PassEligibleView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import kotlinx.android.synthetic.main.activity_pass_eligible.*
import ru.terrakok.cicerone.Navigator

class PassEligibleActivity: BaseActivity(), PassEligibleView {

    @InjectPresenter
    lateinit var presenter: PassEligiblePresenter

    @ProvidePresenter
    fun providePresenter() = PassEligiblePresenter()

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_eligible)

        passPayBtn.setOnClickListener {presenter.pay()}
    }

    override fun showProgress() {
        passPayBtn.visibility = View.GONE
        passProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        passProgress.visibility = View.GONE
        passPayBtn.visibility = View.VISIBLE
    }
}
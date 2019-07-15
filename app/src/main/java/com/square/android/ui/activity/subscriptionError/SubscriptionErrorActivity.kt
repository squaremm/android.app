package com.square.android.ui.activity.subscriptionError

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.presentation.presenter.subscriptionError.SubscriptionErrorPresenter
import com.square.android.presentation.view.subscriptionError.SubscriptionErrorView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import kotlinx.android.synthetic.main.activity_subscription_error.*
import ru.terrakok.cicerone.Navigator

class SubscriptionErrorActivity: BaseActivity(), SubscriptionErrorView{

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    @InjectPresenter
    lateinit var presenter: SubscriptionErrorPresenter

    @ProvidePresenter
    fun providePresenter() = SubscriptionErrorPresenter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription_error)

        subErrorTryAgain.setOnClickListener{
            presenter.checkSubs()
        }
    }

    override fun showNoConnectionLabel() {
        subErrorConnectionLabel.visibility = View.VISIBLE
    }

    override fun showProgress() {
        subErrorConnectionLabel.visibility = View.GONE
        subErrorTryAgain.visibility = View.GONE
        subErrorProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        subErrorProgress.visibility = View.GONE
        subErrorTryAgain.visibility = View.VISIBLE
    }

    override fun finishAc() {
        super.onBackPressed()
    }

    override fun onBackPressed() { }
}

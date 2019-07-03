package com.square.android.ui.activity.start

import android.content.Context
import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.gms.common.api.internal.zzc
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.androidx.navigator.AppNavigator
import com.square.android.data.pojo.ProfileInfo
import com.square.android.presentation.presenter.start.StartPresenter
import com.square.android.presentation.view.start.StartView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.activity.main.MainActivity
import com.square.android.ui.fragment.auth.AuthFragment
import com.square.android.ui.fragment.fillProfileFirst.FillProfileFirstFragment
import com.square.android.ui.fragment.fillProfileReferral.FillProfileReferralFragment
import com.square.android.ui.fragment.fillProfileSecond.FillProfileSecondFragment
import com.square.android.ui.fragment.fillProfileThird.FillProfileThirdFragment
import com.square.android.ui.fragment.intro.IntroFragment
import org.jetbrains.anko.intentFor
import ru.terrakok.cicerone.Navigator

class StartActivity : BaseActivity(), StartView {

    @InjectPresenter
    lateinit var presenter: StartPresenter


    override fun provideNavigator(): Navigator = StartNavigator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.fragments.last() is FillProfileFirstFragment
                || supportFragmentManager.fragments.last() is AuthFragment
                || supportFragmentManager.fragments.last() is zzc
                || supportFragmentManager.fragments.last() is IntroFragment) {
            finishAffinity()

            System.exit(0)
        } else {
            super.onBackPressed()
        }
    }

    private class StartNavigator(activity: androidx.fragment.app.FragmentActivity) : AppNavigator(activity, R.id.start_container) {
        override fun createActivityIntent(context: Context, screenKey: String, data: Any?) =
                when (screenKey) {
                    SCREENS.MAIN -> context.intentFor<MainActivity>()
                    else -> null
                }

        override fun createFragment(screenKey: String, data: Any?) =
                when (screenKey) {
                    SCREENS.INTRO -> IntroFragment()
                    SCREENS.AUTH -> AuthFragment()
                    SCREENS.FILL_PROFILE_FIRST -> FillProfileFirstFragment.newInstance(data as ProfileInfo)
                    SCREENS.FILL_PROFILE_SECOND -> FillProfileSecondFragment.newInstance(data as ProfileInfo)
                    SCREENS.FILL_PROFILE_THIRD -> FillProfileThirdFragment.newInstance(data as ProfileInfo)
                    SCREENS.FILL_PROFILE_REFERRAL -> FillProfileReferralFragment.newInstance(data as ProfileInfo)
                    else -> throw IllegalArgumentException("Unknown screen key: $screenKey")
                }
    }
}

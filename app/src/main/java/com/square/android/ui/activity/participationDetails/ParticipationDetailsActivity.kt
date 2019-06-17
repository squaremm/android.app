package com.square.android.ui.activity.participationDetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.androidx.navigator.AppNavigator
import com.square.android.data.pojo.Participation
import com.square.android.extensions.loadImage
import com.square.android.presentation.presenter.participationDetails.ParticipationDetailsPresenter
import com.square.android.presentation.view.participationDetails.ParticipationDetailsView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.fragment.addPhoto.AddPhotoFragment
import com.square.android.ui.fragment.approval.ApprovalFragment
import com.square.android.ui.fragment.uploadPics.UploadPicsFragment
import kotlinx.android.synthetic.main.activity_participation_details.*
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward

const val EXTRA_PARTICIPATION = "EXTRA_PARTICIPATION"

const val PARTICIPATION_EXTRA_ID = "PARTICIPATION_EXTRA_ID"

const val PARTICIPATION_MAX_PHOTOS_VALUE = 6
const val PARTICIPATION_MIN_PHOTOS_VALUE = 3

class ParticipationDetailsActivity: BaseActivity(), ParticipationDetailsView{

    @InjectPresenter
    lateinit var presenter: ParticipationDetailsPresenter

    @ProvidePresenter
    fun providePresenter() = ParticipationDetailsPresenter(getParticipationId())

    override fun provideNavigator(): Navigator = ParticipationDetailsNavigator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_participation_details)

        participationDetailsBack.setOnClickListener {presenter.exit()}
    }

    override fun showData(participation: Participation) {
        participation.mainImage?.let {participationDetailsBg.loadImage(it)}

        participationDetailsName.text = participation.name
    }

    fun replaceToApproval(){
        presenter.replaceToApproval()
    }

    fun navigateToAddPhoto(){
        presenter.navigateToAddPhoto()
    }

    override fun showProgress() {
        participationDetailsCardView.visibility = View.GONE
        participationDetailsProgress.visibility =  View.VISIBLE
    }

    override fun hideProgress() {
        participationDetailsCardView.visibility = View.VISIBLE
        participationDetailsProgress.visibility =  View.GONE
    }

    private class ParticipationDetailsNavigator(activity: FragmentActivity) : AppNavigator(activity, R.id.participationDetailsContainer) {

        override fun createActivityIntent(context: Context, screenKey: String, data: Any?): Intent? {
            return null
        }

        override fun createFragment(screenKey: String, data: Any?) = when (screenKey) {
            SCREENS.UPLOAD_PICS -> UploadPicsFragment.newInstance(data as Participation)
            SCREENS.ADD_PHOTO -> AddPhotoFragment.newInstance(data as Participation)
            SCREENS.APPROVAL -> ApprovalFragment.newInstance(data as Participation)
            else -> throw IllegalArgumentException("Unknown screen key: $screenKey")
        }

        override fun setupFragmentTransactionAnimation(command: Command,
                                                       currentFragment: Fragment?,
                                                       nextFragment: Fragment,
                                                       fragmentTransaction: FragmentTransaction) {

            if(command is Forward){
                fragmentTransaction.setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right)
            } else{
                fragmentTransaction.setCustomAnimations(R.anim.fade_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.fade_out)
            }

        }
    }

    private fun getParticipationId() = intent.getLongExtra(PARTICIPATION_EXTRA_ID, 0)
}

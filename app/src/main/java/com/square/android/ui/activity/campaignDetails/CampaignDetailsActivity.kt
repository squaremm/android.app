package com.square.android.ui.activity.campaignDetails

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.androidx.navigator.AppNavigator
import com.square.android.data.pojo.Campaign
import com.square.android.data.pojo.CampaignInterval
import com.square.android.extensions.loadImage
import com.square.android.presentation.presenter.campaignDetails.CampaignDetailsPresenter
import com.square.android.presentation.view.campaignDetails.CampaignDetailsView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.activity.campaignFinished.CampaignFinishedActivity
import com.square.android.ui.fragment.addPhoto.AddPhotoFragment
import com.square.android.ui.fragment.approval.ApprovalFragment
import com.square.android.ui.fragment.campaignNotApproved.CampaignNotApprovedFragment
import com.square.android.ui.fragment.pickUpInterval.PickUpIntervalExtras
import com.square.android.ui.fragment.pickUpInterval.PickUpIntervalFragment
import com.square.android.ui.fragment.pickUpLocation.PickUpLocationFragment
import com.square.android.ui.fragment.pickUpSpot.PickUpSpotFragment
import com.square.android.ui.fragment.scanQr.ScanQrFragment
import com.square.android.ui.fragment.uploadPics.UploadPicsFragment
import com.square.android.ui.fragment.winner.WinnerFragment
import kotlinx.android.synthetic.main.activity_campaign_details.*
import org.jetbrains.anko.intentFor
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward

const val EXTRA_CAMPAIGN = "EXTRA_CAMPAIGN"


const val EXTRA_CAMPAIGN_ID = "EXTRA_CAMPAIGN_ID"
const val EXTRA_INTERVAL_ID = "EXTRA_INTERVAL_ID"

const val EXTRA_CAMPAIGN_LOCATION = "EXTRA_CAMPAIGN_LOCATION"

const val CAMPAIGN_EXTRA_ID = "CAMPAIGN_EXTRA_ID"

const val CAMPAIGN_MAX_PHOTOS_VALUE = 6
const val CAMPAIGN_MIN_PHOTOS_VALUE = 3

class CampaignDetailsActivity: BaseActivity(), CampaignDetailsView{

    @InjectPresenter
    lateinit var presenter: CampaignDetailsPresenter

    @ProvidePresenter
    fun providePresenter() = CampaignDetailsPresenter(getCampaignId())

    override fun provideNavigator(): Navigator = CampaignNavigator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campaign_details)

        campaignBack.setOnClickListener {presenter.exit()}
    }

    override fun showData(campaign: Campaign) {
        campaign.mainImage?.let {campaignBg.loadImage(it)}

        campaignName.text = campaign.title
    }

    fun replaceToApproval(){
        presenter.replaceToApproval()
    }

    fun navigateToAddPhoto(){
        presenter.navigateToAddPhoto()
    }

    fun navigateToQr(){
        presenter.navigateToQr()
    }

    override fun showProgress() {
        campaignContainer.visibility = View.GONE
        campaignProgress.visibility =  View.VISIBLE
    }

    override fun hideProgress() {
        campaignContainer.visibility = View.VISIBLE
        campaignProgress.visibility =  View.GONE
    }

    private class CampaignNavigator(activity: FragmentActivity) : AppNavigator(activity, R.id.campaignContainer) {

        override fun createActivityIntent(context: Context, screenKey: String, data: Any?) = when (screenKey) {
            SCREENS.WINNER -> context.intentFor<CampaignFinishedActivity>(com.square.android.ui.activity.campaignFinished.CAMPAIGN_EXTRA_ID to (data as Campaign).id)
            else -> null

        }

        override fun createFragment(screenKey: String, data: Any?): Fragment? {
            return when (screenKey) {
                SCREENS.NOT_APPROVED -> CampaignNotApprovedFragment.newInstance(data as Campaign)
                SCREENS.UPLOAD_PICS -> UploadPicsFragment.newInstance(data as Campaign)
                SCREENS.ADD_PHOTO -> AddPhotoFragment.newInstance(data as Campaign)
                SCREENS.APPROVAL -> ApprovalFragment.newInstance(data as Campaign)
                SCREENS.PICK_UP_SPOT -> PickUpSpotFragment.newInstance(data as Long)

                SCREENS.PICK_UP_INTERVAL -> {
                    val extras = data as PickUpIntervalExtras
                    PickUpIntervalFragment.newInstance(extras.campaignId, extras.intervalId)
                }

                SCREENS.PICK_UP_LOCATION -> PickUpLocationFragment.newInstance(data as CampaignInterval.Location)
                SCREENS.SCAN_QR -> ScanQrFragment()
                else -> throw IllegalArgumentException("Unknown screen key: $screenKey")
            }
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

    private fun getCampaignId() = intent.getLongExtra(CAMPAIGN_EXTRA_ID, 0)
}

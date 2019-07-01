package com.square.android.ui.fragment.redemptions

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.square.android.R
import com.square.android.data.pojo.RedemptionInfo
import com.square.android.presentation.presenter.redemptions.RedemptionsPresenter
import com.square.android.presentation.view.redemptions.RedemptionsView
import com.square.android.ui.base.tutorial.Tutorial
import com.square.android.ui.base.tutorial.TutorialService
import com.square.android.ui.base.tutorial.TutorialStep
import com.square.android.ui.fragment.LocationFragment
import kotlinx.android.synthetic.main.fragment_redemptions.*

class RedemptionsFragment : LocationFragment(), RedemptionsView, RedemptionsAdapter.Handler {

    @InjectPresenter(type = PresenterType.GLOBAL, tag = "RedemptionsPresenter")
    lateinit var presenter: RedemptionsPresenter

    private var adapter : RedemptionsAdapter? = null

    var initialized = false

    override fun showData(ordered: List<Any>) {
        adapter = RedemptionsAdapter(ordered, this)

        redemptionsList.adapter = adapter

        if(!initialized){
            initialized = true
            visibleNow()
        }
    }

    override fun locationGotten(lastLocation: Location?) {
        presenter.locationGotten(lastLocation)
    }

    override fun showProgress() {
        redemptionsProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        redemptionsProgress.visibility = View.GONE
    }

    override fun claimClicked(position: Int) {
        presenter.claimClicked(position)
    }

    override fun claimedItemClicked(position: Int) {
        presenter.claimedInfoClicked(position)
    }

    override fun removeItem(position: Int) {
        adapter?.removeItem(position)
    }

    override fun cancelClicked(position: Int) {
        presenter.cancelClicked(position)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_redemptions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        redemptionsList.setHasFixedSize(true)
    }

    override val PERMISSION_REQUEST_CODE: Int?
        get() = 1340

    override val tutorial: Tutorial?
        get() = when {
            presenter.data
                    ?.filterIsInstance<RedemptionInfo>()
                    ?.isNullOrEmpty() == true -> null
            else -> Tutorial.Builder(tutorialKey = TutorialService.TutorialKey.REDEMPTIONS)
                    .addNextStep(TutorialStep(
                            // width percentage, height percentage for text with arrow
                            floatArrayOf(0.50f, 0.78f),
                            getString(R.string.tut_3_1),
                            TutorialStep.ArrowPos.TOP,
                            R.drawable.arrow_bottom_right_x_top_left,
                            0.35f,
                            // marginStart dp, marginEnd dp, horizontal center of the transView in 0.0f - 1f, height of the transView in dp
                            // 0f,0f,0f,0f for covering entire screen
                            floatArrayOf(0f, 0f, 0.30f, 500f),
                            1,
                            // delay before showing view in ms
                            0f))
                    .addNextStep(TutorialStep(
                            // width percentage, height percentage for text with arrow
                            floatArrayOf(0.50f, 0.38f),
                            getString(R.string.tut_3_2),
                            TutorialStep.ArrowPos.TOP,
                            R.drawable.arrow_bottom_right_x_top_left,
                            0.35f,
                            // marginStart dp, marginEnd dp, horizontal center of the transView in 0.0f - 1f, height of the transView in dp
                            // 0f,0f,0f,0f for covering entire screen
                            floatArrayOf(0f, 0f, 0.1f, 190f),
                            1,
                            // delay before showing view in ms
                            500f,
                            0))
                    .setOnNextStepIsChangingListener {

                    }
                    .setOnContinueTutorialListener {
                        presenter.claimClicked(1)
                    }
                    .build()
        }
}

package com.square.android.ui.fragment.offer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.square.android.R
import com.square.android.data.pojo.OfferInfo
import com.square.android.data.pojo.Place
import com.square.android.presentation.presenter.offer.OfferPresenter
import com.square.android.presentation.view.offer.OfferView
import com.square.android.ui.activity.placeDetail.PlaceDetailActivity
import com.square.android.ui.base.tutorial.Tutorial
import com.square.android.ui.base.tutorial.TutorialService
import com.square.android.ui.base.tutorial.TutorialStep
import com.square.android.ui.base.tutorial.TutorialView
import com.square.android.ui.fragment.BaseFragment
import com.square.android.ui.fragment.map.MarginItemDecorator
import kotlinx.android.synthetic.main.fragment_offer.*
import java.lang.Exception
import java.util.*

class OfferFragment(private val place: Place?) : BaseFragment(), OfferView, OfferAdapter.Handler {

    private var adapter: OfferAdapter? = null

    private var dialog: OfferDialog? = null

    @InjectPresenter
    lateinit var presenter: OfferPresenter

    override fun showData(data: List<OfferInfo>) {
        adapter = OfferAdapter(data, this)

        offerList.adapter = adapter

        offerList.addItemDecoration(MarginItemDecorator( offerList.context.resources.getDimension(R.dimen.rv_item_decorator_12).toInt(),true,
                offerList.context.resources.getDimension(R.dimen.rv_item_decorator_12).toInt(),
                offerList.context.resources.getDimension(R.dimen.rv_item_decorator_16).toInt()
        ))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_offer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        offerList.setHasFixedSize(true)
    }

    override fun itemClicked(position: Int) {
        presenter.itemClicked(position, place)
    }

    override fun setSelectedItem(position: Int) {
        adapter?.setSelectedItem(position)
    }

    override fun showOfferDialog(offer: OfferInfo, place: Place?) {
        context?.let {
            dialog = OfferDialog(it)
            dialog!!.show(offer, place)
        }
    }

    override val PERMISSION_REQUEST_CODE: Int?
        get() = 1338

    override val tutorial: Tutorial?
        get() =  Tutorial.Builder(tutorialKey = TutorialService.TutorialKey.PLACE)
                .addNextStep(TutorialStep(
                        // width percentage, height percentage for text with arrow
                        floatArrayOf(0.60f, 0.565f),
                        getString(R.string.tut_1_1),
                        TutorialStep.ArrowPos.BOTTOM,
                        R.drawable.arrow_bottom_left_x_top_right,
                        0.3f,
                        // marginStart dp, marginEnd dp, horizontal center of the transView in 0.0f - 1f, height of the transView in dp
                        // 0f,0f,0f,0f for covering entire screen
                        floatArrayOf(0f,0f,0.76f,88f),
                        1,
                        // delay before showing view in ms
                        0f))
                .addNextStep(TutorialStep(
                        // width percentage, height percentage for text with arrow
                        floatArrayOf(0.65f, 0.92f),
                        getString(R.string.tut_1_2),
                        TutorialStep.ArrowPos.TOP,
                        R.drawable.arrow_bottom_left_x_top_right,
                        0.8f,
                        // marginStart dp, marginEnd dp, horizontal center of the transView in 0.0f - 1f, height of the transView in dp
                        // 0f,0f,0f,0f for covering entire screen
                        floatArrayOf(0f,0f,0.667f,230f),
                        1,
                        // delay before showing view in ms
                        500f))
                .addNextStep(TutorialStep(
                        // width percentage, height percentage for text with arrow
                        floatArrayOf(0.65f, 0.92f),
                        getString(R.string.tut_1_2),
                        TutorialStep.ArrowPos.TOP,
                        R.drawable.arrow_bottom_left_x_top_right,
                        0.8f,
                        // marginStart dp, marginEnd dp, horizontal center of the transView in 0.0f - 1f, height of the transView in dp
                        // 0f,0f,0f,0f for covering entire screen
                        floatArrayOf(0f,0f,0f,0f),
                        0,
                        // delay before showing view in ms
                        500f,
                        250))
                .setOnNextStepIsChangingListener {
                    if(it == 3){
                        presenter.itemClicked(0, place)
                    }
                }
                .setOnContinueTutorialListener {
                    dialog?.close()

                    Timer().schedule(object : TimerTask() {
                        override fun run() {
                            try{
                                (activity as PlaceDetailActivity).pagerMoveToAnotherPage(1)
                            } catch (exception: Exception){
                            }
                        }
                    }, it+50)
                }
                .build()
}

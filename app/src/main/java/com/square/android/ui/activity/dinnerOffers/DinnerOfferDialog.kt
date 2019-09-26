package com.square.android.ui.activity.dinnerOffers

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.ViewPager
import com.square.android.R
import com.square.android.data.pojo.OfferInfo
import com.square.android.ui.fragment.dinnerInfo.DinnerBackClickedEvent
import com.square.android.ui.fragment.dinnerOffer.DinnerInfoClickedEvent
import com.square.android.ui.fragment.dinnerOffer.DinnerInfoCloseEvent
import kotlinx.android.synthetic.main.dinner_offer_dialog.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject

class DinnerOfferDialog(var offerInfo: OfferInfo, private val handler: Handler?, var mCancelable: Boolean = true): DialogFragment() {

    private var currentPagerPosition = 0

    private val eventBus: EventBus by inject()

    init {
        eventBus.register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDinnerBackClickedEvent(event: DinnerBackClickedEvent) {
        dinnerOfferPager.setCurrentItem(0,true)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDinnerInfoCloseEvent(event: DinnerInfoCloseEvent) {
        dialog.cancel()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDinnerInfoClickedEvent(event: DinnerInfoClickedEvent) {
        dinnerOfferPager.setCurrentItem(1,true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(mCancelable)
        dialog.window!!.setGravity(Gravity.CENTER)

        return inflater.inflate(R.layout.dinner_offer_dialog, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpPager()
        setUpPage(currentPagerPosition)

        dinnerOfferBtn.setOnClickListener {confirmClicked()}
    }

    private fun confirmClicked(){
        handler?.confirmClicked()
        dialog.cancel()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val metrics = resources.displayMetrics
        val screenWidth = (metrics.widthPixels * 0.9).toInt()

        dialog.window?.setLayout(screenWidth, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    private fun setUpPager() {
        dinnerOfferPager.isPagingEnabled = false
        dinnerOfferPager.adapter = DinnerOfferDialogAdapter(childFragmentManager, offerInfo)
        dinnerOfferPager.offscreenPageLimit = 2

        dinnerOfferPager.addOnPageChangeListener( object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) { }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }
            override fun onPageSelected(position: Int) {
                currentPagerPosition = position
                setUpPage(currentPagerPosition)
            }
        })
    }

    fun setUpPage(position: Int){
        when(position){
            0 -> { dinnerOfferBtn.visibility = View.VISIBLE }
            1 -> { dinnerOfferBtn.visibility = View.GONE }
        }
    }

    interface Handler {
        fun confirmClicked()
    }

    override fun onDestroy() {
        eventBus.unregister(this)
        super.onDestroy()
    }

}
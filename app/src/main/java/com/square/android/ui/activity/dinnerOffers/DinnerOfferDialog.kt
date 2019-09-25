package com.square.android.ui.activity.dinnerOffers

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.ViewPager
import com.google.android.libraries.places.api.Places
import com.mapbox.mapboxsdk.Mapbox
import com.square.android.Network
import com.square.android.R
import com.square.android.data.pojo.Offer
import com.square.android.ui.activity.event.*
import kotlinx.android.synthetic.main.driver_dialog.*
import kotlinx.android.synthetic.main.driver_dialog.view.*

class DinnerOfferDialog(var offer: Offer, private val handler: Handler?, var mCancelable: Boolean = true): DialogFragment() {

    private var currentPagerPosition = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(mCancelable)
        dialog.window!!.setGravity(Gravity.CENTER)

        return inflater.inflate(R.layout.driver_dialog, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpPager()
        setUpPage(currentPagerPosition)

        acDriverBack.setOnClickListener {
            acDriverPager.setCurrentItem(0,true)
        }

        acDriverBtn.setOnClickListener { confirmClicked() }

//        acDriverExit.setOnClickListener { dialog.cancel() }

        Places.initialize(Mapbox.getApplicationContext(), Network.GOOGLE_PLACES_KEY)
        Places.createClient(context!!)
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
        view!!.acDriverPager.isPagingEnabled = false

        //TODO change to DinnerOfferDialogAdapter
        view!!.acDriverPager.adapter = DriverAdapter(childFragmentManager)

        view!!.acDriverPager.offscreenPageLimit = 2

        acDriverPager.addOnPageChangeListener( object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) { }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }
            override fun onPageSelected(position: Int) {
                currentPagerPosition = position
                setUpPage(currentPagerPosition)
            }
        })
    }

    //TODO rename dinner_offer_dialog

    //TODO create main layout with ViewPager and name it dinner_offer_dialog
    //TODO move button to main layout

    //TODO make two dialog views as fragments

    //TODO visibility fot button, close icon and back arrow
    fun setUpPage(position: Int){
        when(position){
            0 -> {
                acDriverBtn.isEnabled = !needDriver || driverFilled
                acDriverBtn.text = getString(R.string.next)
                acDriverBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.button_arrow, 0)
                acDriverBtn.setPadding(resources.getDimensionPixelSize(R.dimen.customBtnPaddingStart),0, resources.getDimensionPixelSize(R.dimen.customBtnPaddingEnd),0)
            }
            1 -> {
                acDriverBtn.isEnabled = !needReturn || returnFilled
                acDriverBtn.text = getString(R.string.confirm)
                acDriverBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                acDriverBtn.setPadding(0,0,0,0)
            }
        }
    }

    interface Handler {
        fun confirmClicked()
    }
}













//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.DialogInterface
//import android.graphics.drawable.ColorDrawable
//import android.view.LayoutInflater
//import android.view.View
//import androidx.core.content.ContextCompat
//import com.afollestad.materialdialogs.MaterialDialog
//import com.square.android.R
//import com.square.android.data.pojo.OfferInfo
//import com.square.android.data.pojo.Place
//import com.square.android.extensions.loadImage
//import kotlinx.android.synthetic.main.offer_dialog.view.*
//import java.util.regex.Pattern
//
//class OfferDialog(private val context: Context, var cancelable: Boolean = true) {
//
//    lateinit var dialog: MaterialDialog
//
//    @SuppressLint("InflateParams")
//    fun show(offer: OfferInfo, place: Place?, cancelHandler: Handler? = null) {
//        val inflater = LayoutInflater.from(context)
//        val view = inflater.inflate(R.layout.offer_dialog, null, false)
//
//        dialog = MaterialDialog.Builder(context)
//                .customView(view, false)
//                .cancelable(cancelable)
//                .cancelListener { DialogInterface.OnCancelListener {cancelHandler?.dialogCancelled()} }
//                .build()
//
//        dialog.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent)))
//
//        view.offerDialogImg.loadImage((offer.mainImage ?: offer.photo) ?: "")
//        view.offerDialogName.text = offer.name
//        view.offerDialogCredits.text = offer.price.toString()
//
//        //TODO change later
//        //TODO separate values for numbers and names will be added in API later
//        if(!offer.composition.isNullOrEmpty()){
//            view.scrollViewMaxHeight.visibility = View.VISIBLE
//            view.offerDialogDetails.visibility = View.VISIBLE
//            view.offerDialogQt.visibility = View.VISIBLE
//
//            val numberList: MutableList<Int> = mutableListOf()
//            val names = offer.compositionAsStr()
//
//            val p = Pattern.compile("\\d+")
//            val m = p.matcher(offer.compositionAsString())
//            while (m.find()) {
//                numberList.add(m.group().toInt())
//            }
//
//            view.offerNames.text = names
//
//            view.offerNumbers.text = numberList.joinToString(separator = "\n")
//        }
//
//        view.offerClose.setOnClickListener { close() }
//
//        dialog.show()
//    }
//
//    fun close(){
//        dialog.cancel()
//    }
//
//    interface Handler {
//        fun dialogCancelled()
//    }
//
//}
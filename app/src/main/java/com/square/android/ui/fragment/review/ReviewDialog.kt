package com.square.android.ui.fragment.review

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.viewpager.widget.ViewPager
import com.square.android.R
import com.square.android.data.pojo.*
import com.square.android.extensions.toBytes
import com.square.android.ui.fragment.reviewUpload.PhotoResultEvent
import com.square.android.ui.fragment.reviewUpload.ReviewPhotoEvent
import com.square.android.utils.FileUtils
import com.square.android.utils.IMAGE_PICKER_RC
import kotlinx.android.synthetic.main.review_dialog.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject

class ReviewDialog(val index: Int, val action: Offer.Action, private val subActions: List<Offer.Action> = listOf(), private val instaName: String,
                   private val fbName: String, private val handler: Handler, private val mCancelable: Boolean, private val fromClaimed: Boolean): DialogFragment() {

    private var currentPagerPosition = 0

    private var photo: ByteArray? = null

    private val eventBus: EventBus by inject()

    init {
        eventBus.register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReviewPhotoEvent(event: ReviewPhotoEvent) {
        changePhoto(event.data)
    }

    private fun changePhoto(uri: Uri?){
        uri?.let {
            photo = uri.toBytes(context!!)
        } ?: run{
            photo = null
        }

        reviewBtnAction.isEnabled = uri != null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.setCancelable(mCancelable)
        dialog.window!!.setGravity(Gravity.CENTER)

        return inflater.inflate(R.layout.review_dialog, null, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val metrics = resources.displayMetrics
        val screenWidth = (metrics.widthPixels * 0.9).toInt()
        val screenHeight = (metrics.heightPixels * 0.9).toInt()

        dialog.window?.setLayout(screenWidth, screenHeight)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpPager()
        setUpPage(currentPagerPosition)

        reviewDialogClose.setOnClickListener { dialog.cancel() }
        reviewDialogBack.setOnClickListener { reviewPager.setCurrentItem(0,true) }

        reviewBtnAction.setOnClickListener {
            when(currentPagerPosition){
                0 -> openByType()
                1 -> {
                    photo?.let {
                        handler.sendClicked(index, it)
                        dialog.cancel()
                    }
                }
            }
        }
    }

    private fun openByType() {
        val mPackage = when(action.type){
            //TODO there will be more types - facebook review, facebook story etc
            TYPE_FACEBOOK_POST -> getString(R.string.facebook_package)
            TYPE_INSTAGRAM_POST, TYPE_INSTAGRAM_STORY -> getString(R.string.instagram_package)
            TYPE_TRIP_ADVISOR -> getString(R.string.tripadvisor_package)
            TYPE_GOOGLE_PLACES -> getString(R.string.google_maps_package)
            TYPE_YELP -> getString(R.string.yelp_package)
            else -> ""
        }

        if(action.type != TYPE_PICTURE){
            val intent = activity?.packageManager?.getLaunchIntentForPackage(mPackage)

            intent?.let {
                intent.addCategory(Intent.CATEGORY_LAUNCHER)
                startActivity(intent)
            } ?: run{ Toast.makeText(activity, getString(R.string.app_not_installed), Toast.LENGTH_SHORT).show()}
        }

        reviewPager.setCurrentItem(1,true)
    }

    private fun setUpPager() {
        reviewPager.isPagingEnabled = false
        reviewPager.adapter = ReviewFragmentAdapter(childFragmentManager, action, subActions, instaName, fbName)

        reviewPager.offscreenPageLimit = 2

        reviewPager.addOnPageChangeListener( object: ViewPager.OnPageChangeListener {
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
            0 -> {
                reviewDialogBack.visibility = View.GONE
                reviewDialogClose.visibility =  View.VISIBLE
                reviewBtnAction.isEnabled = true
            }
            1 -> {
                reviewDialogClose.visibility = View.GONE
                reviewDialogBack.visibility =  View.VISIBLE
                reviewBtnAction.isEnabled = photo != null
            }
        }

        setBtnText()
    }

    private fun setBtnText(){
        if(currentPagerPosition == 0){

//TODO there will be more types - facebook review, facebook story etc
            reviewBtnAction.text = when(action.type){
                TYPE_FACEBOOK_POST -> getString(R.string.action_btn_fb)
                TYPE_INSTAGRAM_POST, TYPE_INSTAGRAM_STORY -> getString(R.string.action_btn_instagram)
                TYPE_TRIP_ADVISOR -> getString(R.string.action_btn_tripadvisor)
                TYPE_GOOGLE_PLACES -> getString(R.string.action_btn_google_places)
                TYPE_YELP -> getString(R.string.action_btn_yelp)
                TYPE_PICTURE -> getString(R.string.action_btn_photo)
                else -> "TODO"
            }
        } else{

            reviewBtnAction.text = if(fromClaimed) getString(R.string.send) else getString(R.string.accept)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        val uri: Uri? = when (requestCode) {
            IMAGE_PICKER_RC -> data?.data
            else -> data?.extras?.get("data") as Uri? ?: FileUtils.getOutputFileUri(activity!!)
        }

        uri?.let {eventBus.post(PhotoResultEvent(it))}
    }

    interface Handler {
        fun sendClicked(index: Int, photo: ByteArray)
    }

    override fun onDestroy() {
        eventBus.unregister(this)
        super.onDestroy()
    }

}
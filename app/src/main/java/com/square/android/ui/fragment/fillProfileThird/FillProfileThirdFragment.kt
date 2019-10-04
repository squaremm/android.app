package com.square.android.ui.fragment.fillProfileThird

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.mapbox.android.core.permissions.PermissionsListener
import com.square.android.R
import com.square.android.data.pojo.ProfileInfo
import com.square.android.extensions.loadImage
import com.square.android.extensions.toBytes
import com.square.android.presentation.presenter.fillProfileThird.FillProfileThirdPresenter
import com.square.android.presentation.view.fillProfileThird.FillProfileThirdView
import com.square.android.ui.fragment.BaseFragment
import com.square.android.utils.FileUtils
import com.square.android.utils.IMAGE_PICKER_RC
import com.square.android.utils.PermissionsManager
import kotlinx.android.synthetic.main.fragment_fill_profile_3.*
import kotlinx.android.synthetic.main.profile_form_3.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.dimen

private const val EXTRA_MODEL = "EXTRA_MODEL"

class FillProfileThirdFragment: BaseFragment(), FillProfileThirdView, PermissionsListener {

    // Commented because we're not saving images on device right now
    override fun showData(profileInfo: ProfileInfo) {

//            profileInfo.images?.let {
//                images = it.toMutableList()
//            }
//            profileInfo.imagesUri?.let {
//                imagesUri = it.toMutableList()
//
//                for (x in 0 until imagesUri.size) {
//                    if (imagesUri[x] != null) {
//                        when (x) {
//                            0 -> form3Img1.loadImage(imagesUri[x]!!, roundedCornersRadiusPx = context!!.dimen(R.dimen.value_12dp), placeholder = R.color.white)
//                            1 -> form3Img2.loadImage(imagesUri[x]!!, roundedCornersRadiusPx = context!!.dimen(R.dimen.value_12dp), placeholder = R.color.white)
//                            2 -> form3Img3.loadImage(imagesUri[x]!!, roundedCornersRadiusPx = context!!.dimen(R.dimen.value_12dp), placeholder = R.color.white)
//                        }
//                    }
//                }
//            }
//
//            fillProfile3Next.isEnabled = checkImagesFilled()


        // clearing images because we don't save them now
        presenter.info.images = null
        images = mutableListOf(null, null, null)
        imagesUri = mutableListOf(null, null, null)
        fillProfile3Next.isEnabled = false
    }

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(info: ProfileInfo): FillProfileThirdFragment {
            val fragment = FillProfileThirdFragment()

            val args = bundleOf(EXTRA_MODEL to info)
            fragment.arguments = args

            return fragment
        }
    }

    @InjectPresenter
    lateinit var presenter: FillProfileThirdPresenter

    @ProvidePresenter
    fun providePresenter(): FillProfileThirdPresenter = FillProfileThirdPresenter(getModel())

    var images: MutableList<ByteArray?> = mutableListOf(null, null, null)

    var imagesUri: MutableList<Uri?> = mutableListOf(null, null, null)

    private val permissionsManager by lazy {
        PermissionsManager(this)
    }

    private var photoSelected = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fill_profile_3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fillProfile3Next.setOnClickListener { nextClicked() }

        form3Img1.setOnClickListener { imageClicked(0) }
        form3Img2.setOnClickListener { imageClicked(1) }
        form3Img3.setOnClickListener { imageClicked(2) }

        fillProfile3Back.setOnClickListener { activity?.onBackPressed() }

        form3Img1.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                form3ProfilePic.width = form3Img1.measuredWidth

                form3Img1.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        form3Img3.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                form3OtherPic.width = form3Img3.measuredWidth

                form3Img3.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun imageClicked(index: Int){
        if(images[index] == null){
            photoSelected = index
            openPhotoPicker()
        } else{
            deleteImage(index)
        }
    }

    private fun deleteImage(index: Int){
        val drawable = ContextCompat.getDrawable(form3Img1.context, R.drawable.image_holder)

        when(index){
            0 -> form3Img1.setImageDrawable(drawable)
            1 -> form3Img2.setImageDrawable(drawable)
            2 -> form3Img3.setImageDrawable(drawable)
        }

        images[index] = null
        imagesUri[index] = null

        checkEnabledAndMissing()
    }

    private fun nextClicked(){
        if(checkImagesFilled()){

            fillProfile3Next.visibility = View.GONE
            fillProfile3Progress.visibility = View.VISIBLE

            presenter.nextClicked(images.filterNotNull().toList(), imagesUri.filterNotNull().toList())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        val uri: Uri? = when (requestCode) {
            IMAGE_PICKER_RC -> data?.data
            else -> data?.extras?.get("data") as Uri? ?: FileUtils.getOutputFileUri(context!!)
        }

        assignPhoto(uri)
    }

    private fun assignPhoto(uri: Uri?){
        if(uri != null){
            imagesUri[photoSelected] = uri
            images[photoSelected] = uri.toBytes(context!!)

            when(photoSelected){
                0 -> form3Img1.loadImage(uri, roundedCornersRadiusPx = context!!.dimen(R.dimen.value_12dp),placeholder = R.color.white)
                1 -> form3Img2.loadImage(uri, roundedCornersRadiusPx = context!!.dimen(R.dimen.value_12dp),placeholder = R.color.white)
                2 -> form3Img3.loadImage(uri, roundedCornersRadiusPx = context!!.dimen(R.dimen.value_12dp),placeholder = R.color.white)
            }

            checkEnabledAndMissing()
        }
    }

    private fun checkEnabledAndMissing(){
        fillProfile3Next.isEnabled = checkImagesFilled()

        form3PhotoMissing.visibility = if(images.filterNotNull().size == 2) View.VISIBLE else View.GONE
    }

    private fun checkImagesFilled() = images.filterNotNull().size == 3

    private fun getModel() = arguments?.getParcelable(EXTRA_MODEL) as ProfileInfo

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            FileUtils.startImagePicker(activity!!)
        }
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) { }

    private fun openPhotoPicker() {
        if (PermissionsManager.areCameraPermissionsGranted(context)) {
            FileUtils.startImagePicker(activity!!)
        } else {
            permissionsManager.requestCameraPermissions(activity)
        }
    }

    // Commented because we're not saving images on device right now
    override fun onStop() {

//        val profileInfo = presenter.info

//        if(images.isNotEmpty()){
//            profileInfo.images = images.filterNotNull().toList()
//        }
//
//        if(imagesUri.isNotEmpty()){
//            profileInfo.imagesUri = imagesUri.filterNotNull().toList()
//        }
//
//        presenter.saveState(profileInfo, 3)

        super.onStop()
    }
}

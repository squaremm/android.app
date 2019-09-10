package com.square.android.ui.fragment.reviewUpload

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.mapbox.android.core.permissions.PermissionsListener
import com.square.android.R
import com.square.android.data.pojo.*
import com.square.android.extensions.loadImage
import com.square.android.ui.fragment.BaseNoMvpFragment
import com.square.android.utils.FileUtils
import com.square.android.utils.PermissionsManager
import kotlinx.android.synthetic.main.fragment_review_upload.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ReviewPhotoEvent(val data: Uri?)

class PhotoResultEvent(val data: Uri)

class ReviewUploadFragment(private var actionType: String): BaseNoMvpFragment(), PermissionsListener {

    private var imageUri: Uri? = null

    private val permissionsManager by lazy {
        PermissionsManager(this)
    }

    init {
        eventBus.register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPhotoResultEvent(event: PhotoResultEvent) {
        assignPhoto(event.data)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_review_upload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(actionType == TYPE_PICTURE){
            reviewDialogLabel.text = getString(R.string.upload_photo_label)
        } else{
            reviewDialogLabel.text = getString(R.string.upload_action_label)
        }

        updateLabel(true)

        uploadImv.setOnClickListener { if(imageUri == null) openPhotoPicker() else deleteImage() }
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            FileUtils.startImagePicker(activity!!)
        }
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) { }

    private fun openPhotoPicker() {
        if (PermissionsManager.areCameraPermissionsGranted(activity!!)) {
            FileUtils.startImagePicker(activity!!)
        } else {
            permissionsManager.requestCameraPermissions(activity!!)
        }
    }

    private fun assignPhoto(uri: Uri){
        updateLabel(false)

        imageUri = uri

        uploadImv.loadImage(imageUri!!, withoutCropping = true)

        uploadImv.background = ContextCompat.getDrawable(uploadImv.context, R.drawable.rounded_background_white)
        uploadImv.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(uploadImv.context, R.color.placeholder))

        eventBus.post(ReviewPhotoEvent(imageUri))
    }

    private fun deleteImage(){
        updateLabel(true)

        imageUri = null

        uploadImv.loadImage(R.drawable.upload_bg, fitOnly = true)

        uploadImv.background = ContextCompat.getDrawable(uploadImv.context, R.drawable.rounded_background_white)
        uploadImv.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(uploadImv.context, android.R.color.white))

        eventBus.post(ReviewPhotoEvent(null))
    }

    private fun updateLabel(show: Boolean){
        if(show){
            reviewDialogUploadLl.visibility = View.VISIBLE

            if(actionType == TYPE_PICTURE){
                reviewDialogUploadLabel.text = getString(R.string.upload_photo_gallery)
            } else{
                reviewDialogUploadLabel.text = getString(R.string.upload_action_prntscr)
            }
        } else{
            reviewDialogUploadLl.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        eventBus.unregister(this)
        super.onDestroy()
    }

}
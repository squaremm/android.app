package com.square.android.ui.fragment.sendPictureUpload

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.mapbox.android.core.permissions.PermissionsListener
import com.square.android.R
import com.square.android.extensions.loadImage
import com.square.android.extensions.toBytes
import com.square.android.presentation.presenter.sendPictureUpload.SendPictureUploadPresenter
import com.square.android.presentation.view.sendPictureUpload.SendPictureUploadView
import com.square.android.ui.activity.sendPicture.INDEX_EXTRA
import com.square.android.ui.activity.sendPicture.SendPictureActivity
import com.square.android.ui.activity.sendPicture.TYPE_EXTRA
import com.square.android.ui.fragment.BaseFragment
import com.square.android.utils.FileUtils
import com.square.android.utils.IMAGE_PICKER_RC
import com.square.android.utils.PermissionsManager
import kotlinx.android.synthetic.main.fragment_send_picture_upload.*
import org.jetbrains.anko.bundleOf

class SendPictureExtras(val index: Int, val type: Int)
class UploadPictureExtras(val index: Int, val photo: ByteArray)

class SendPictureUploadFragment: BaseFragment(), SendPictureUploadView, PermissionsListener {

    companion object {
        @Suppress("DEPRECATION")
        fun newInstance(index: Int, type: Int): SendPictureUploadFragment {
            val fragment = SendPictureUploadFragment()

            val args = bundleOf(INDEX_EXTRA to index, TYPE_EXTRA to type)
            fragment.arguments = args

            return fragment
        }
    }

    private val permissionsManager by lazy {
        PermissionsManager(this)
    }

    @InjectPresenter
    lateinit var presenter: SendPictureUploadPresenter

    @ProvidePresenter
    fun providePresenter(): SendPictureUploadPresenter = SendPictureUploadPresenter(getIndex(), getType())

    var imageUri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_send_picture_upload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as SendPictureActivity).lastFragment = false

        uploadImv.setOnClickListener { if(imageUri == null) openPhotoPicker() else deleteImage() }

        uploadSend.setOnClickListener { uploadPhoto() }
    }

    override fun changeLabel(type: Int) {
        (activity as SendPictureActivity).changeTitle(type)
    }

    private fun uploadPhoto(){
        imageUri?.let {
            it.toBytes(activity!!)?.let { byteArray ->
                presenter.uploadPhoto(byteArray)
            }
        }
    }

    private fun openPhotoPicker() {
        if (PermissionsManager.areCameraPermissionsGranted(activity!!)) {
            FileUtils.startImagePicker(activity!!)
        } else {
            permissionsManager.requestCameraPermissions(activity!!)
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

        uri?.let {assignPhoto(it)}
    }

    private fun assignPhoto(uri: Uri){
        imageUri = uri

        uploadImv.loadImage(imageUri!!, withoutCropping = true)

        uploadImv.background = ContextCompat.getDrawable(uploadImv.context, R.drawable.rounded_background_white)
        uploadImv.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(uploadImv.context, R.color.placeholder))

        uploadSend.isEnabled = true
    }

    private fun deleteImage(){
        imageUri = null

        uploadImv.loadImage(R.drawable.upload_photo_bg, fitOnly = true)

        uploadImv.background = ContextCompat.getDrawable(uploadImv.context, R.drawable.rounded_background_white)
        uploadImv.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(uploadImv.context, android.R.color.white))

        uploadSend.isEnabled = false
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            FileUtils.startImagePicker(activity!!)
        }
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) { }

    override fun hideProgress() {
        uploadProgress.visibility = View.GONE
        uploadSend.visibility = View.VISIBLE
    }

    override fun showProgress() {
        uploadSend.visibility = View.GONE
        uploadProgress.visibility = View.VISIBLE
    }

    private fun getIndex() = arguments?.getInt(INDEX_EXTRA, 0) ?: 0

    private fun getType() = arguments?.getInt(TYPE_EXTRA, 0) ?: 0
}
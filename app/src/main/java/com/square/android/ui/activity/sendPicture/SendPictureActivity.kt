package com.square.android.ui.activity.sendPicture

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.mapbox.android.core.permissions.PermissionsListener
import com.square.android.R
import com.square.android.extensions.loadImage
import com.square.android.extensions.toBytes
import com.square.android.presentation.presenter.sendPicture.SendPicturePresenter
import com.square.android.presentation.view.sendPicture.SendPictureView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import com.square.android.utils.FileUtils
import com.square.android.utils.IMAGE_PICKER_RC
import com.square.android.utils.PermissionsManager
import kotlinx.android.synthetic.main.activity_send_picture.*
import ru.terrakok.cicerone.Navigator

const val INDEX_EXTRA = "INDEX_EXTRA"

class SendPictureActivity: BaseActivity(), SendPictureView, PermissionsListener {

    @InjectPresenter
    lateinit var presenter: SendPicturePresenter

    @ProvidePresenter
    fun providePresenter() = SendPicturePresenter(getIndex())

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    private val permissionsManager by lazy {
        PermissionsManager(this)
    }

    var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_picture)

        uploadImv.setOnClickListener { if(imageUri == null) openPhotoPicker() else deleteImage() }

        uploadSend.setOnClickListener { uploadPhoto() }
    }

    private fun uploadPhoto(){
        imageUri?.let {
            it.toBytes(this)?.let { byteArray ->
                presenter.uploadPhoto(byteArray)
            }
        }
    }

    private fun openPhotoPicker() {
        if (PermissionsManager.areCameraPermissionsGranted(this)) {
            FileUtils.startImagePicker(this)
        } else {
            permissionsManager.requestCameraPermissions(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        val uri: Uri? = when (requestCode) {
            IMAGE_PICKER_RC -> data?.data
            else -> data?.extras?.get("data") as Uri? ?: FileUtils.getOutputFileUri(this)
        }

        uri?.let {assignPhoto(it)}
    }

    private fun assignPhoto(uri: Uri){
        imageUri = uri

        uploadImv.loadImage(imageUri!!, withoutCropping = true)

        uploadImv.background = getDrawable(R.drawable.rounded_background_white)
        uploadImv.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(uploadImv.context, R.color.placeholder))

        uploadSend.isEnabled = true
    }

    private fun deleteImage(){
        imageUri = null

        uploadImv.loadImage(R.drawable.upload_ss_bg, fitOnly = true)

        uploadImv.background = getDrawable(R.drawable.rounded_background_white)
        uploadImv.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(uploadImv.context, android.R.color.white))

        uploadSend.isEnabled = false
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            FileUtils.startImagePicker(this)
        }
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) { }

    override fun acExit() = onBackPressed()

    override fun hideProgress() {
        uploadProgress.visibility = View.GONE
        uploadSend.visibility = View.VISIBLE
    }

    override fun showProgress() {
        uploadSend.visibility = View.GONE
        uploadProgress.visibility = View.VISIBLE
    }

    private fun getIndex() = intent.getIntExtra(INDEX_EXTRA, 0)
}

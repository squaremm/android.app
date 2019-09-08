package com.square.android.ui.activity.uploadScreenshot

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
import com.square.android.presentation.presenter.uploadScreenshot.UploadScreenshotPresenter
import com.square.android.presentation.view.uploadScreenshot.UploadScreenshotView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.activity.sendPicture.INDEX_EXTRA
import com.square.android.ui.base.SimpleNavigator
import com.square.android.utils.FileUtils
import com.square.android.utils.IMAGE_PICKER_RC
import com.square.android.utils.PermissionsManager
import kotlinx.android.synthetic.main.activity_upload_screenshot.*
import ru.terrakok.cicerone.Navigator

class UploadScreenshotActivity: BaseActivity(), UploadScreenshotView, PermissionsListener {
    @InjectPresenter
    lateinit var presenter: UploadScreenshotPresenter

    @ProvidePresenter
    fun providePresenter() = UploadScreenshotPresenter(getIndex())

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    private val permissionsManager by lazy {
        PermissionsManager(this)
    }

    var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_screenshot)

        uploadScreenshotImv.setOnClickListener { if(imageUri == null) openPhotoPicker() else deleteImage() }

        uploadScreenshotSend.setOnClickListener { uploadPhoto() }
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

    private fun openPhotoPicker() {
        if (PermissionsManager.areCameraPermissionsGranted(this)) {
            FileUtils.startImagePicker(this)
        } else {
            permissionsManager.requestCameraPermissions(this)
        }
    }

    private fun uploadPhoto(){
        imageUri?.let {
            it.toBytes(this)?.let { byteArray ->
                presenter.uploadPhoto(byteArray)
            }
        }
    }

    private fun assignPhoto(uri: Uri){
        imageUri = uri

        uploadScreenshotImv.loadImage(imageUri!!, withoutCropping = true)

        uploadScreenshotImv.background = ContextCompat.getDrawable(uploadScreenshotImv.context, R.drawable.rounded_background_white)
        uploadScreenshotImv.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(uploadScreenshotImv.context, R.color.placeholder))

        uploadScreenshotSend.isEnabled = true
    }

    private fun deleteImage(){
        imageUri = null

        uploadScreenshotImv.loadImage(R.drawable.upload_screenshot_bg, fitOnly = true)

        uploadScreenshotImv.background = ContextCompat.getDrawable(uploadScreenshotImv.context, R.drawable.rounded_background_white)
        uploadScreenshotImv.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(uploadScreenshotImv.context, android.R.color.white))

        uploadScreenshotSend.isEnabled = false
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            FileUtils.startImagePicker(this)
        }
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) { }

    override fun showProgress() {
        uploadScreenshotSend.visibility = View.GONE
        uploadScreenshotProgress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        uploadScreenshotProgress.visibility = View.GONE
        uploadScreenshotSend.visibility = View.VISIBLE
    }

    private fun getIndex() = intent.getIntExtra(INDEX_EXTRA, 0)

    override fun goBack() {
        onBackPressed()
    }
}
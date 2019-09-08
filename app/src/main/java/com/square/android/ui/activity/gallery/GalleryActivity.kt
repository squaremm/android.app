package com.square.android.ui.activity.gallery

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.mapbox.android.core.permissions.PermissionsListener
import com.square.android.R
import com.square.android.data.pojo.Photo
import com.square.android.data.pojo.Profile
import com.square.android.extensions.toBytes
import com.square.android.presentation.presenter.gallery.GalleryPresenter
import com.square.android.presentation.view.gallery.GalleryView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.base.SimpleNavigator
import com.square.android.utils.FileUtils
import com.square.android.utils.IMAGE_PICKER_RC
import com.square.android.utils.PermissionsManager
import kotlinx.android.synthetic.main.activity_gallery.*
import ru.terrakok.cicerone.Navigator

const val USER_EXTRA = "GalleryActivity:USER_EXTRA"

class GalleryActivity: BaseActivity(), GalleryView, GalleryAdapter.Handler, PermissionsListener {

    @InjectPresenter
    lateinit var presenter: GalleryPresenter

    @ProvidePresenter
    fun providePresenter() = GalleryPresenter(intent.getParcelableExtra(USER_EXTRA))

    override fun provideNavigator(): Navigator = object : SimpleNavigator {}

    val  permissionsManager by lazy {
        PermissionsManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        setSupportActionBar(galleryToolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun showProgress() {
        progress.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress.visibility = View.GONE
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {

    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            FileUtils.startImagePicker(this)
        }
    }


    override fun showData(user: Profile.User) {
        user.images?.run {
            rv_photo.adapter = GalleryAdapter(this + Photo(), this@GalleryActivity)
        }
    }

    override fun openPhotoPicker() {
        if (PermissionsManager.areCameraPermissionsGranted(this)) {
            FileUtils.startImagePicker(this)
        } else {
            permissionsManager.requestCameraPermissions(this)
        }
    }

    override fun itemClicked(item: Photo) = presenter.setPhotoAsMain(item)


    override fun deleteClicked(item: Photo) {
        presenter.removePhoto(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.e("LOL", "LOL" + data?.extras?.get("data") )
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            IMAGE_PICKER_RC -> data?.data
            else -> data?.extras?.get("data") as Uri? ?: FileUtils.getOutputFileUri(this)
        }?.toBytes(this)?.run(presenter::addPhoto)
    }

    override fun launchPhotoPicker() {
        openPhotoPicker()
    }
}
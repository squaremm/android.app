package com.square.android.presentation.presenter.gallery

import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import com.square.android.data.network.PhotoId
import com.square.android.data.pojo.Images
import com.square.android.data.pojo.Photo
import com.square.android.data.pojo.Profile
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.gallery.GalleryView

@InjectViewState
class GalleryPresenter(var user: Profile.User) : BasePresenter<GalleryView>() {

    init {
        viewState.showData(user)
    }

    fun removePhoto(photo: Photo) = launch {
        viewState.showProgress()
        val repsonse = repository.removePhoto(user.id, PhotoId(photo.id)).await()
        updateUser()
        viewState.hideProgress()
    }

    fun setPhotoAsMain(photo: Photo) = launch {
        viewState.showProgress()
        val repsonse = repository.setPhotoAsMain(user.id, photo.id).await()
        updateUser()
        viewState.hideProgress()
    }

    fun addPhoto(bytes: ByteArray) = launch {
        viewState.showProgress()
        val repsonse = repository.addPhoto(user.id, bytes).await()
        updateUser()
        viewState.hideProgress()
    }

    fun updateUser() = launch {
        viewState.showProgress()
        user = repository.getCurrentUser().await()
        viewState.showData(user)
        viewState.hideProgress()
    }
}

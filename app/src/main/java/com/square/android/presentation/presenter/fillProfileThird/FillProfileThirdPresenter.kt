package com.square.android.presentation.presenter.fillProfileThird

import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.ProfileInfo
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.fillProfileThird.FillProfileThirdView

@InjectViewState
class FillProfileThirdPresenter(val info: ProfileInfo) : BasePresenter<FillProfileThirdView>() {

    init {
        viewState.showData(info)
    }

    fun nextClicked(photos: List<ByteArray>, photosUri: List<Uri>) {
        // crashing - out of memory
//        info.imagesUri = photosUri

        info.images = photos
        router.navigateTo(SCREENS.FILL_PROFILE_REFERRAL, info)
    }
}
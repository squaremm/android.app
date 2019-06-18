package com.square.android.presentation.presenter.addPhoto

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.Campaign
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.campaignDetails.PhotosAddedEvent
import com.square.android.presentation.view.addPhoto.AddPhotoView
import org.greenrobot.eventbus.EventBus
import org.koin.standalone.inject

@InjectViewState
class AddPhotoPresenter(var campaign: Campaign): BasePresenter<AddPhotoView>() {

    private val eventBus: EventBus by inject()

    fun uploadPhotos(bytes: List<ByteArray>) = launch {
            viewState.showProgress()

            for(photo in bytes){
                repository.addCampaignImage(campaign.id, photo).await()
            }

            eventBus.post(PhotosAddedEvent())

            router.backTo(SCREENS.UPLOAD_PICS)
        }
}
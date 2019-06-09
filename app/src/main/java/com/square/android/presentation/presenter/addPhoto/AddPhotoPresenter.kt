package com.square.android.presentation.presenter.addPhoto

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.Participation
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.presenter.participationDetails.PhotosAddedEvent
import com.square.android.presentation.view.addPhoto.AddPhotoView
import org.greenrobot.eventbus.EventBus
import org.koin.standalone.inject

@InjectViewState
class AddPhotoPresenter(var participation: Participation): BasePresenter<AddPhotoView>() {

    private val eventBus: EventBus by inject()

    fun uploadPhotos(bytes: List<ByteArray>) = launch {
            viewState.showProgress()

            //TODO uncomment later
//            for(photo in bytes){
//                repository.addParticipationPhoto(participation.id, photo).await()
//            }

            eventBus.post(PhotosAddedEvent())

            router.backTo(SCREENS.UPLOAD_PICS)
        }

}
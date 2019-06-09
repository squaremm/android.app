package com.square.android.presentation.presenter.participationDetails

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.Participation
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.participationDetails.ParticipationDetailsView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject

class PhotosAddedEvent()

@InjectViewState
class ParticipationDetailsPresenter(val participationId: Long): BasePresenter<ParticipationDetailsView>(){

    var data: Participation? = null

    private val eventBus: EventBus by inject()

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPhotosAdded(event: PhotosAddedEvent) {
        loadData()
    }

    init {
        eventBus.register(this)

        loadData()
    }

    private fun loadData() = launch {
            viewState.showProgress()

            //TODO uncomment later
//            data = repository.getParticipation(participationId).await()
//
//            viewState.showData(data!!)
//                if (data!!.active) {
//                    router.replaceScreen(SCREENS.UPLOAD_PICS, data)
//                } else {
//                    router.replaceScreen(SCREENS.APPROVAL, data)
//                }
//
//            viewState.hideProgress()
        }

    fun exit(){
        router.exit()
    }

    fun replaceToApproval(){
        data?.let {router.replaceScreen(SCREENS.APPROVAL, it)
        } ?: exit()
    }

    fun navigateToAddPhoto() = data?.let { router.navigateTo(SCREENS.ADD_PHOTO, it) }

    override fun onDestroy() {
        super.onDestroy()

        eventBus.unregister(this)
    }
}

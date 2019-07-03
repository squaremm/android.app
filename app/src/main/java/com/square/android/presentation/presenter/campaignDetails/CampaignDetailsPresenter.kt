package com.square.android.presentation.presenter.campaignDetails

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.Campaign
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.campaignDetails.CampaignDetailsView
import com.square.android.ui.activity.pickupMap.PickUpMapExtras
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject

class PhotosAddedEvent()

@InjectViewState
class CampaignDetailsPresenter(val campaignId: Long): BasePresenter<CampaignDetailsView>(){

    var data: Campaign? = null

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

        data = repository.getCampaign(campaignId).await()

        viewState.showData(data!!)

        //TODO change this -> ?this is wrong? also PickUpSpotFragment is not included here
        if(data!!.status == 0){
            router.replaceScreen(SCREENS.NOT_APPROVED, data)
        } else {
            val locationWrappers = repository.getCampaignLocations(data!!.id).await()

            if (!locationWrappers.isNullOrEmpty() && data!!.isGiftTaken == false) {
                router.replaceScreen(SCREENS.PICK_UP_SPOT, data)
            } else if (data!!.isPictureUploadAllow == true && data!!.imageCount != data!!.images?.size) {
                router.replaceScreen(SCREENS.UPLOAD_PICS, data)
            } else {
                router.replaceScreen(SCREENS.APPROVAL, data)
            }
        }

        viewState.hideProgress()
    }

    fun exit(){
        router.exit()
    }

    fun replaceToApproval(){
        data?.let { router.replaceScreen(SCREENS.APPROVAL, it) } ?: exit()
    }

    fun navigateToAddPhoto() = data?.let { router.navigateTo(SCREENS.ADD_PHOTO, it) }

    override fun onDestroy() {
        super.onDestroy()

        eventBus.unregister(this)
    }
}

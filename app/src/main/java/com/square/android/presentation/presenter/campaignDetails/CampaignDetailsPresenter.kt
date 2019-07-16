package com.square.android.presentation.presenter.campaignDetails

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.Campaign
import com.square.android.data.pojo.CampaignBooking
import com.square.android.data.pojo.CampaignInterval
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.campaignDetails.CampaignDetailsView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.standalone.inject

class PhotosAddedEvent()

class CampaignBookEvent()

class ScanQrEvent()

@InjectViewState
class CampaignDetailsPresenter(val campaignId: Long): BasePresenter<CampaignDetailsView>(){

    var data: Campaign? = null

    private val eventBus: EventBus by inject()

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPhotosAdded(event: PhotosAddedEvent) {
        loadData()
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCampaignBook(event: CampaignBookEvent) {
        loadData()
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onScanQrEvent(event: ScanQrEvent) {
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

        if(data!!.status == 0){
            router.replaceScreen(SCREENS.NOT_APPROVED, data)
        } else {
            val locationWrappers = repository.getCampaignLocations(data!!.id).await()

            val userBookings: List<CampaignBooking>? = repository.getCampaignBookings().await()
            var campaignLocation: CampaignInterval.Location? = null

            if(!userBookings.isNullOrEmpty()){
                if(userBookings.firstOrNull { it.campaignId == data!!.id } != null){
                    if(data!!.location != null){
                        if(data!!.location!!.coordinates != null){
                            campaignLocation = data!!.location
                        }
                    }
                }
            }

            val count1 = data!!.imageCount ?: 0
            val count2 = data!!.tasks?.filter { it.type == "photo" }?.sumBy { it.count } ?: 0

            if(data!!.isGiftTaken == false && campaignLocation != null){
                router.replaceScreen(SCREENS.PICK_UP_LOCATION, campaignLocation)
            } else if (!locationWrappers.isNullOrEmpty() && data!!.isGiftTaken == false) {
                router.replaceScreen(SCREENS.PICK_UP_SPOT, data!!.id)
            } else if (data!!.isPictureUploadAllow == true
                    && (((count1 != data!!.images?.size && count1 > 0) || (count2 == data!!.images?.size && count2 > 0)))
                    && data!!.isGiftTaken == true) {
                router.replaceScreen(SCREENS.UPLOAD_PICS, data)
            } else if (data!!.status!! >= 2) {
                router.replaceScreen(SCREENS.APPROVAL, data)
            } else {
                router.replaceScreen(SCREENS.NOT_APPROVED, data)
            }
        }

        viewState.hideProgress()
    }

    fun exit(){
        router.exit()
    }

    fun replaceToApproval() {
        loadData()
    }

    fun navigateToAddPhoto() = data?.let { router.navigateTo(SCREENS.ADD_PHOTO, it) }

    fun navigateToQr() = router.navigateTo(SCREENS.SCAN_QR)

    override fun onDestroy() {
        super.onDestroy()

        eventBus.unregister(this)
    }
}

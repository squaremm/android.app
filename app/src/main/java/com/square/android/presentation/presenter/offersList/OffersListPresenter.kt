package com.square.android.presentation.presenter.offersList

import com.arellomobile.mvp.InjectViewState
import com.square.android.data.pojo.OfferInfo
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.offersList.OffersListView

@InjectViewState
class OffersListPresenter(var offers: List<OfferInfo>) : BasePresenter<OffersListView>(){

 //TODO here and in OffersListFragment -> see Upload pics and other fragments that are replaced in container

}

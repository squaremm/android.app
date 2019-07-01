package com.square.android.presentation.presenter.selectOffer

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.selectOffer.SelectOfferView

@InjectViewState
class SelectOfferPresenter(redemptionId: Long) : BasePresenter<SelectOfferView>() {

    init {
        router.replaceScreen(SCREENS.OFFERS_LIST, redemptionId)
    }
}

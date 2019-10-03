package com.square.android.presentation.presenter.fillProfileSecond

import com.arellomobile.mvp.InjectViewState
import com.square.android.SCREENS
import com.square.android.data.pojo.ProfileInfo
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.fillProfileSecond.FillProfileSecondView

@InjectViewState
class FillProfileSecondPresenter(val info: ProfileInfo) : BasePresenter<FillProfileSecondView>() {

    var cities: MutableList<String> = mutableListOf()

    init {
        loadData()
    }

    private fun loadData(){
        //TODO load cities from API
        cities.add("Milan")
        cities.add("London")
        cities.add("Budapest")

        cities.add(0,"")

        viewState.showData(info)
    }

    fun nextClicked(motherAgency: String, city1: String, agency1: String, city2: String, agency2: String, city3: String, agency3: String) {
        info.motherAgency = motherAgency
        info.city1 = city1
        info.agency1 = agency1
        info.city2 = city2
        info.agency2 = agency2
        info.city3 = city3
        info.agency3 = agency3

        router.navigateTo(SCREENS.FILL_PROFILE_THIRD, info)
    }
}

package com.square.android.presentation.presenter.start

import com.arellomobile.mvp.InjectViewState
import com.google.gson.Gson
import com.square.android.SCREENS
import com.square.android.data.pojo.ProfileInfo
import com.square.android.presentation.presenter.BasePresenter
import com.square.android.presentation.view.start.StartView
import java.lang.Exception

@InjectViewState
class StartPresenter : BasePresenter<StartView>() {

    init {
        if(repository.shouldDisplayIntro()){
            router.replaceScreen(SCREENS.INTRO)
        } else if(!repository.isLoggedIn() && repository.getFragmentNumber() <= 0){
            router.replaceScreen(SCREENS.AUTH)
        } else if(repository.isProfileFilled()) {
            router.replaceScreen(SCREENS.MAIN)
        } else{

            val fragmentNumber = repository.getFragmentNumber()

            val gson = Gson()

            var profileInfo = ProfileInfo()

            try {
                profileInfo = gson.fromJson(repository.getProfileInfo(), ProfileInfo::class.java)
            } catch (e: Exception){}

            when(fragmentNumber){
                1 -> {
                    router.replaceScreen(SCREENS.FILL_PROFILE_FIRST, profileInfo)
                }
                2 -> {
                    router.replaceScreen(SCREENS.FILL_PROFILE_FIRST, profileInfo)
                    router.navigateTo( SCREENS.FILL_PROFILE_SECOND, profileInfo)
                   }
                3 -> {
                    router.replaceScreen(SCREENS.FILL_PROFILE_FIRST, profileInfo)
                    router.navigateTo( SCREENS.FILL_PROFILE_SECOND, profileInfo)
                    router.navigateTo( SCREENS.FILL_PROFILE_THIRD, profileInfo)
                   }
                4 -> {
                    router.replaceScreen(SCREENS.FILL_PROFILE_FIRST, profileInfo)
                    router.navigateTo( SCREENS.FILL_PROFILE_SECOND, profileInfo)
                    router.navigateTo( SCREENS.FILL_PROFILE_THIRD, profileInfo)
                    router.navigateTo( SCREENS.FILL_PROFILE_REFERRAL, profileInfo)
                }
                else -> router.replaceScreen(SCREENS.FILL_PROFILE_FIRST,profileInfo)
            }
        }
    }
}

package com.square.android.ui.activity.sendPicture

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.square.android.R
import com.square.android.SCREENS
import com.square.android.androidx.navigator.AppNavigator
import com.square.android.presentation.presenter.sendPicture.SendPicturePresenter
import com.square.android.presentation.view.sendPicture.SendPictureView
import com.square.android.ui.activity.BaseActivity
import com.square.android.ui.fragment.sendPictureChoose.SendPictureChooseFragment
import com.square.android.ui.fragment.sendPictureUpload.SendPictureExtras
import com.square.android.ui.fragment.sendPictureUpload.SendPictureUploadFragment
import kotlinx.android.synthetic.main.activity_send_picture.*
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward

const val INDEX_EXTRA = "INDEX_EXTRA"
const val TYPE_EXTRA = "TYPE_EXTRA"

class SendPictureActivity: BaseActivity(), SendPictureView {

    @InjectPresenter
    lateinit var presenter: SendPicturePresenter

    @ProvidePresenter
    fun providePresenter() = SendPicturePresenter(getIndex())

    var lastFragment = false

    override fun provideNavigator(): Navigator = SendPictureNavigator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_picture)
    }

    fun changeTitle(type: Int){
        when(type){
            0 ->{
                sendPictureLabel.isAllCaps = true
                sendPictureLabel.text = getString(R.string.send_picture)
            }

            1 ->{
                sendPictureLabel.isAllCaps = false
                sendPictureLabel.text = getString(R.string.food_picture)
            }

            2 ->{
                sendPictureLabel.isAllCaps = false
                sendPictureLabel.text = getString(R.string.atmosphere)
            }

            3 ->{
                sendPictureLabel.isAllCaps = false
                sendPictureLabel.text = getString(R.string.model_in_venue)
            }

            4 ->{
                sendPictureLabel.isAllCaps = false
                sendPictureLabel.text = getString(R.string.still_life)
            }
        }
    }

    private class SendPictureNavigator(activity: FragmentActivity) : AppNavigator(activity, R.id.sendPictureContainer) {

        override fun createActivityIntent(context: Context, screenKey: String, data: Any?) = null

        override fun createFragment(screenKey: String, data: Any?) = when (screenKey) {

            SCREENS.SEND_PICTURE_CHOOSE -> SendPictureChooseFragment.newInstance(data as Int)

            SCREENS.SEND_PICTURE_UPLOAD ->{
                val extras = data as SendPictureExtras
                SendPictureUploadFragment.newInstance(extras.index, extras.type)
            }

            else -> throw IllegalArgumentException("Unknown screen key: $screenKey")
        }

        override fun setupFragmentTransactionAnimation(command: Command,
                                                       currentFragment: Fragment?,
                                                       nextFragment: Fragment,
                                                       fragmentTransaction: FragmentTransaction) {

            if(command is Forward){
                fragmentTransaction.setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right)
            } else{
                fragmentTransaction.setCustomAnimations(R.anim.fade_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.fade_out)
            }

        }
    }

    override fun onBackPressed() {
        if(lastFragment){
            presenter.finishChain()
        } else{
            super.onBackPressed()
        }
    }

    private fun getIndex() = intent.getIntExtra(INDEX_EXTRA, 0)
}

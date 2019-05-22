package com.square.android.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.square.android.extensions.onTextChanged
import com.square.android.presentation.view.BaseView
import com.square.android.ui.base.tutorial.TutorialService
import com.square.android.utils.ValidationCallback
import org.jetbrains.anko.contentView
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import android.os.Build
import android.provider.Settings
import com.square.android.data.local.LocalDataManager
import com.square.android.ui.base.tutorial.Tutorial
import com.square.android.ui.base.tutorial.TutorialLoadedEvent
import org.greenrobot.eventbus.EventBus
import java.util.*

abstract class BaseActivity : MvpActivity(), BaseView {
    private val navigatorHolder: NavigatorHolder by inject()

    private var navigator: Navigator? = null

    private var waitAttempts: Int = 0

    open val tutorial: Tutorial? = null

    open val PERMISSION_REQUEST_CODE: Int? = null

    private var localDataManager: LocalDataManager? = null

    private val eventBus: EventBus by inject()

    override fun showMessage(message: String) {
        contentView?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT)
                    .show()
        }
    }

    private fun checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {

                startPermissionForResult()
            } else {
                startTutorialService()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    startTutorialService()
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun startPermissionForResult(requestCode: Int = -1){
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName"))

        if(requestCode == -1){
            PERMISSION_REQUEST_CODE?.let { startActivityForResult(intent, it)}
        } else{
            startActivityForResult(intent, requestCode)
        }
    }

    private fun startTutorialService(){
        val intent = Intent(Intent(this,TutorialService::class.java))
        intent.putExtra(TutorialService.TUTORIAL_APP_EXTRA_KEY, tutorial?.tutorialKey?.name)
        startService(intent)

        waitAttempts = 0
        waitForSubscriber()
    }

    private fun waitForSubscriber(){
        if (eventBus.hasSubscriberForEvent(TutorialLoadedEvent::class.java)){
            eventBus.post(TutorialLoadedEvent(tutorial))
        } else{
            if(waitAttempts < 10){
                waitAttempts++
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        waitForSubscriber()
                    }
                }, 50)
            }
        }
    }

    override fun showMessage(messageRes: Int) {
        showMessage(getString(messageRes))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navigator = provideNavigator()

        localDataManager = LocalDataManager(this)

        if(tutorial?.tutorialKey != null){

            //TODO uncomment later
//            if(!localDataManager!!.getTutorialDontShowAgain(tutorialName!!)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkDrawOverlayPermission()
                } else{
                    startTutorialService()
                }
//            }
            //TODO uncomment later
        }
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    @SuppressLint("ResourceType")
    protected fun calculateFadeHeight(): Int {
        val array = theme.obtainStyledAttributes(
                arrayOf(android.R.attr.actionBarSize).toIntArray()
        )

        val actionBarSize = array.getDimension(0, 0.0f).toInt()
        var statusBarSize = 0

        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarSize = resources.getDimensionPixelSize(resourceId)
        }

        return actionBarSize + statusBarSize
    }

    protected fun addTextValidation(views: List<TextView>, callback: ValidationCallback<CharSequence>) {
        views.forEach {
            it.onTextChanged {
                updateValidity(views, callback)
            }
        }

        updateValidity(views, callback)
    }

    private fun updateValidity(views: List<TextView>, callback: ValidationCallback<CharSequence>) {
        val isValid = views.all { callback.isValid(it.text) }

        callback.validityChanged(isValid)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                android.R.id.home -> {
                    onBackPressed()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    protected abstract fun provideNavigator(): Navigator

}
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

abstract class BaseActivity(var tutorialName: String = "") : MvpActivity(), BaseView {
    private val navigatorHolder: NavigatorHolder by inject()

    private var navigator: Navigator? = null

    private val PERMISSION_REQUEST_CODE = 1338

    override fun showMessage(message: String) {
        contentView?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT)
                    .show()
        }
    }

    fun checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {

                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName"))

                startActivityForResult(intent, PERMISSION_REQUEST_CODE)
            } else {
                startTutorialService()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    startTutorialService()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun startTutorialService(){
        val intent = Intent(Intent(this,TutorialService::class.java))
        intent.putExtra(TutorialService.TUTORIAL_APP_EXTRA_KEY, tutorialName)

        startService(intent)
    }

    override fun showMessage(messageRes: Int) {
        showMessage(getString(messageRes))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navigator = provideNavigator()

        if(!TextUtils.isEmpty(tutorialName)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkDrawOverlayPermission()
            } else{
                startTutorialService()
            }
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
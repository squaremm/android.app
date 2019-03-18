package com.square.android.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.square.android.extensions.onTextChanged
import com.square.android.presentation.view.BaseView
import com.square.android.utils.ValidationCallback
import org.jetbrains.anko.contentView
import org.koin.android.ext.android.inject
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder


abstract class BaseActivity : MvpActivity(), BaseView {
    private val navigatorHolder: NavigatorHolder by inject()

    private var navigator: Navigator? = null

    override fun showMessage(message: String) {
        contentView?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT)
                    .show()
        }
    }

    override fun showMessage(messageRes: Int) {
        showMessage(getString(messageRes))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        navigator = provideNavigator()
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
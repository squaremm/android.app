package com.square.android.ui.fragment

import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.square.android.extensions.onTextChanged
import com.square.android.presentation.view.BaseView
import com.square.android.utils.ValidationCallback

abstract class BaseFragment : MvpFragment(), BaseView {
    private var snackBar: Snackbar? = null

    override fun showMessage(message: String) {
        view?.let {
            snackBar = Snackbar.make(it, message, Snackbar.LENGTH_LONG)

            snackBar?.show()
        }
    }

    @JvmName("addTextViewValidation")
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

    override fun showMessage(messageRes: Int) {
        showMessage(getString(messageRes))
    }

    override fun onPause() {
        super.onPause()

        snackBar?.dismiss()
    }
}
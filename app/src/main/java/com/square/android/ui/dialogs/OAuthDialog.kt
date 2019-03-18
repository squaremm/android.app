package com.square.android.ui.dialogs

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AlertDialog
import android.webkit.WebView
import android.webkit.WebViewClient
import com.square.android.R


@Suppress("OverridingDeprecatedMember")
@SuppressLint("SetJavaScriptEnabled")
class OAuthDialog(private val address: String, private val trigger: String) {
    private var dialog: AlertDialog? = null

    fun show(context: Context,
             callback: (String) -> Unit,
             onCancelListener: () -> Unit = {}) {
        val webView = object : WebView(context) {
            override fun onCheckIsTextEditor() = true
        }


        webView.settings.javaScriptEnabled = true

        webView.loadUrl(address)

        dialog = AlertDialog.Builder(context)
                .setCancelable(true)
                .setView(webView)
                .setOnCancelListener {
                    it.dismiss()

                    onCancelListener.invoke()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()

                    onCancelListener.invoke()
                }
                .show()

        webView.webViewClient = AuthWebClient(trigger) {
            callback.invoke(it)

            dialog?.dismiss()
        }
    }

    fun dismiss() {
        dialog?.dismiss()
    }

    private class AuthWebClient(trigger: String,
                                private val callback: (String) -> Unit) : WebViewClient() {
        private val triggerRegex = "$trigger=(.+)&?".toRegex() // code=xxx[&]

        private val trigger = "$trigger="

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            if (url.contains(trigger)) {
                val code = triggerRegex.find(url)?.groupValues?.getOrNull(1)

                if (code != null) {
                    view.stopLoading()

                    callback.invoke(code)
                }
            }

            return false
        }
    }
}
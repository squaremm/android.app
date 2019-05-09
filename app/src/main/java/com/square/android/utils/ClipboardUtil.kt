package com.square.android.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

object ClipboardUtil {

    fun copyText(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText("review", text)
        clipboard!!.primaryClip = clip
    }
}
package com.square.android.utils.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.square.android.R

class ErrorEditText(context: Context, attributeSet: AttributeSet): EditText(context, attributeSet){

    var errorShowing: Boolean = false
    var normalHint: String = ""
    var normalHintTextColor: Int = 0

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)

        if(errorShowing){
            errorShowing = false
            setHintTextColor(ContextCompat.getColor(context, normalHintTextColor))
            hint = normalHint
        }
    }

    fun showCustomError(errorText: String, errorTextColor: Int = R.color.nice_red, normalText: String = if(hint == null) "" else hint.toString(), normalTextColor: Int = R.color.grey_dark){

        normalHint = normalText
        normalHintTextColor = normalTextColor

        setHintTextColor(ContextCompat.getColor(context, errorTextColor))
        hint = errorText
        errorShowing = true
    }
}

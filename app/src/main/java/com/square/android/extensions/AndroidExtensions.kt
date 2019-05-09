package com.square.android.extensions

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.location.Location
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.webkit.URLUtil
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.square.android.App
import com.square.android.R
import com.square.android.R.color.placeholder
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.io.ByteArrayOutputStream
import java.io.IOException


private const val PREFIX_METER = "m"
private const val PREFIX_KILOMETER = "km"

fun ImageView.loadImage(url: String,
                        @ColorRes placeholder: Int = R.color.placeholder,
                        roundedCornersRadiusPx: Int = 0,
                        whichCornersToRound: RoundedCornersTransformation.CornerType = RoundedCornersTransformation.CornerType.ALL) {
    if (URLUtil.isValidUrl(url)) {
        Picasso.get()
                .load(url)
                .fit()
                .centerCrop()
                .transform(RoundedCornersTransformation(roundedCornersRadiusPx, 0, whichCornersToRound))
                .placeholder(placeholder)
                .into(this)
    }
}

fun ImageView.loadImage(@DrawableRes drawableRes: Int,
                        withoutCropping: Boolean = false,
                        roundedCornersRadiusPx: Int = 0,
                        whichCornersToRound: RoundedCornersTransformation.CornerType = RoundedCornersTransformation.CornerType.ALL) {
    val creator = Picasso.get()
            .load(drawableRes)
            .transform(RoundedCornersTransformation(roundedCornersRadiusPx, 0, whichCornersToRound))
            .fit()

    if (!withoutCropping) creator.centerCrop()

    creator.placeholder(R.color.white)
            .into(this)
}

fun ImageView.loadImageCenterInside(url: String,
                                    @ColorRes placeholder: Int = R.color.placeholder,
                                    roundedCornersRadiusPx: Int = 0,
                                    whichCornersToRound: RoundedCornersTransformation.CornerType = RoundedCornersTransformation.CornerType.ALL) {
    Picasso.get()
            .load(url)
            .fit()
            .centerInside()
            .transform(RoundedCornersTransformation(roundedCornersRadiusPx, 0, whichCornersToRound))
            .placeholder(placeholder)
            .into(this)
}


inline fun View.doOnPreDraw(crossinline listener: (View) -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            listener.invoke(this@doOnPreDraw)
        }
    })
}

fun ImageView.loadFirstOrPlaceholder(photos: List<String>?) {
    if (photos?.isEmpty() == true) {
        setImageResource(R.color.placeholder)
    } else {
        photos?.run {
            loadImage(first())
        }
    }
}

fun ImageView.loadImageInside(@DrawableRes drawableRes: Int) {
    Picasso.get()
            .load(drawableRes)
            .fit()
            .centerInside()
            .placeholder(placeholder)
            .into(this)
}

fun TextView.setStartDrawable(@DrawableRes drawableRes: Int?) {
    val drawable = if (drawableRes != null) context.getDrawable(drawableRes) else null

    setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
}

fun TextView.clearText() {
    text = ""
}

fun ImageView.makeBlackWhite() {
    val matrix = ColorMatrix()
    matrix.setSaturation(0f)

    val filter = ColorMatrixColorFilter(matrix)
    colorFilter = filter
}

fun ImageView.removeFilters() {
    colorFilter = null
}

val TextView.content
    get() = this.text.toString()

fun Intent.withClearingStack(): Intent {
    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    return this
}

var EditText.content: String
    get() = this.text.toString()
    set(value) = setText(value, TextView.BufferType.EDITABLE)

fun Activity.hideKeyboard() {
    val view = currentFocus
    view?.hideKeyboard()
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun TextView.setTextCarryingEmpty(content: String?) {
    text = content
    visibility = if (content.isNullOrEmpty()) View.GONE else View.VISIBLE
}

fun Context.copyToClipboard(text: String) {
    val clipManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(null, text)

    clipManager.primaryClip = clipData
}

fun TextView.onTextChanged(block: (CharSequence) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            block.invoke(s)
        }
    })
}

fun Context.getBitmap(drawableRes: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(this, drawableRes)!!
    val canvas = Canvas()
    val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    canvas.setBitmap(bitmap)
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    drawable.draw(canvas)

    return bitmap
}

@Suppress("ConstantConditionIf", "DEPRECATION")
fun TextView.setHtml(string: String) {
    val html = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(string)
    }

    text = html
}

fun TextView.setTextColorRes(@ColorRes colorRes: Int) {
    val color = App.getColor(colorRes)

    setTextColor(color)
}

fun Int?.asDistance(): String {
    if (this == null) return ""

    val truncated = toInt()

    return when (truncated) {
        in 1..999 -> truncated.toString() + PREFIX_METER
        else -> (truncated / 1000).toString() + PREFIX_KILOMETER
    }
}

fun Location.distanceTo(location: com.square.android.data.pojo.Location): Float {
    val temp = Location("Temp")

    val latLng = location.latLng()

    temp.latitude = latLng.latitude
    temp.longitude = latLng.longitude

    return distanceTo(temp)
}

fun Uri.toBytes(context: Context): ByteArray? {
    val inputStream = context.contentResolver.openInputStream(this)?.run {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)

        var len = read(buffer)
        while (len != -1) {
            byteBuffer.write(buffer, 0, len)
            len = read(buffer)
        }
        return byteBuffer.toByteArray()
    }

    return null
}
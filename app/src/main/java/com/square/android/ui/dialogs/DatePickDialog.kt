package com.square.android.ui.dialogs

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import com.square.android.R
import com.square.android.extensions.toOrdinalString
import java.util.*

class DatePickDialog(private val context: Context,
                     private val minDate: Long ? = null) {

    companion object {
        fun minDateToday() : Long = System.currentTimeMillis() - 1000
    }

    fun show(listener: (Calendar) -> Unit) {
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val onDateSetListener = OnDateSetListener { _, y, m, d ->
            calendar.set(Calendar.MONTH, m)
            calendar.set(Calendar.DAY_OF_MONTH, d)
            calendar.set(Calendar.YEAR, y)


            listener.invoke(calendar)
        }

        val dialog = DatePickerDialog(context, onDateSetListener, year, month, day)

        minDate?.let { dialog.datePicker.minDate = it }

        dialog.show()
    }

    @JvmName("showForString")
    fun show(listener: (String) -> Unit) {
       show { calendar: Calendar ->
           val mothName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
           val dayName = calendar.get(Calendar.DAY_OF_MONTH).toOrdinalString()

           val result = context.getString(R.string.birthday_format, dayName, mothName)

           listener.invoke(result)
       }
    }


}

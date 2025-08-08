package tl.bnctl.banking.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateFormat
import tl.bnctl.banking.BuildConfig
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
class DateUtils {

    companion object {

        private val dateFormatISO = SimpleDateFormat("yyyy-MM-dd")
        private var contextDateFormat: java.text.DateFormat? = null

        fun getContextDateFormat(context: Context): java.text.DateFormat {
            if (contextDateFormat == null) {
                contextDateFormat = if (BuildConfig.DATE_FORMAT != null) {
                    SimpleDateFormat(BuildConfig.DATE_FORMAT)
                } else {
                    DateFormat.getDateFormat(context)
                }

            }
            return contextDateFormat!!
        }

        fun formatDate(context: Context, date: Date): String {
            val contextDateFormat = getContextDateFormat(context)
            return contextDateFormat.format(date)
        }

        fun formatDate(dateFormat: java.text.DateFormat, date: Date): String {
            return dateFormat.format(date)
        }

        fun formatDate(context: Context, date: String, inFormat: String): String {
            val inDateFormat = SimpleDateFormat(inFormat)
            val dateObj = inDateFormat.parse(date)
            val contextDateFormat = getContextDateFormat(context)
            return contextDateFormat.format(dateObj!!)
        }

        fun formatDateISO(date: Date): String {
            return dateFormatISO.format(date)
        }

        fun formatDateAndTimeToUserPreference(context: Context, date: String): String {
            val parsedDate = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date)
            if (parsedDate != null) {
                val dateFormat = getContextDateFormat(context)
                val timeFormat = if(BuildConfig.TIME_FORMAT != null) {
                    SimpleDateFormat(BuildConfig.TIME_FORMAT)
                } else {
                    DateFormat.getTimeFormat(context)
                }

                return "${dateFormat.format(parsedDate)} ${timeFormat.format(parsedDate)}"
            }
            return date
        }
    }
}
package tl.bnctl.banking.ui.utils

import android.content.Context
import android.text.TextUtils
import tl.bnctl.banking.BuildConfig
import tl.bnctl.banking.R
import tl.bnctl.banking.util.Constants
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat

class NumberUtils {

    // TODO: Use CurrencyFormat somehow instead of NumberFormat
    companion object {

        fun formatAmount(amount: Double?): String {
            val amountStr = scaleAmount(amount)
            return formatAmount(amountStr = amountStr)
        }

        fun formatAmount(decimal: Double): String {
            return scaleAmount(decimal)
        }

        fun formatAmount(amountStr: String?): String {
            val amount = amountStr!!.toDouble()
            val nf = NumberFormat.getNumberInstance() as DecimalFormat
            setDefaultDecimalSeparator(nf)
            nf.applyPattern(Constants.AMOUNT_FORMAT_SHORT)
            nf.minimumFractionDigits = BuildConfig.NUMBER_DECIMAL_PLACES
            nf.maximumFractionDigits = BuildConfig.NUMBER_DECIMAL_PLACES
            return nf.format(amount)
        }

        fun formatAmountWithCurrency(context: Context, amount: Double?, currency: String): String {
            val formattedAmount = formatAmount(amount)
            return context.resources.getString(
                R.string.common_amount_with_currency,
                formattedAmount,
                currency
            )
        }

        fun bigDecimalToStringNoDecimal(d: BigDecimal?): String? {
            val formatter = NumberFormat.getInstance() as DecimalFormat
            formatter.applyPattern(Constants.AMOUNT_FORMAT_SHORT)
            formatter.roundingMode = RoundingMode.DOWN
            return formatter.format(d)
        }

        /**
         * Example: input BigDecimal = 95.88 will return String = ".88"
         * Example: input BigDecimal = 0 will return String = ".00"
         *
         * Be aware about the decimal point which is set to a local standard
         *
         * @param value
         * @return is a String representation of reminder with a decimal point and two trailing zeroes after it
         */
        fun bigDecimalRemainderToStringWithTrailingZero(value: BigDecimal): String? {
            val formatter = NumberFormat.getInstance() as DecimalFormat
            formatter.applyPattern(".${addNeededNumberOfZeroesForFormat()}")
            setDefaultDecimalSeparator(formatter)
            return formatter.format(value.remainder(BigDecimal.ONE).abs())
        }

        fun addNeededNumberOfZeroesForFormat(): String {
            val list = Array(BuildConfig.NUMBER_DECIMAL_PLACES){""}
            list.fill("0")
            return StringBuilder().append(TextUtils.join("", list)).toString()
        }

        fun parseAmount(amountStr: String): BigDecimal {
            val nf = NumberFormat.getNumberInstance() as DecimalFormat
            setDefaultDecimalSeparator(nf)
            nf.minimumFractionDigits = BuildConfig.NUMBER_DECIMAL_PLACES
            nf.maximumFractionDigits = BuildConfig.NUMBER_DECIMAL_PLACES
            val amount = nf.parse(amountStr)!!
            return BigDecimal(amount.toString())
        }

        fun parseAmountToString(amount: String): String {
            return parseAmount(amount).toPlainString()
        }

        private fun scaleAmount(amount: Double?): String {
            return BigDecimal.valueOf(amount!!).setScale(BuildConfig.NUMBER_DECIMAL_PLACES, RoundingMode.HALF_UP).toString()
        }

        private fun setDefaultDecimalSeparator(formatter: DecimalFormat) {
            val decimalSymbols = DecimalFormatSymbols()
            decimalSymbols.decimalSeparator = BuildConfig.NUMBER_DECIMAL_SEPARATOR.toCharArray()[0]
            formatter.decimalFormatSymbols = decimalSymbols
        }
    }
}

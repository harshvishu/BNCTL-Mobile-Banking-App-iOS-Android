package tl.bnctl.banking.ui.banking.custom.amount.field

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.core.util.Consumer
import tl.bnctl.banking.BuildConfig
import tl.bnctl.banking.ui.utils.NumberUtils
import java.lang.ref.WeakReference
import java.text.DecimalFormat


class AmountTextWatcher(
    editText: EditText,
    val context: Context,
    private val runnable: Consumer<String>? = null
) : TextWatcher {

    private var strBuilder = StringBuilder()

    init {
        editText.setOnLongClickListener { false }
        editText.isLongClickable = false
    }

    companion object {
        private const val NUMBER_PARTS = 2
    }

    private val editTextWeakReference: WeakReference<EditText> = WeakReference(editText)
    private val decimalPlaces = BuildConfig.NUMBER_DECIMAL_PLACES
    //    private val localeSeparator: Char = DecimalFormatSymbols.getInstance().decimalSeparator
    private val defaultKeyboardDecimalSeparator: Char = BuildConfig.NUMBER_DECIMAL_SEPARATOR[0]
    private var lastCharMustBeExcluded = false


    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        // raise a flag before the string is changed when trying to add additional number after decimal point
//        if (start == s.length && after == 1 && s.contains(localeSeparator)) {
        if (start == s.length && after == 1 && s.contains(defaultKeyboardDecimalSeparator)) {
            lastCharMustBeExcluded = true
        }
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(editable: Editable) {
        strBuilder.setLength(0)
        var textAmount = editable.toString()
        val editText = editTextWeakReference.get() ?: return

        // if the last character is not the locale separator and is the default keyboard separator,
        // replace it with the locale separator
//        if (textAmount.isNotEmpty() && textAmount.last() == defaultKeyboardDecimalSeparator && localeSeparator != defaultKeyboardDecimalSeparator) {
//            textAmount = textAmount.replace(defaultKeyboardDecimalSeparator, localeSeparator)
//        }

//        if (textAmount.isNotEmpty() && textAmount.first() == localeSeparator && textAmount.length == 1) {
        if (textAmount.isNotEmpty() && textAmount.first() == defaultKeyboardDecimalSeparator && textAmount.length == 1) {
            textAmount = ""
            editText.removeTextChangedListener(this)
            editText.setText(textAmount)
            editText.addTextChangedListener(this)
            return
        }
        if (textAmount.isEmpty()) return
        strBuilder.append(textAmount)

        editText.removeTextChangedListener(this)
//        val currentDecimalSeparator = localeSeparator.toString()
        val currentDecimalSeparator = defaultKeyboardDecimalSeparator.toString()

        val amountSplitText = textAmount.split(currentDecimalSeparator)
        if (amountSplitText.isEmpty()) {
            editText.addTextChangedListener(this)
            return
        }
        val numberOfSeparators =
            textAmount.length - textAmount.replace(currentDecimalSeparator, "").length

        // if number contains decimal point and the length of the string after decimal point
        // is greater than than the allowed delete the unneeded part
        if ((amountSplitText.size == NUMBER_PARTS && amountSplitText[1].length > decimalPlaces) || numberOfSeparators > 1) {
            val sBuilder = StringBuilder(textAmount)
            // replace the number after cursor so that the length of the part after decimal point to
            // be as required
            if (!lastCharMustBeExcluded) {
//                val decimalPointIndex = textAmount.indexOf(localeSeparator)
                val decimalPointIndex = textAmount.indexOf(defaultKeyboardDecimalSeparator)
                textAmount =
                    if (editText.selectionStart > decimalPointIndex && editText.selectionStart < editText.text.length) {
                        sBuilder.deleteCharAt(editText.selectionStart).toString()
                    } else if (editText.text.length - 1 == editText.selectionStart) {
                        sBuilder.dropLast(1).toString()
                    } else {
                        val amountFormat = "0.${NumberUtils.addNeededNumberOfZeroesForFormat()}"
                        DecimalFormat(amountFormat).format(textAmount.replace(",", ".").toDouble())
                    }
            } else {
                textAmount = sBuilder.dropLast(1).toString()
                lastCharMustBeExcluded = false
            }

        }

        // update cursor position to be where it is expected
        val selectionPos = editText.selectionStart
        if (editText.selectionStart == textAmount.length) {
            editText.setText(textAmount)
            editText.setSelection(editText.text.length)

        } else {
            editText.setText(textAmount)
            if (selectionPos >= textAmount.length) {
                editText.setSelection(textAmount.length)
            } else if (editText.text.length >= selectionPos) {
                editText.setSelection(selectionPos)
            }
        }
        editText.addTextChangedListener(this)
        runnable?.accept(NumberUtils.parseAmountToString(textAmount))
    }
}

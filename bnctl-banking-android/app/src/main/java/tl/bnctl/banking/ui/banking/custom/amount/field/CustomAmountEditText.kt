package tl.bnctl.banking.ui.banking.custom.amount.field

import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.material.textfield.TextInputEditText
import tl.bnctl.banking.BuildConfig
import java.text.DecimalFormatSymbols


class CustomAmountEditText constructor(context: Context, attrs: AttributeSet) :
    TextInputEditText(context, attrs) {

    private val decimalPlaces = BuildConfig.NUMBER_DECIMAL_PLACES
    private var strBuilder = StringBuilder()
    private val defaultKeyboardDecimalSeparator: String = BuildConfig.NUMBER_DECIMAL_SEPARATOR
    private val localeSeparator: Char = DecimalFormatSymbols.getInstance().decimalSeparator

    private companion object {
        private val DECIMAL_PART_NUMBER_LENGTH = BuildConfig.NUMBER_DECIMAL_PLACES + 1 // + 1 for the decimal separator
        private val WHOLE_PART_NUMBER_LENGTH = BuildConfig.AMOUNT_NUMBER_MAX_LENGTH - DECIMAL_PART_NUMBER_LENGTH
    }

    init {
        textAlignment = View.TEXT_ALIGNMENT_VIEW_END
        keyListener =DigitsKeyListener.getInstance("0123456789${defaultKeyboardDecimalSeparator}")
//            DigitsKeyListener.getInstance("0123456789${localeSeparator}${defaultKeyboardDecimalSeparator}")
        filters = arrayOf(
            InputFilter { source, start, _, dest, _, _ -> filterString(source, start, dest) },
            InputFilter.LengthFilter(BuildConfig.AMOUNT_NUMBER_MAX_LENGTH)
        )
        isLongClickable = false
    }

    override fun setTextIsSelectable(selectable: Boolean) {
        super.setTextIsSelectable(false)
    }

    override fun isTextSelectable(): Boolean {
        return false
    }

    override fun getCustomSelectionActionModeCallback(): ActionMode.Callback {
        return object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode?) {}
        }
    }

    override fun startActionMode(callback: ActionMode.Callback?): ActionMode? {
        return null
    }

    override fun startActionMode(callback: ActionMode.Callback?, type: Int): ActionMode? {
        return null
    }

    private fun filterString (
        source: CharSequence,
        start: Int,
        dest: Spanned,
    ): String {
        val wholeStringAmount = text.toString()

        if (source.isEmpty()) return source.toString() // skip filter

        // filter pasting option from keyboard and when trying to add additional separator
        if ((source.length > 1 && dest.isNotEmpty()) ||
            // (wholeStringAmount.contains(localeSeparator) && source[start] == localeSeparator) ||
            wholeStringAmount.contains(defaultKeyboardDecimalSeparator) &&
            // (source == defaultKeyboardDecimalSeparator.toString() && source != localeSeparator.toString())) {
            source.toString() == defaultKeyboardDecimalSeparator) {
            return ""
        }

        // switch decimal separator if the keyboard allows typing a different delimiter than the local one
        if (arrayOf(
                //localeSeparator,
                defaultKeyboardDecimalSeparator.toCharArray()[0]).contains(source[0]) &&
            //source != localeSeparator.toString()) {
            source != defaultKeyboardDecimalSeparator) {
            // return localeSeparator.toString()
            return defaultKeyboardDecimalSeparator
        }

        val curPos = selectionEnd - 1
        val sbAmount = StringBuilder(wholeStringAmount)
        // skip adding additional numbers in the field when there is no decimal separator and the length of the string reached
        // max length of the field - (number of the decimal places after delimiter + 1 for the delimiter itself),
        // so that when focusing out of the field there must be enough space to be filled with decimal separator and trailing zeroes
        // "125" -> out of focus -> "125.00" => max_length = 6 | decimal_places = 2 | 1 space for "."
//        if ((!wholeStringAmount.contains(localeSeparator) && wholeStringAmount.length == WHOLE_PART_NUMBER_LENGTH) ||
        if ((!wholeStringAmount.contains(defaultKeyboardDecimalSeparator) && wholeStringAmount.length == WHOLE_PART_NUMBER_LENGTH) ||
            // skip adding additional numbers when the whole part of the number has reached max size
//            (wholeStringAmount.contains(localeSeparator) && wholeStringAmount.split(localeSeparator)[0].length == WHOLE_PART_NUMBER_LENGTH)) {
            (wholeStringAmount.contains(defaultKeyboardDecimalSeparator) && wholeStringAmount.split(defaultKeyboardDecimalSeparator)[0].length == WHOLE_PART_NUMBER_LENGTH)) {
            return sbAmount.deleteCharAt(curPos).toString()
//        } else if (wholeStringAmount.contains(localeSeparator) && wholeStringAmount.split(localeSeparator)[0].length == WHOLE_PART_NUMBER_LENGTH) {
        } else if (wholeStringAmount.contains(defaultKeyboardDecimalSeparator) && wholeStringAmount.split(defaultKeyboardDecimalSeparator)[0].length == WHOLE_PART_NUMBER_LENGTH) {
            return sbAmount.deleteCharAt(curPos).toString()
            // if max length is reached and there is decimal separator in the field, replace the existing number with the new one at the position of the cursor
//        } else if (wholeStringAmount.contains(localeSeparator) && wholeStringAmount.length == BuildConfig.AMOUNT_NUMBER_MAX_LENGTH) {
        } else if (wholeStringAmount.contains(defaultKeyboardDecimalSeparator) && wholeStringAmount.length == BuildConfig.AMOUNT_NUMBER_MAX_LENGTH) {
//            val indexOfSeparator = sbAmount.indexOf(localeSeparator)
            val indexOfSeparator = sbAmount.indexOf(defaultKeyboardDecimalSeparator)
            return if (curPos > indexOfSeparator) {
                val curP = if (curPos == wholeStringAmount.length - 1) { curPos } else { curPos + 1 }
                sbAmount.deleteCharAt(curP).toString()
            } else if (curPos < indexOfSeparator) {
                sbAmount.deleteCharAt(curPos).toString()
            } else {
                // if the cursor is to be replaced -> skip the addition
                dest.toString()
            }
        }
        return source.toString()
    }

    override fun isLongClickable(): Boolean {
        return false
    }

    override fun setLongClickable(longClickable: Boolean) {
        super.setLongClickable(false)
    }

    override fun hasSelection(): Boolean {
        return false
    }


    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        if (!focused) {
            text?.let { formatAmount(it) }
        }
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
    }

    override fun isSuggestionsEnabled(): Boolean {
        return false
    }

    /**
     * If string contains from whole and decimal part, format the value adding additional zeroes if needed
     */
    private fun formatAmount(text: Editable) {
        if (text.isNotEmpty()) {
            val currentDecimalSeparator =
//                localeSeparator.toString()
                defaultKeyboardDecimalSeparator
            val splitAmount = text.split(currentDecimalSeparator)
            if (splitAmount.size > 1) {
                val afterDecimalPointCharacters = splitAmount[1]
                if (afterDecimalPointCharacters.isNotEmpty()) {
                    if (afterDecimalPointCharacters.length == 1) {
                        text.clear()
                        text.append(
                            "${splitAmount[0]}${currentDecimalSeparator}${
                                addAdditionalZerosToAmount(afterDecimalPointCharacters)
                            }"
                        )
                    }
                } else {
                    text.clear()
                    text.append(
                        "${splitAmount[0]}${currentDecimalSeparator}${
                            addAdditionalZerosToAmount("")
                        }"
                    )
                }
            }
        }
    }

    private fun addAdditionalZerosToAmount(currentStringAfterDecimalPoint: String): String {
        strBuilder.setLength(0)
        strBuilder.append(currentStringAfterDecimalPoint)
        var startIndex = 0
        val endIndex = decimalPlaces - currentStringAfterDecimalPoint.length
        while (startIndex < endIndex) {
            strBuilder.append("0")
            startIndex++
        }

        return strBuilder.toString()
    }
}

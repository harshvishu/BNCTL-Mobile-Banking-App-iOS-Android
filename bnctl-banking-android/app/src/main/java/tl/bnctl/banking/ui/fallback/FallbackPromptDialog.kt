package tl.bnctl.banking.ui.fallback

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import tl.bnctl.banking.R

/**
 * Dialog to show when some action (login, transfer, etc.) could not be confirmed with SCA.
 * This dialog shows a message to the user saying they can use SMS to confirm whatever they are trying to do.
 */
class FallbackPromptDialog(
    context: Context,
    var message: Int,
    var onUseFallbackClickHandler: View.OnClickListener,
    private var onCancelHandler: (View.OnClickListener)?,
) : AlertDialog(context) {

    init {
        setCancelable(false)
    }

    private lateinit var backToLoginButton: Button
    private lateinit var useSmsButton: Button
    private lateinit var dialogTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_fallback)

        useSmsButton = findViewById(R.id.fallback_dialog_button_use_sms)
        useSmsButton.setOnClickListener(onUseFallbackClickHandler)

        dialogTextView = findViewById(R.id.dialog_message)
        dialogTextView.text = context.getString(message)


        backToLoginButton = findViewById(R.id.fallback_dialog_button_back_to_login)
        // Default action is to just close the dialog
        if (onCancelHandler == null) {
            onCancelHandler = View.OnClickListener {
                dismiss()
            }
        }
        backToLoginButton.setOnClickListener(onCancelHandler)

    }
}
package tl.bnctl.banking.ui.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.core.os.bundleOf
import tl.bnctl.banking.R
import tl.bnctl.banking.ui.fallback.FallbackPromptDialog
import tl.bnctl.banking.ui.login.LoginActivity

class DialogFactory {

    companion object {

        fun createLoadingDialog(
            context: Context,
            messageResource: Int = R.string.common_please_wait
        ): AlertDialog {
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null)
            val message = view.findViewById<TextView>(R.id.dialog_loading_message)
            message.setText(messageResource)
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setView(view)
                .setCancelable(false)
            return builder.create()
        }

        fun createCancellableDialog(context: Context, messageResource: Int): AlertDialog {
            return createInformativeDialog(
                context,
                null,
                messageResource,
                R.string.common_button_dismiss
            )
        }

        fun createInformativeDialog(
            context: Context,
            titleResource: Int?,
            messageResource: Int,
            buttonMsgResource: Int,
            doOnConfirmation: (() -> Any?)? = null
        ): AlertDialog {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            if (titleResource != null) {
                builder.setTitle(titleResource)
            }
            builder.setMessage(messageResource)
                .setCancelable(true)
                .setPositiveButton(buttonMsgResource) { dialog, _ ->
                    if (doOnConfirmation != null) {
                        doOnConfirmation()
                    }
                    dialog.dismiss()
                }
            return builder.create()
        }


        fun createNonCancelableInformativeDialog(
            context: Context,
            titleResource: Int?,
            messageResource: Int,
            buttonMsgResource: Int,
            doOnConfirmation: (() -> Any?)? = null
        ): AlertDialog {
            val dialog = createInformativeDialog(
                context,
                titleResource,
                messageResource,
                buttonMsgResource,
                doOnConfirmation
            )
            dialog.setCancelable(false)
            return dialog
        }

        fun createSessionExpiredDialog(
            context: Context,
            messageResource: Int = R.string.error_authentication_session_expired
        ): AlertDialog {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setMessage(messageResource)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_button_back_to_login) { _, _ ->
                    val intent = Intent(context, LoginActivity::class.java)
                    val bundle = bundleOf()
                    startActivity(context, intent, bundle)
                    (context as Activity).finish()
                }
            return builder.create()
        }

        fun createConfirmDialog(
            context: Context,
            messageResource: Int = R.string.common_confirmation_dialog_message,
            confirm: Int = R.string.common_confirm_message,
            reject: Int = R.string.common_reject_message,
            doOnConfirmation: () -> Any?,
        ): AlertDialog {
            return createConfirmDialog(
                context,
                messageResource,
                confirm,
                reject,
                doOnConfirmation,
                null
            )
        }

        fun createConfirmDialog(
            context: Context,
            messageResource: Int = R.string.common_confirmation_dialog_message,
            confirm: Int = R.string.common_confirm_message,
            reject: Int = R.string.common_reject_message,
            doOnConfirmation: () -> Any?,
            doOnCancel: (() -> Any)? = null
        ): AlertDialog {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setMessage(messageResource)
                .setCancelable(false)
                .setPositiveButton(confirm) { _, _ ->
                    doOnConfirmation()
                }
                .setNegativeButton(reject) { dialog, _ ->
                    if (doOnCancel != null) {
                        doOnCancel()
                    }
                    dialog.cancel()
                }
            return builder.create()
        }

        /**
         * Used when some action (login, transfer, etc.) could not be confirmed with SCA.
         * This dialog shows a message to the user saying they can use SMS (and maybe even PIN) to confirm whatever.
         * If the user chooses to use the fallback, the SMS code must be sent. This is handled by onUseFallbackClickHandler
         */
        fun createFallBackPromptDialog(
            context: Context,
            messageResource: Int = R.string.fallback_error_confirming_action_use_sms,
            onUseFallbackClickHandler: View.OnClickListener,
            onCancelHandler: (View.OnClickListener)? = null
        ): FallbackPromptDialog {
            return FallbackPromptDialog(
                context,
                messageResource,
                onUseFallbackClickHandler,
                onCancelHandler
            )
        }


    }
}
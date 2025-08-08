package tl.bnctl.banking.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.ui.utils.DialogFactory
import tl.bnctl.banking.util.Constants

abstract class BaseFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.crashlytics.log("onCreate: ${this.javaClass.simpleName}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Firebase.crashlytics.log("onDestroy: ${this.javaClass.simpleName}")
    }

    protected fun <T> handleResult(errMsg: Int, handleResultSuccess: (T) -> Unit): Observer<T> {
        return Observer {
            if (it == null || it is Result.Error) {
                handleResultError(it as Result.Error, errMsg)
            } else {
                handleResultSuccess(it as T)
            }
        }
    }

    protected fun <T> handleResult(
        handleResultError: (Result.Error) -> Unit,
        handleResultSuccess: (T) -> Unit
    ): Observer<T> {
        return Observer {
            if (it == null || it is Result.Error) {
                handleResultError(it as Result.Error)
            } else {
                handleResultSuccess(it as T)
            }
        }
    }

    protected fun handleResultError(error: Result.Error, errMsg: Int) {
        handleResultError(error, errMsg, true)
    }

    protected fun handleResultError(error: Result.Error, errMsg: Int, showDialog: Boolean = true) {
        var textId = errMsg
        // When the session is expired there is no target in the error
        if (error.code.equals(Constants.SESSION_EXPIRED_CODE) && error.target.isNullOrBlank()) {
            DialogFactory.createSessionExpiredDialog(requireActivity()).show()
        } else if (showDialog) {
            val errorCode = requireContext().resources.getIdentifier(
                error.getErrorString(),
                "string",
                requireContext().packageName
            )
            if (errorCode != 0) {
                textId = errorCode
            }
            DialogFactory.createCancellableDialog(requireContext(), textId).show()
        }
    }
}
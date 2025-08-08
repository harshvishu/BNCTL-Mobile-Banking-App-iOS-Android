package tl.bnctl.banking.ui.banking.fragments.transfers.end

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TransferEndViewModel : ViewModel() {

    private val _transferSuccessful: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val transferSuccessful: LiveData<Boolean> = _transferSuccessful

    private val _displayMessage: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val displayMessage: LiveData<String> = _displayMessage

    fun setTransferSuccessful(successful: Boolean) {
        _transferSuccessful.value = successful
    }

    fun setMessageToDisplay(messageToDisplay: String) {
        _displayMessage.value = messageToDisplay
    }
}
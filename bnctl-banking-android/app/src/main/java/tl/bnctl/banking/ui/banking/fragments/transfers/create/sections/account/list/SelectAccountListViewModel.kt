package tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.account.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tl.bnctl.banking.ui.banking.fragments.transfers.create.adapters.SelectAccountAdapter

class SelectAccountListViewModel : ViewModel() {

    private val _adapter = MutableLiveData<SelectAccountAdapter>()
    private val _resultKey = MutableLiveData<String>()

    val adapter: LiveData<SelectAccountAdapter> = _adapter
    val resultKey: LiveData<String> = _resultKey

    fun setAdapter(adapter: SelectAccountAdapter) {
        _adapter.value = adapter
    }

    fun setResultKey(resultKey: String) {
        _resultKey.value = resultKey
    }
}
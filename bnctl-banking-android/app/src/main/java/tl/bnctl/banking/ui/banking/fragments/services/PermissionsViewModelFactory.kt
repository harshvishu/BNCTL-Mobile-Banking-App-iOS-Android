package tl.bnctl.banking.ui.banking.fragments.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PermissionsViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PermissionsViewModel::class.java)) {
            return PermissionsViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }
}
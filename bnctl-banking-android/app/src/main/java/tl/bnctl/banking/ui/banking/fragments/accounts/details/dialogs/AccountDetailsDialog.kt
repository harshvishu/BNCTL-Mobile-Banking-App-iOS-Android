package tl.bnctl.banking.ui.banking.fragments.accounts.details.dialogs

import android.view.LayoutInflater
import android.view.ViewGroup

interface AccountDetailsDialog<T> {
    fun inflateDialog(inflater: LayoutInflater, container: ViewGroup?)
    fun getBinding(): T
    fun onDestroy()
    fun onCreateDialog()
}
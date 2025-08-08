package tl.bnctl.banking.ui.banking.fragments.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tl.bnctl.banking.services.PermissionService

class PermissionsViewModel: ViewModel() {

    private val _menuViews = MutableLiveData<Map<String, Boolean>>()
    val menuViews: LiveData<Map<String, Boolean>> = _menuViews

    fun registerViews(viewsIds: List<String>) {
        val map = HashMap<String, Boolean>()
        viewsIds.forEach { viewId ->
            map[viewId] = PermissionService.getInstance().checkIfViewShouldBeDisplayed(viewId)
        }
        _menuViews.postValue(map)
    }
}

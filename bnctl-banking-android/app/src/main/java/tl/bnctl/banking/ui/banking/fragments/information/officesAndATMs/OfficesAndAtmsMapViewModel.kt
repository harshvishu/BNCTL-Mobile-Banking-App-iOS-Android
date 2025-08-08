package tl.bnctl.banking.ui.banking.fragments.information.officesAndATMs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.atms.LocationsRepository
import tl.bnctl.banking.data.atms.model.LocationObj

class OfficesAndAtmsMapViewModel(
    private val locationsRepository: LocationsRepository
) : ViewModel() {

    private val _locations = MutableLiveData<List<LocationObj>>()
    val locations: LiveData<List<LocationObj>> = _locations

    fun fetchBranchesLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = locationsRepository.branchesFetch()
            if (result is Result.Success) {
                _locations.postValue(result.data)
            }
        }
    }

    fun fetchAtmsLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = locationsRepository.atmsFetch()
            if (result is Result.Success) {
                _locations.postValue(result.data)
            }
        }
    }
}
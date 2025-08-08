package tl.bnctl.banking.ui.banking.fragments.information.officesAndATMs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tl.bnctl.banking.connectivity.RetrofitService
import tl.bnctl.banking.data.atms.LocationsDataSource
import tl.bnctl.banking.data.atms.LocationsRepository
import tl.bnctl.banking.data.atms.LocationsService

class OfficesAndATMsViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OfficesAndAtmsMapViewModel::class.java)) {
            return OfficesAndAtmsMapViewModel(
                locationsRepository = LocationsRepository(
                    dataSource = LocationsDataSource(getAtmService())
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }

    private fun getAtmService(): LocationsService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(LocationsService::class.java)
    }
}
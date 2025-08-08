package tl.bnctl.banking.ui.banking.fragments.information.changeCustomer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tl.bnctl.banking.connectivity.RetrofitService
import tl.bnctl.banking.data.customer.CustomerDataSource
import tl.bnctl.banking.data.customer.CustomerRepository
import tl.bnctl.banking.data.customer.CustomerService

@Suppress("UNCHECKED_CAST")
class ChangeCustomerViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChangeCustomerViewModel::class.java)) {
            return ChangeCustomerViewModel(
                customerRepository = CustomerRepository(
                    dataSource = CustomerDataSource(getCustomerService())
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }

    private fun getCustomerService(): CustomerService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(CustomerService::class.java)
    }
}

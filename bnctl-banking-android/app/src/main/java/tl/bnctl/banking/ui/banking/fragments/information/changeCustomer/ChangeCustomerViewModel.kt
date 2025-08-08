package tl.bnctl.banking.ui.banking.fragments.information.changeCustomer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.customer.CustomerRepository
import tl.bnctl.banking.data.customer.model.Customer

class ChangeCustomerViewModel(
    private val customerRepository: CustomerRepository
): ViewModel() {

    private var _customers = MutableLiveData<Result<List<Customer>>>()
    val customers: LiveData<Result<List<Customer>>> = _customers


    fun fetchCustomers() {
        if (_customers.value == null) {
            viewModelScope.launch(Dispatchers.IO) {
                val result = customerRepository.customersFetch()
                _customers.postValue(result)
            }
        }

    }
}

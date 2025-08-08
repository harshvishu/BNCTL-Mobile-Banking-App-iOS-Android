package tl.bnctl.banking.data.customer

import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.customer.model.Customer

class CustomerRepository(private val dataSource: CustomerDataSource) {

    var customers: List<Customer>? = null

    suspend fun customersFetch(): Result<List<Customer>>{
        val result = dataSource.customersFetch()
        if (result is Result.Success) {
            customers = result.data
        }
        return result
    }

}

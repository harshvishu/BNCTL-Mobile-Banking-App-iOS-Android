package tl.bnctl.banking.data.customer

import com.google.gson.Gson
import com.google.gson.JsonArray
import retrofit2.HttpException
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.customer.model.Customer
import tl.bnctl.banking.services.AuthenticationService
import tl.bnctl.banking.util.Constants

class CustomerDataSource(
    private val customerService: CustomerService
) {
    val gson: Gson = Gson()

    suspend fun customersFetch(): Result<List<Customer>> {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)
           val customersResult = customerService.fetchCustomers(accessToken)
           val customers: JsonArray = customersResult.get("result") as JsonArray
           val customerList: List<Customer> = customers.map { customerJsonObj ->
                gson.fromJson(customerJsonObj.toString(), Customer::class.java)
            }
            return Result.Success(customerList)
        } catch (retrofitEx: HttpException) {
            return Result.createError(retrofitEx)
        } catch (ex: Exception) {
            return Result.createError(ex)
        }
    }

}

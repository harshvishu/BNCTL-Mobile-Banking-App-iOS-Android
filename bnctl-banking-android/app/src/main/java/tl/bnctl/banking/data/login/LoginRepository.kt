package tl.bnctl.banking.data.login

import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.fallback.SendFallbackSMSResult
import tl.bnctl.banking.data.login.model.LoggedInUser
import tl.bnctl.banking.data.login.model.accsessPolicy.AccessPolicy
import tl.bnctl.banking.services.AuthenticationService

class LoginRepository(val dataSource: LoginDataSource) {

    var user: LoggedInUser? = null
        private set // private setter with default implementation

    val isLoggedIn: Boolean
        get() = user != null

    init {
        user = null
    }

    suspend fun login(username: String, password: String): Result<LoggedInUser> {
        val result = dataSource.login(username, password)
        if (result is Result.Success) {
            user = result.data
        }
        return result
    }

    suspend fun logout() {
        user = null
        dataSource.logout()
    }

    suspend fun changePassword(
        username: String,
        oldPassword: String,
        newPassword: String
    ): Result<Boolean> {
        return dataSource.changePassword(username, oldPassword, newPassword)
    }

    suspend fun changeUsername(oldUsername: String, newUsername: String): Result<Boolean>? {
        return dataSource.changeUsername(oldUsername, newUsername)
    }

    /**
     * Sends SMS for initiating the fallback login
     */
    suspend fun sendFallbackSMS(
        username: String,
        password: String
    ): Result<SendFallbackSMSResult> {
        return dataSource.sendFallbackSMS(username, password)
    }

    /**
     * Confirm fallback login with SMS (and maybe PIN)
     */
    suspend fun confirmFallback(
        username: String,
        password: String,
        smsCode: String,
        pin: String? = null
    ): Result<LoggedInUser> {
        val result = dataSource.confirmFallback(username, password, smsCode, pin)
        if (result is Result.Success) {
            user = result.data
        }
        return result
    }

    /**
     * "Dirty", because we're not calling the backend to invalidate the current access token
     * This is not an issue for the current IB/MB implementations
     */
    fun dirtyLogout() {
        AuthenticationService.getInstance().logout()
    }

    suspend fun fetchAccessPolicy(): Result<AccessPolicy> {
        return dataSource.fetchAccessPolicy()
    }

}
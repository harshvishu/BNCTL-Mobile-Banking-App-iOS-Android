package tl.bnctl.banking.data.current_user

import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.login.model.LoggedInUser

class CurrentUserRepository(private val dataSource: CurrentUserDataSource) {

    var user: LoggedInUser? = null
        private set // private setter with default implementation

    init {
        user = null
    }

    suspend fun getCurrentUser(): Result<LoggedInUser>? {
        val result = dataSource.getCurrentUser()
        if (result is Result.Success) {
            user = result.data
        }
        return result
    }
}
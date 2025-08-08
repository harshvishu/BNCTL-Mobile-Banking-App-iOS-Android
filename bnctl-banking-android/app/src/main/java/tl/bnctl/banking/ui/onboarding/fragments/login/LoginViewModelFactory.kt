package tl.bnctl.banking.ui.onboarding.fragments.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tl.bnctl.banking.connectivity.RetrofitService
import tl.bnctl.banking.data.current_user.CurrentUserDataSource
import tl.bnctl.banking.data.current_user.CurrentUserRepository
import tl.bnctl.banking.data.current_user.CurrentUserService
import tl.bnctl.banking.data.login.LoginDataSource
import tl.bnctl.banking.data.login.LoginRepository
import tl.bnctl.banking.data.login.LoginService

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory : ViewModelProvider.Factory {

    /**
     * Creates a Retrofit Service specifically for Login operations.
     * This Service will be supplied to the Data Source.
     */
    private fun getLoginService(): LoginService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(LoginService::class.java)
    }

    private fun getCurrentUserService(): CurrentUserService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(CurrentUserService::class.java)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            val loginService = getLoginService() // API helper Service
            val loginDataSource =
                LoginDataSource(loginService) // Data Source that consumes data from an API
            val loginRepository =
                LoginRepository(loginDataSource) // Repository, which has a Data Source

            val currentUserService = getCurrentUserService()
            val currentUserDataSource = CurrentUserDataSource(currentUserService)
            val currentUserRepository = CurrentUserRepository(currentUserDataSource)
            return LoginViewModel(
                loginRepository = loginRepository,
                currentUserRepository = currentUserRepository
            ) as T // Create the ViewModel with the Repository
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }
}
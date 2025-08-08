package tl.bnctl.banking.ui.banking.fragments.settings.changeUsername

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tl.bnctl.banking.connectivity.RetrofitService
import tl.bnctl.banking.data.login.LoginDataSource
import tl.bnctl.banking.data.login.LoginRepository
import tl.bnctl.banking.data.login.LoginService

class ChangeUsernameViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChangeUsernameViewModel::class.java)) {
            return ChangeUsernameViewModel(
                LoginRepository(LoginDataSource(getLoginService()))
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }

    private fun getLoginService(): LoginService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(LoginService::class.java)
    }
}
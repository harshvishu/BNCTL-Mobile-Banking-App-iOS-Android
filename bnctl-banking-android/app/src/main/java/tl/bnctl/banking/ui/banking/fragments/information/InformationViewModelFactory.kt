package tl.bnctl.banking.ui.banking.fragments.information

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tl.bnctl.banking.connectivity.RetrofitService
import tl.bnctl.banking.data.login.LoginDataSource
import tl.bnctl.banking.data.login.LoginRepository
import tl.bnctl.banking.data.login.LoginService

class InformationViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InformationViewModel::class.java)) {
            return InformationViewModel(
                loginRepository = LoginRepository(
                    dataSource = LoginDataSource(loginService = getLoginService())
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }

    private fun getLoginService(): LoginService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(LoginService::class.java)
    }
}
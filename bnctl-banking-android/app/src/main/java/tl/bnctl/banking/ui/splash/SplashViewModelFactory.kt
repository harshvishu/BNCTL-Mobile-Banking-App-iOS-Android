package tl.bnctl.banking.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tl.bnctl.banking.connectivity.RetrofitService
import tl.bnctl.banking.data.config.AppConfigDataSource
import tl.bnctl.banking.data.config.AppConfigRepository
import tl.bnctl.banking.data.config.AppConfigService

class SplashViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(
                getAppConfigRepository()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class.")
    }

    private fun getAppConfigRepository(): AppConfigRepository {
        return AppConfigRepository(getAppConfigDataSource())
    }

    private fun getAppConfigDataSource(): AppConfigDataSource {
        return AppConfigDataSource(getAppConfigService())
    }

    private fun getAppConfigService(): AppConfigService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(AppConfigService::class.java)
    }
}
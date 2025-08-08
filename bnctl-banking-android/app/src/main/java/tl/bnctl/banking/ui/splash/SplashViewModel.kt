package tl.bnctl.banking.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.config.AppConfigRepository
import tl.bnctl.banking.data.config.model.ApplicationConfigurationDto

class SplashViewModel(
    private val appConfigRepository: AppConfigRepository
) : ViewModel() {

    private val _appConfigurationResult =
        MutableLiveData<Result<ApplicationConfigurationDto>>().apply {}
    val versionCheck: LiveData<Result<ApplicationConfigurationDto>> = _appConfigurationResult

    fun fetchApplicationConfiguration() {
        viewModelScope.launch(Dispatchers.IO) {
            _appConfigurationResult.postValue(appConfigRepository.getAppConfig())
        }
    }
}
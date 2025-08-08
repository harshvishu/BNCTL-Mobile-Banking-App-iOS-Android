package tl.bnctl.banking

import android.app.Application
import androidx.lifecycle.LifecycleObserver
import tl.bnctl.banking.services.AuthenticationService
import tl.bnctl.banking.services.ExceptionLogging
import tl.bnctl.banking.services.PermissionService
import tl.bnctl.banking.services.SettingsService

class BankingApplication : Application(), LifecycleObserver {

    override fun onCreate() {
        super.onCreate()
        SettingsService.initSettingsService(this)
        AuthenticationService.initAuthenticationService(this)
        PermissionService.initPermissionService(this)
        ExceptionLogging.prepareErrorLoggingKeys(this)
    }

    companion object {
        const val appCode: String = "mobileBanking"
    }
}
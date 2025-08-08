package tl.bnctl.banking.services

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.crashlytics.ktx.setCustomKeys
import com.google.firebase.ktx.Firebase
import tl.bnctl.banking.BuildConfig
import tl.bnctl.banking.util.LocaleHelper

class ExceptionLogging {

    companion object {
        fun prepareErrorLoggingKeys(context: Context) {
            val googlePlayServicesAvailability: String = if (GoogleApiAvailability
                    .getInstance()
                    .isGooglePlayServicesAvailable(context) == 0
            ) "Unavailable" else "Available"
            Firebase.crashlytics.setCrashlyticsCollectionEnabled(true)
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences(
                    tl.bnctl.banking.BankingApplication.appCode,
                    Context.MODE_PRIVATE
                )
            val tokenData = AuthenticationService.getInstance().getTokenData()
            val userId = if (tokenData.userId !== null) {
                tokenData.userId
            } else ""
            Firebase.crashlytics.setUserId(userId)
            Firebase.crashlytics.setCustomKeys {
                key("Locale", LocaleHelper.getCurrentLanguage(context))
                key("Google Play Services Availability", googlePlayServicesAvailability)
                key("Os Version", System.getProperty("os.version") ?: "Unknown")
                key("Customer number", "not captured")
                key("User ID", userId)
                key("Permissions", sharedPreferences.getString("permissions", "")!!)
                key("Debug enabled", BuildConfig.DEBUG.toString())
                key("Build Type", BuildConfig.BUILD_TYPE)
                key("App version", BuildConfig.VERSION_NAME)
                key("Base URL", BuildConfig.BASE_URL)
                key("Maker-Checker flow", BuildConfig.MAKER_CHECKER_FLOW)
                key("Supported languages", BuildConfig.SUPPORTED_LANGUAGES.joinToString())
            }
        }
    }
}

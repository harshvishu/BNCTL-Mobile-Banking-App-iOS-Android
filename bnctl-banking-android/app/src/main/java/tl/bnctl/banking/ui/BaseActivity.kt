package tl.bnctl.banking.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.security.ProviderInstaller
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import tl.bnctl.banking.services.ExceptionLogging
import tl.bnctl.banking.util.LocaleHelper

abstract class BaseActivity : AppCompatActivity(), ProviderInstaller.ProviderInstallListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProviderInstaller.installIfNeededAsync(this, this)
        ExceptionLogging.prepareErrorLoggingKeys(this)
        Firebase.crashlytics.log("onCreate: ${this.javaClass.simpleName}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Firebase.crashlytics.log("onDestroy: ${this.javaClass.simpleName}")
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    /**
     * This method is only called if the provider is successfully updated
     * (or is already up-to-date).
     */
    override fun onProviderInstalled() {
        // Provider is up-to-date, app can make secure network calls.
    }

    /**
     * This method is called if updating fails; the error code indicates
     * whether the error is recoverable.
     */
    override fun onProviderInstallFailed(errorCode: Int, recoveryIntent: Intent?) {
        GoogleApiAvailability.getInstance().apply {
            if (isUserResolvableError(errorCode)) {
                // Recoverable error. Show a dialog prompting the user to
                // install/update/enable Google Play services.
//                showErrorDialogFragment(this@BaseActivity, errorCode, ERROR_DIALOG_REQUEST_CODE) {
//                    // The user chose not to take the recovery action
//                    onProviderInstallerNotAvailable()
//                }
            } else {
                onProviderInstallerNotAvailable()
            }
        }
    }

    private fun onProviderInstallerNotAvailable() {
        // This is reached if the provider cannot be updated for some reason.
        // App should consider all HTTP communication to be vulnerable, and take
        // appropriate action.
    }

}

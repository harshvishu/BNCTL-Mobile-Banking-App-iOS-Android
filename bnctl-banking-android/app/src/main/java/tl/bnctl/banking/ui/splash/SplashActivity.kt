package tl.bnctl.banking.ui.splash

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import tl.bnctl.banking.BankingApplication
import tl.bnctl.banking.BuildConfig
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.config.model.ApplicationConfigurationDto
import tl.bnctl.banking.data.config.model.UpdateType
import tl.bnctl.banking.data.config.model.VersionConfigurationDto
import tl.bnctl.banking.ui.login.LoginActivity
import tl.bnctl.banking.ui.onboarding.OnboardingActivity
import tl.bnctl.banking.ui.utils.DialogFactory
import tl.bnctl.banking.util.Constants

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val splashViewModel: SplashViewModel by viewModels { SplashViewModelFactory() }

    override fun onResume() {
        super.onResume()
        if (splashViewModel.versionCheck.value != null && splashViewModel.versionCheck.value is Result.Success) {
            handleAppConfigFetch(splashViewModel.versionCheck.value)
        } else {
            splashViewModel.fetchApplicationConfiguration()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashViewModel.versionCheck.observe(this) {
            handleAppConfigFetch(it)
        }
    }

    private fun handleAppConfigFetch(result: Result<ApplicationConfigurationDto>?) {
        Log.d(TAG, "handleAppConfigFetch: got application configuration: ${result.toString()}")
        if (result is Result.Success) {
            val appConfig = result.data
            when (getUpdateType(appConfig)) {
                UpdateType.MANDATORY -> showMandatoryUpdateDialog()
                UpdateType.RECOMMENDED -> {
                    val sharedPreferences: SharedPreferences =
                        getSharedPreferences(
                            BankingApplication.appCode,
                            MODE_PRIVATE
                        )
                    val version = appConfig.version!!
                    val latestVersionCode: Int = version.latest.versionCode
                    val lastKnownVersionCode: Int =
                        sharedPreferences.getInt(
                            Constants.LAST_KNOWN_RECOMMENDED_APP_VERSION_CODE,
                            0
                        )
                    if (latestVersionCode != lastKnownVersionCode) {
                        with(sharedPreferences.edit()) {
                            putInt(
                                Constants.LAST_KNOWN_RECOMMENDED_APP_VERSION_CODE,
                                latestVersionCode
                            )
                            apply()
                        }

                        showRecommendedUpdateDialog()
                    } else {
                        startLoginActivity()
                    }
                }
                UpdateType.NONE -> startLoginActivity()
            }
        } else {
            val errorString = (result as Result.Error).getErrorString()
            val stringId = resources.getIdentifier(errorString, "string", packageName)
            val errorStringId =
                if (stringId == 0) R.string.error_loading_app_config else stringId

            DialogFactory.createConfirmDialog(
                this,
                errorStringId,
                R.string.common_button_retry,
                R.string.common_button_dismiss,
                onRetryChosen,
                onExitChosen
            ).show()
        }
    }

    private fun showRecommendedUpdateDialog() {
        DialogFactory.createConfirmDialog(
            this,
            R.string.update_dialog_recommended_update_text,
            R.string.update_dialog_update_button,
            R.string.common_button_dismiss,
            onUpdateConfirm
        ) {
            startLoginActivity()
        }.show()
    }

    private fun showMandatoryUpdateDialog() {
        DialogFactory.createConfirmDialog(
            this,
            R.string.update_dialog_mandatory_update_text,
            R.string.update_dialog_update_button,
            R.string.update_dialog_exit,
            onUpdateConfirm,
            onExitChosen
        ).show()
    }

    private val onExitChosen: () -> Unit = {
        this.finish()
    }

    private val onRetryChosen: () -> Unit = {
        splashViewModel.fetchApplicationConfiguration()
    }

    private val onUpdateConfirm: () -> Unit = {
        // Go to the PlayStore to update
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$packageName")
                )
            )
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
        this.finish()
    }

    private fun getUpdateType(appConfig: ApplicationConfigurationDto): UpdateType {
        val version: VersionConfigurationDto? = appConfig.version
        val currentVersion: Int = BuildConfig.VERSION_CODE
        return if (version != null && version.minimum.versionCode > currentVersion) {
            UpdateType.MANDATORY
        } else if (version != null && version.latest.versionCode > currentVersion) {
            UpdateType.RECOMMENDED
        } else {
            UpdateType.NONE
        }
    }

    /**
     * Based on the "hasLoggedInBefore" flag start the appropriate activity.
     * If user has previously successfully logged in - lead them to LoginActivity directly.
     * If not - lead them to the OnboardingActivity.
     */
    private fun startLoginActivity() {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences(tl.bnctl.banking.BankingApplication.appCode, MODE_PRIVATE)
        val hasLoggedInBefore =
            sharedPreferences.getBoolean("hasLoggedInBefore", false)
        val intent: Intent = if (hasLoggedInBefore) {
            Intent(this, LoginActivity::class.java)
        } else {
            Intent(this, OnboardingActivity::class.java)
        }
        intent.flags =
            (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) and Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    companion object {
        val TAG: String = SplashActivity::class.java.simpleName
    }
}
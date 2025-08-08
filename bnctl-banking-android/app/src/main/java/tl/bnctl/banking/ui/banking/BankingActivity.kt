package tl.bnctl.banking.ui.banking

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.runBlocking
import tl.bnctl.banking.R
import tl.bnctl.banking.data.login.LoginDataSource
import tl.bnctl.banking.data.login.LoginService
import tl.bnctl.banking.databinding.ActivityBankingBinding
import tl.bnctl.banking.ui.BaseActivity
import tl.bnctl.banking.ui.banking.fragments.dashboard.DashboardFragmentDirections
import tl.bnctl.banking.ui.login.LoginActivity
import tl.bnctl.banking.ui.utils.DialogFactory

private const val s = "usernameChanged"

/**
 * The main activity that User reaches after they have successfully been onboarded.
 */
class BankingActivity : BaseActivity() {

    private lateinit var binding: ActivityBankingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBankingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_banking)
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_fragment_dashboard -> navView.visibility = View.VISIBLE
                R.id.nav_fragment_transfers -> navView.visibility = View.VISIBLE
                R.id.nav_fragment_account_details -> navView.visibility = View.VISIBLE
                R.id.nav_fragment_services -> navView.visibility = View.VISIBLE
                R.id.nav_fragment_cards -> navView.visibility = View.VISIBLE
                R.id.nav_fragment_information -> navView.visibility = View.VISIBLE
                R.id.nav_fragment_transfer_between_own_accounts -> navView.visibility = View.GONE
                R.id.nav_fragment_iban_transfer -> navView.visibility = View.GONE
                R.id.nav_fragment_internal_transfer -> navView.visibility = View.GONE
                R.id.nav_fragment_currency_exchange -> navView.visibility = View.GONE
                R.id.nav_fragment_transfer_summary -> navView.visibility = View.GONE
                R.id.nav_fragment_transfer_confirm -> navView.visibility = View.GONE
                R.id.nav_fragment_transfer_end -> navView.visibility = View.GONE
                R.id.nav_fragment_transfer_pending -> navView.visibility = View.GONE
                R.id.nav_fragment_pending_transfer_confirm -> navView.visibility = View.GONE
                R.id.nav_fragment_card_transaction_history -> navView.visibility = View.GONE
                R.id.nav_fragment_card_details -> navView.visibility = View.GONE
                R.id.nav_fragment_templates -> navView.visibility = View.GONE
                R.id.nav_fragment_utility_bills -> navView.visibility = View.GONE
                R.id.nav_fragment_utility_bills_summary -> navView.visibility = View.GONE
                R.id.nav_fragment_account_statement_filter -> navView.visibility = View.GONE
                R.id.nav_fragment_account_additional_details -> navView.visibility = View.GONE
                R.id.nav_fragment_statements_filter -> navView.visibility = View.GONE
                R.id.nav_fragment_transfer_history -> navView.visibility = View.GONE
                R.id.nav_fragment_insurance -> navView.visibility = View.GONE
                R.id.nav_fragment_insurance_confirm -> navView.visibility = View.GONE
                R.id.nav_fragment_cash_withdrawal -> navView.visibility = View.GONE
                R.id.nav_fragment_offices_and_atms -> navView.visibility = View.GONE
                R.id.nav_fragment_offices_and_atms_map -> navView.visibility = View.GONE
                R.id.nav_fragment_new_debit_card -> navView.visibility = View.GONE
                R.id.nav_fragment_news -> navView.visibility = View.GONE
                R.id.nav_fragment_news_selected -> navView.visibility = View.GONE
                R.id.nav_fragment_statements_details -> navView.visibility = View.GONE
                R.id.nav_fragment_change_client -> navView.visibility = View.GONE
                R.id.nav_fragment_exchange_rates -> navView.visibility = View.GONE
                R.id.nav_fragment_change_language -> navView.visibility = View.GONE
                R.id.nav_fragment_settings -> navView.visibility = View.GONE
                R.id.nav_fragment_credit_card_statement -> navView.visibility = View.GONE
                R.id.nav_fragment_settings_change_password -> navView.visibility = View.GONE
                R.id.nav_fragment_settings_change_username -> navView.visibility = View.GONE
            }
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Checking if the user has their username and/or password change and navigating to the change screens
        val usernameChanged = intent.getBooleanExtra(
            USERNAME_CHANGED_EXTRA,
            false
        ) // TODO: Decide if this should be true by default

        val passwordChanged = intent.getBooleanExtra(
            PASSWORD_CHANGED_EXTRA,
            false
        ) // TODO: Maybe it's better to be true
        val oldUsername: String? = intent.getStringExtra(OLD_USERNAME_EXTRA)
        val oldPassword: String? = intent.getStringExtra(OLD_PASSWORD_EXTRA)

        if (!passwordChanged) {
            navController.navigate(
                DashboardFragmentDirections.actionGlobalNavFragmentChangePassword(
                    oldUsername,
                    oldPassword,
                    usernameChanged,
                    false // Don't require the user to enter the current password, because they've just entered it when loggint in
                )
            )
            return
        }

        if (!usernameChanged) {
            navController.navigate(
                DashboardFragmentDirections.actionGlobalNavFragmentChangeUsername(
                    oldUsername,
                    false
                )
            )
        }

    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.nav_host_fragment_activity_banking)

        // Some screens shouldn't allow going back. Changing the temporary username/password for example
        if (disabledBackPress.indexOf(navController.currentDestination?.id) != -1) {
            return
        }

        if (navController.currentDestination?.id != R.id.nav_fragment_dashboard) {
            super.onBackPressed()
        } else {
            DialogFactory.createConfirmDialog(this, R.string.dialog_message_about_to_logout) {
                val loginDataSource = LoginDataSource(
                    tl.bnctl.banking.connectivity.RetrofitService.getInstance()
                        .getService(LoginService::class.java)
                )
                runBlocking { loginDataSource.logout() }
                // Leaving this commented out, because we're asking the user if they want to also
                // exit the application, so we exit the application without sending them to the login screen
                // startLoginActivity()
                this.finish()
            }.show()
        }
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags =
            (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) and Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("shouldLogout", true)
        startActivity(intent)
    }

    companion object {
        const val OLD_USERNAME_EXTRA = "oldUsername"
        const val OLD_PASSWORD_EXTRA = "oldPassword"
        const val USERNAME_CHANGED_EXTRA = "usernameChanged"
        const val PASSWORD_CHANGED_EXTRA = "passwordChanged"

        val disabledBackPress = arrayOf(
            R.id.nav_fragment_settings_change_password,
            R.id.nav_fragment_settings_change_username,
        )
    }
}
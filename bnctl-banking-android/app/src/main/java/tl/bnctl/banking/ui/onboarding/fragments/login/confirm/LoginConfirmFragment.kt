package tl.bnctl.banking.ui.onboarding.fragments.login.confirm

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.databinding.FragmentTransferConfirmBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.BankingActivity
import tl.bnctl.banking.ui.fallback.FallbackConfirmActivity
import tl.bnctl.banking.ui.fallback.FallbackPromptDialog
import tl.bnctl.banking.ui.onboarding.fragments.login.LoginViewModel
import tl.bnctl.banking.ui.onboarding.fragments.login.LoginViewModelFactory
import tl.bnctl.banking.ui.utils.DialogFactory
import tl.bnctl.banking.util.Constants

// TODO: Use this for login OTP
class LoginConfirmFragment : BaseFragment() {

    private var _binding: FragmentTransferConfirmBinding? = null
    private val binding get() = _binding!!

    private val navArgs: LoginConfirmFragmentArgs by navArgs()

    private val loginViewModel: LoginViewModel by activityViewModels { LoginViewModelFactory() }

    // Dialogs
    private var sendingSMSDialog: AlertDialog? = null
    private var loggingYouInDialog: AlertDialog? = null
    private var fallbackPromptDialog: FallbackPromptDialog? = null

    // Fallback activity
    private val fallbackConfirm =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            fallbackPromptDialog?.dismiss()
            sendingSMSDialog?.dismiss()
            loggingYouInDialog?.dismiss()
            if (result.resultCode == Activity.RESULT_OK) {
                val smsCode =
                    result.data?.getStringExtra(FallbackConfirmActivity.EXTRA_SMS_CODE_KEY)
                val pin = result.data?.getStringExtra(FallbackConfirmActivity.EXTRA_PIN_KEY)
                loggingYouInDialog = DialogFactory.createLoadingDialog(
                    requireContext(),
                    R.string.login_dialog_label_logging_you_in
                )
                loginViewModel.confirmFallback(
                    navArgs.username,
                    navArgs.password,
                    smsCode!!,
                    pin
                )
            } else {
                findNavController().popBackStack()
            }
        }

    companion object {
        val TAG: String = LoginConfirmFragment::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel.resetLoginState()
        // Do this here, otherwise the login is initiated again after you return from the fallback screen
        loginViewModel.login(navArgs.username, navArgs.password)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransferConfirmBinding.inflate(inflater, container, false)
        binding.transferConfirmLabel.text = getString(R.string.login_confirm_label)
        binding.transferConfirmMessage.text = getString(R.string.login_confirm_message)
        binding.toolbar.menu[0].isVisible = false
        observeLogin()
        observeCurrentUser()
        observeFallbackLogin()
        return binding.root
    }

    /**
     * Once user clicks Login we observe the result of the Login request.
     * If it's successful - we make another call - for current user.
     * On Failure return to login form
     * @see observeCurrentUser
     */
    private fun observeLogin() {
        loginViewModel.loginResult.observe(viewLifecycleOwner) {
//            handleResult(Errors.getAuthErrorStringId(requireContext(), it.error.getErrorString())) {loginResult: LoginResult -> loginViewModel.checkCurrentUser()}
            if (it?.success != null) {
                loginViewModel.checkCurrentUser()
            }
            if (it?.error != null) run {
                var errorCode = 0
                when (it.error.target) {
                    Constants.ERROR_SCA_ERROR,
                    Constants.ERROR_SCA_EXPIRED,
                    Constants.ERROR_FALLBACK_AGREEMENT -> {
                        fallbackPromptDialog = DialogFactory.createFallBackPromptDialog(
                            context = requireContext(),
                            onUseFallbackClickHandler = onUseSmsConfirm,
                            onCancelHandler = {
                                loginViewModel.resetLoginState()
                                fallbackPromptDialog!!.dismiss()
                                findNavController().popBackStack()
                            }
                        )
                        fallbackPromptDialog!!.show()
                    }
                    Constants.ERROR_FALLBACK_INVALID_SMS -> {
                        fallbackConfirm.launch(
                            getFallbackConfirmIntent(
                                loginViewModel.usePin.value == true,
                                true
                            )
                        )
                    }
                    Constants.ERROR_NO_CONNECTION -> {
                        errorCode = R.string.error_no_connection
                    }
                    Constants.ERROR_INVALID_USERNAME,
                    Constants.ERROR_INVALID_PASSWORD -> {
                        errorCode = R.string.error_access_denied_invalid_credentials
                    }
                    Constants.ERROR_INVALID_PHONE_NUMBER -> {
                        errorCode = R.string.error_authentication_invalid_mobile_number_error_login
                    }
                    else -> {
                        errorCode = R.string.error_generic
                    }
                }
                if (errorCode != 0) {
                    handleResultError(
                        it.error,
                        errorCode,
                        true
                    )
                    findNavController().popBackStack()
                }
            }
        }
    }

    /**
     * Waits for the result for the current user.
     * If there's a valid current user, we move along to the dashboard activity.
     * @see startBankingActivity
     */
    private fun observeCurrentUser() {
        // Uncomment this if you want the app to login the user automatically when there's a saved session
        // loginViewModel.checkCurrentUser()
        loginViewModel.currentUserResult.observe(viewLifecycleOwner) {
            if (it != null) {
                val sharedPreferences: SharedPreferences = requireActivity()
                    .getSharedPreferences(
                        tl.bnctl.banking.BankingApplication.appCode,
                        Context.MODE_PRIVATE
                    )
                sharedPreferences.edit().putBoolean("hasLoggedInBefore", true).apply()
                startBankingActivity()
            }
        }
    }

    private fun startBankingActivity() {
        val intent = Intent(activity, BankingActivity::class.java)
        intent.flags =
            (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) and Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeFallbackLogin() {
        loginViewModel.fallbackSMSResult.observe(viewLifecycleOwner) {
            sendingSMSDialog?.hide()
            if (it != null) {
                if (it is Result.Success) {
                    loginViewModel.setUsePin(it.data.usePin)
                    fallbackConfirm.launch(getFallbackConfirmIntent(it.data.usePin, false))
                } else {
                    handleResultError((it as Result.Error), R.string.fallback_error_sending_sms)
                }
            }
        }
    }

    private fun getFallbackConfirmIntent(usePin: Boolean, wrongSms: Boolean): Intent {
        val intent = Intent(requireContext(), FallbackConfirmActivity::class.java)
        intent.putExtra(
            FallbackConfirmActivity.EXTRA_TITLE_TEXT_KEY,
            R.string.fallback_login_via_sms
        )
        if (usePin) {
            intent.putExtra(
                FallbackConfirmActivity.EXTRA_TITLE_TEXT_KEY,
                R.string.fallback_login_via_sms_and_pin
            )
        }
        intent.putExtra(FallbackConfirmActivity.EXTRA_WRONG_SMS_CODE_KEY, wrongSms)
        intent.putExtra(FallbackConfirmActivity.EXTRA_USE_PIN_KEY, usePin)

        return intent
    }

    private val onUseSmsConfirm: View.OnClickListener =
        View.OnClickListener {
            sendingSMSDialog =
                DialogFactory.createLoadingDialog(requireContext(), R.string.common_please_wait)
            sendingSMSDialog!!.show()
            fallbackPromptDialog!!.hide()
            loginViewModel.sendFallbackSMS(navArgs.username, navArgs.password)
        }

    override fun onPause() {
        super.onPause()
        sendingSMSDialog?.dismiss()
        loggingYouInDialog?.dismiss()
        fallbackPromptDialog?.dismiss()
    }
}
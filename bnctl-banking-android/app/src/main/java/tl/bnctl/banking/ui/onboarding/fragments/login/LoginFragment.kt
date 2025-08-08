package tl.bnctl.banking.ui.onboarding.fragments.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.autofill.AutofillManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import tl.bnctl.banking.BuildConfig
import tl.bnctl.banking.R
import tl.bnctl.banking.data.login.model.LoggedInUser
import tl.bnctl.banking.databinding.FragmentLoginBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.BankingActivity
import tl.bnctl.banking.ui.utils.DialogFactory
import tl.bnctl.banking.util.Constants
import tl.bnctl.banking.util.LocaleHelper
import java.util.*


class LoginFragment : BaseFragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by activityViewModels { LoginViewModelFactory() }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            // Nothing happening for now when new location is received
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val locationRequest = LocationRequest.create()
        locationRequest.interval = 600 * 1000
        locationRequest.fastestInterval = 150 * 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Create binding and set listeners
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.loginLanguageOption.text = BuildConfig.SUPPORTED_LANGUAGES.filter {
            !it.equals(LocaleHelper.getCurrentLanguage(requireContext()))
        }[0]?.uppercase()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val autofillManager: AutofillManager? =
                getSystemService(requireContext(), AutofillManager::class.java)
            autofillManager!!.disableAutofillServices()
            binding.fragmentLoginInputUsername.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO
            binding.fragmentLoginInputPassword.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO
        }

        binding.loginLanguageOption.setOnClickListener {
            LocaleHelper.setLocale(
                requireContext(),
                binding.loginLanguageOption.text.toString().lowercase()
            )
            requireActivity().finish()
            startActivity(requireActivity().intent)
        }

        // Setup ViewModel and observers
        binding.fragmentLoginButtonLogin.setOnClickListener {
            val username = binding.fragmentLoginInputUsername
            val password = binding.fragmentLoginInputPassword
            if (isLoginValid()) {
                binding.loadingIndicator.visibility = View.VISIBLE
                binding.fragmentLoginButtonLogin.isEnabled = false

                // When there is OTP for login, this will show the LoginConfirmFragment which
                // is expected to handle all the OTP-related logic.
                if (BuildConfig.USE_OTP_ON_LOGIN) {
                    findNavController().navigate(
                        LoginFragmentDirections.actionNavFragmentLoginToNavFragmentConfirmLogin(
                            username.text.toString().trim(),
                            password.text.toString()
                        )
                    )
                } else {
                    loginViewModel.login(username.text.toString(), password.text.toString())
                }
            }
        }
        /*binding.loginNewsButton.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionNavFragmentLoginToCommonNewsNavigation())
        }
        binding.loginBranchesButton.setOnClickListener {
            findNavController().navigate(
                LoginFragmentDirections.actionNavFragmentLoginToNavFragmentOfficesAndAtmsMap(
                    true
                )
            )
        }*/
        binding.loginForgotPassword.setOnClickListener {
            DialogFactory.createInformativeDialog(
                requireContext(),
                R.string.login_label_title_dialog_forgoten_password,
                R.string.login_label_text_forgoten_password,
                R.string.login_label_button_forgoten_password
            ).show()
        }

        setupFields()
        observeCurrentUser()
        observeLogin()
        return binding.root
    }

    /**
     * Waits for the result for the current user.
     * If there's a valid current user, we move along to the dashboard activity.
     * @see startBankingActivity
     */
    private fun observeCurrentUser() {
        // If you want the app to login the user automatically when there's a saved session, set this
        // to true. (Please do so in the local.properties file, so you don't accidentally commit it)
        if (BuildConfig.CHECK_CURRENT_USER_ON_LOGIN.toBoolean()) {
            loginViewModel.checkCurrentUser()
        }
        loginViewModel.currentUserResult.observe(viewLifecycleOwner) {
            if (it != null) {
                val sharedPreferences: SharedPreferences = requireActivity()
                    .getSharedPreferences(
                        tl.bnctl.banking.BankingApplication.appCode,
                        Context.MODE_PRIVATE
                    )
                sharedPreferences.edit().putBoolean("hasLoggedInBefore", true).apply()
                startBankingActivity(it)
                requireActivity().finish()
            }
        }
    }

    private fun startBankingActivity(loggedInUser: LoggedInUser) {
        val intent = Intent(activity, BankingActivity::class.java)
        intent.flags =
            (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) and Intent.FLAG_ACTIVITY_CLEAR_TASK

        addTemporaryCredentialsExtras(loggedInUser, intent)

        startActivity(intent)
    }

    private fun addTemporaryCredentialsExtras(
        loggedInUser: LoggedInUser,
        intent: Intent
    ) {
        // Passing username and password here is safe since we're not doing IPC and it's all in this little sandbox carelessly minding its own business
        intent.putExtra("oldPassword", binding.fragmentLoginInputPassword.text.toString())
        intent.putExtra("oldUsername", binding.fragmentLoginInputUsername.text.toString())

        intent.putExtra("usernameChanged", loggedInUser.authData.usernameChanged)
        intent.putExtra("passwordChanged", loggedInUser.authData.passwordChanged)
    }

    private fun isLoginValid(): Boolean {
        val username = binding.fragmentLoginInputUsername.text.toString()
        val password = binding.fragmentLoginInputPassword.text.toString()

        if (username.isBlank()) {
            loginViewModel.raiseUsernameError(R.string.common_error_field_required)
        } else {
            loginViewModel.clearUsernameError()
        }
        if (password.isBlank()) {
            loginViewModel.raisePasswordError(R.string.common_error_field_required)
        } else {
            loginViewModel.clearPasswordError()
        }

        return username.isNotBlank() && password.isNotBlank()
    }

    private fun setupFields() {
        loginViewModel.usernameError.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.fragmentLoginInputUsernameLayout.error = resources.getString(it)
            } else {
                binding.fragmentLoginInputUsernameLayout.error = null
                binding.fragmentLoginInputUsernameLayout.isErrorEnabled = false
            }
        }
        loginViewModel.passwordError.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.fragmentLoginInputPasswordLayout.error = resources.getString(it)
            } else {
                binding.fragmentLoginInputPasswordLayout.error = null
                binding.fragmentLoginInputPasswordLayout.isErrorEnabled = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeLogin() {
        loginViewModel.loginResult.observe(viewLifecycleOwner) {
            binding.fragmentLoginButtonLogin.isEnabled = true
            if (it?.success != null) {
                binding.loadingIndicator.visibility = View.GONE
                loginViewModel.checkCurrentUser()
            }
            if (it?.error != null) run {
                binding.loadingIndicator.visibility = View.GONE
                var errorCode = resources.getIdentifier(
                    it.error.getErrorString(), "string", requireActivity().packageName
                )
                if (errorCode == 0) {
                    errorCode = when (it.error.target) {
                        Constants.ERROR_NO_CONNECTION -> {
                            R.string.error_no_connection
                        }
                        Constants.ERROR_INVALID_USERNAME,
                        Constants.ERROR_INVALID_PASSWORD -> {
                            R.string.error_access_denied_invalid_credentials
                        }
                        Constants.ERROR_INVALID_PHONE_NUMBER -> {
                            R.string.error_authentication_invalid_mobile_number_error_login
                        }
                        else -> {
                            R.string.error_generic
                        }
                    }
                }
                handleResultError(
                    it.error,
                    errorCode,
                    true
                )
            }
        }
    }

    companion object {
        @JvmField
        val TAG: String = LoginFragment::class.java.name
    }
}

///**
// * Extension function to simplify setting an afterTextChanged action to EditText components.
// */
//fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
//    this.addTextChangedListener(object : TextWatcher {
//        override fun afterTextChanged(editable: Editable?) {
//            afterTextChanged.invoke(editable.toString())
//        }
//
//        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
//
//        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
//    })
//}
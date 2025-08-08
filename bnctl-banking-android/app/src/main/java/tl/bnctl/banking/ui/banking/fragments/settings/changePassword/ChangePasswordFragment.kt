package tl.bnctl.banking.ui.banking.fragments.settings.changePassword

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.databinding.FragmentChangePasswordBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.BankingActivity
import tl.bnctl.banking.ui.banking.fragments.dashboard.DashboardFragmentDirections
import tl.bnctl.banking.ui.login.LoginActivity
import tl.bnctl.banking.ui.utils.DialogFactory

class ChangePasswordFragment : BaseFragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var loadingDialog: Dialog

    private val changePasswordViewModel: ChangePasswordViewModel by navGraphViewModels(R.id.nav_fragment_settings_change_password) {
        ChangePasswordViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        initUI()
        changePasswordViewModel.fetchAccessPolicy()
        return binding.root
    }

    private fun initUI() {

        loadingDialog = DialogFactory.createLoadingDialog(requireContext())

        /*
           * Show or hide the field for the user's current password.
           * When the user comes here from the login screen, they shouldn't have to enter their password, because they've just done it
           */
        val shouldRequireCurrentPassword = shouldRequireCurrentPassword()

        with(binding) {
            if (shouldRequireCurrentPassword) {
                settingsChangePasswordCurrentPasswordLayout.visibility = View.VISIBLE
                inputCurrentPassword.isEnabled = true
                inputCurrentPassword.isFocusable = true
            } else {
                settingsChangePasswordCurrentPasswordLayout.visibility = View.GONE
                inputCurrentPassword.isEnabled = false
                inputCurrentPassword.isFocusable = false
            }
        }

        // Hide back arrow from the toolbar if we've come here from the login screen
        if (!shouldRequireCurrentPassword) {
            binding.toolbar.navigationIcon = null
        } else {
            binding.toolbar.setOnClickListener {
                findNavController().popBackStack()
            }
        }


        binding.settingsChangePasswordSubmitButton.setOnClickListener {
            if (isInputValid()) {
                binding.loadingIndicator.visibility = View.VISIBLE
                val oldPassword = if (shouldRequireCurrentPassword()) {
                    binding.inputCurrentPassword.text.toString()
                } else {
                    arguments?.getString(BankingActivity.OLD_PASSWORD_EXTRA)!!
                }
                changePasswordViewModel.changePassword(
                    arguments?.getString(BankingActivity.OLD_USERNAME_EXTRA)!!,
                    oldPassword,
                    binding.inputNewPassword.text.toString()
                )
            }
        }
        setupObservers()
    }

    private fun setupObservers() {

        changePasswordViewModel.changePasswordResult.observe(viewLifecycleOwner) {
            binding.loadingIndicator.visibility = View.GONE
            if (it is Result.Success) {
                onPasswordChangeSuccess()
            } else {
                handleResultError(it as Result.Error, R.string.error_generic)
            }
        }
        changePasswordViewModel.repeatPasswordError.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.inputRepeatPassword.error = resources.getString(it)
                binding.inputRepeatPassword.requestFocus()
            } else {
                binding.inputRepeatPassword.error = null
            }
        }

        changePasswordViewModel.passwordError.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.inputNewPassword.error = resources.getString(it)
                binding.inputNewPassword.requestFocus()
            } else {
                binding.inputNewPassword.error = null
            }
        }

        changePasswordViewModel.currentPasswordError.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.inputCurrentPassword.error = resources.getString(it)
                binding.inputCurrentPassword.requestFocus()
            } else {
                binding.inputCurrentPassword.error = null
            }
        }

        changePasswordViewModel.loading.observe(viewLifecycleOwner) {
            if (it) {
                loadingDialog.show()
            } else {
                loadingDialog.dismiss()
            }
        }

        changePasswordViewModel.accessPolicyResult.observe(viewLifecycleOwner) {
            if (it is Result.Error) {
                var errorStringId = requireContext().resources.getIdentifier(
                    it.getErrorString(),
                    "string",
                    requireActivity().packageName
                )
                if (errorStringId == 0) {
                    Log.e(TAG, "Couldn't find error string: ${it.getErrorString()}")
                    errorStringId = R.string.error_generic
                }

                DialogFactory.createNonCancelableInformativeDialog(
                    requireContext(),
                    null,
                    errorStringId,
                    R.string.common_ok
                ) {
                    findNavController().popBackStack()
                }.show()
            }
        }
    }

    private fun onPasswordChangeSuccess() {
        val usernameChanged = arguments?.getBoolean(BankingActivity.USERNAME_CHANGED_EXTRA, true)!!
        val buttonText = if (usernameChanged) {
            R.string.dialog_button_back_to_login
        } else {
            R.string.common_button_next
        }

        val successDialog = DialogFactory.createInformativeDialog(
            requireContext(),
            R.string.settings_change_password_success_dialog_title,
            R.string.settings_change_password_success,
            buttonText
        ) {
            completePasswordChange(usernameChanged)
        }
        successDialog.show()
    }

    private fun completePasswordChange(usernameChanged: Boolean) {
        /*
         * If the username is changed, we go straight to the login
         */
        if (usernameChanged) {
            changePasswordViewModel.dirtyLogout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            val bundle = bundleOf()
            ContextCompat.startActivity(requireContext(), intent, bundle)
            (requireContext() as Activity).finish()
        } else {
            findNavController().navigate(
                DashboardFragmentDirections.actionGlobalNavFragmentChangeUsername(
                    arguments?.getString(BankingActivity.OLD_USERNAME_EXTRA)!!
                )
            )
        }
    }

    private fun isInputValid(): Boolean {
        with(binding) {
            if (shouldRequireCurrentPassword() && inputCurrentPassword.text != null && inputCurrentPassword.text!!.isBlank()) {
                changePasswordViewModel.raiseCurrentPasswordError(R.string.common_error_field_required)
                return false
            } else {
                changePasswordViewModel.clearPasswordError()
            }
            if (inputNewPassword.text != null && inputNewPassword.text!!.isBlank()) {
                changePasswordViewModel.raisePasswordError(R.string.common_error_field_required)
                return false
            } else {
                changePasswordViewModel.clearPasswordError()
            }

            if (changePasswordViewModel.accessPolicyResult.value is Result.Success) {
                val newPassword = inputNewPassword.text!!
                val accessPolicy =
                    changePasswordViewModel.accessPolicyResult.value as Result.Success
                if (!newPassword.matches(Regex(accessPolicy.data.passwordPolicy.regex))) {
                    changePasswordViewModel.raisePasswordError(R.string.password_must_match_criteria)
                    return false
                } else {
                    changePasswordViewModel.clearPasswordError()
                }
            }

            if (inputRepeatPassword.text != null && inputRepeatPassword.text!!.isBlank()) {
                changePasswordViewModel.raiseRepeatPasswordError(R.string.common_error_field_required)
                return false
            } else {
                changePasswordViewModel.clearRepeatPasswordError()
            }

            if (inputNewPassword.text.toString() != inputRepeatPassword.text.toString()) {
                changePasswordViewModel.raiseRepeatPasswordError(R.string.passwords_do_not_match)
                return false
            } else {
                changePasswordViewModel.clearRepeatPasswordError()
            }
        }
        return true
    }

    private fun shouldRequireCurrentPassword(): Boolean {
        return arguments?.getBoolean(REQUIRE_CURRENT_PASSWORD_EXTRA, true)!!
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val REQUIRE_CURRENT_PASSWORD_EXTRA = "requireCurrentPassword"
        val TAG: String = ChangePasswordFragment::class.java.simpleName
    }
}
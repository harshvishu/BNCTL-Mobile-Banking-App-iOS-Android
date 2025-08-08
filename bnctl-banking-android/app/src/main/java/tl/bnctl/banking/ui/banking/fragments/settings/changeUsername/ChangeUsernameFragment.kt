package tl.bnctl.banking.ui.banking.fragments.settings.changeUsername

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
import tl.bnctl.banking.databinding.FragmentChangeUsernameBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.BankingActivity
import tl.bnctl.banking.ui.banking.fragments.dashboard.DashboardFragmentDirections
import tl.bnctl.banking.ui.login.LoginActivity
import tl.bnctl.banking.ui.utils.DialogFactory

class ChangeUsernameFragment : BaseFragment() {

    private var _binding: FragmentChangeUsernameBinding? = null
    private val binding get() = _binding!!

    private lateinit var loadingDialog: Dialog

    private val changeUsernameViewModel: ChangeUsernameViewModel by navGraphViewModels(R.id.nav_fragment_settings_change_username) {
        ChangeUsernameViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeUsernameBinding.inflate(inflater, container, false)
        initUI()
        changeUsernameViewModel.fetchAccessPolicy()
        return binding.root
    }

    private fun initUI() {

        loadingDialog = DialogFactory.createLoadingDialog(requireContext())

        /*
         * Show or hide the field for the user's current username.
         * When the user comes here from the login screen, they shouldn't have to enter their username, because they've just done it
         */
        val shouldRequireCurrentUsername = shouldShowCurrentUsername()

        binding.inputCurrentUsername.setText(requireArguments().getString(BankingActivity.OLD_USERNAME_EXTRA))

        // Hide back arrow from the toolbar if we've come here from the login screen
        if (!shouldRequireCurrentUsername) {
            binding.toolbar.navigationIcon = null
        } else {
            binding.toolbar.setOnClickListener {
                findNavController().popBackStack()
            }
        }


        binding.settingsChangeUsernameSubmitButton.setOnClickListener {
            if (isInputValid()) {
                binding.loadingIndicator.visibility = View.VISIBLE
                binding.settingsChangeUsernameSubmitButton.isEnabled = false
                changeUsernameViewModel.changeUsername(
                    arguments?.getString(BankingActivity.OLD_USERNAME_EXTRA)!!,
                    binding.inputNewUsername.text.toString()
                )
            }
        }
        setupObservers()
    }

    private fun setupObservers() {
        changeUsernameViewModel.changeUsernameResult.observe(viewLifecycleOwner) {
            binding.loadingIndicator.visibility = View.GONE
            binding.settingsChangeUsernameSubmitButton.isEnabled = true
            if (it is Result.Success) {
                onUsernameChangeSuccess()
            } else {
                handleResultError(it as Result.Error, R.string.error_generic)
            }
        }
        changeUsernameViewModel.repeatUsernameError.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.inputRepeatUsername.error = resources.getString(it)
                if (changeUsernameViewModel.usernameError.value == null) {
                    binding.inputRepeatUsername.requestFocus()
                }
            } else {
                binding.inputRepeatUsername.error = null
            }
        }

        changeUsernameViewModel.usernameError.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.inputNewUsername.error = resources.getString(it)
                binding.inputNewUsername.requestFocus()
            } else {
                binding.inputNewUsername.error = null
            }
        }

        changeUsernameViewModel.loading.observe(viewLifecycleOwner) {
            if (it) {
                loadingDialog.show()
            } else {
                loadingDialog.dismiss()
            }
        }

        changeUsernameViewModel.accessPolicyResult.observe(viewLifecycleOwner) {
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

    private fun onUsernameChangeSuccess() {
        val usernameChanged = arguments?.getBoolean(BankingActivity.USERNAME_CHANGED_EXTRA, true)!!
        val buttonText = if (usernameChanged) {
            R.string.dialog_button_back_to_login
        } else {
            R.string.common_button_next
        }

        val successDialog = DialogFactory.createInformativeDialog(
            requireContext(),
            R.string.settings_change_username_success_dialog_title,
            R.string.settings_change_username_success,
            buttonText
        ) {
            completeUsernameChange(usernameChanged)
        }
        successDialog.show()
    }

    private fun completeUsernameChange(usernameChanged: Boolean) {
        if (usernameChanged) {
            changeUsernameViewModel.dirtyLogout()
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
        var valid = true
        with(binding) {
            if (inputNewUsername.text == null || inputNewUsername.text!!.isBlank()) {
                changeUsernameViewModel.raiseUsernameError(R.string.common_error_field_required)
                valid = false
            } else {
                if (changeUsernameViewModel.accessPolicyResult.value is Result.Success) {
                    val newUsername = inputNewUsername.text!!
                    val accessPolicy =
                        changeUsernameViewModel.accessPolicyResult.value as Result.Success
                    if (!newUsername.matches(Regex(accessPolicy.data.usernamePolicy.regex))) {
                        changeUsernameViewModel.raiseUsernameError(R.string.username_must_match_criteria)
                        valid = false
                    } else {
                        changeUsernameViewModel.clearUsernameError()
                    }
                }
            }

            if (inputRepeatUsername.text == null || inputRepeatUsername.text!!.isBlank()) {
                changeUsernameViewModel.raiseRepeatUsernameError(R.string.common_error_field_required)
                valid = false
            } else {
                changeUsernameViewModel.clearRepeatUsernameError()
            }

            if (inputRepeatUsername.text == null || inputRepeatUsername.text!!.isBlank()) {
                changeUsernameViewModel.raiseRepeatUsernameError(R.string.common_error_field_required)
                valid = false
            } else {
                if (inputNewUsername.text.toString() != inputRepeatUsername.text.toString()) {
                    changeUsernameViewModel.raiseRepeatUsernameError(R.string.usernames_do_not_match)
                    valid = false
                } else {
                    changeUsernameViewModel.clearRepeatUsernameError()
                }
            }

        }
        return valid
    }

    private fun shouldShowCurrentUsername(): Boolean {
        return arguments?.getBoolean(SHOW_CURRENT_USERNAME_EXTRA, true)!!
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val SHOW_CURRENT_USERNAME_EXTRA = "requireCurrentUsername"
        val TAG: String = ChangeUsernameFragment::class.java.simpleName
    }
}
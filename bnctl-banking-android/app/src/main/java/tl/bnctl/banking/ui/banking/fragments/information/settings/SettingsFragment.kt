package tl.bnctl.banking.ui.banking.fragments.information.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import tl.bnctl.banking.databinding.FragmentSettingsBinding
import tl.bnctl.banking.services.AuthenticationService
import tl.bnctl.banking.ui.BaseFragment

class SettingsFragment : BaseFragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.settingsToolbar.setOnClickListener {
            findNavController().popBackStack()
        }
        val tokenData = AuthenticationService.getInstance().getTokenData()
        with(binding) {
            languageMenuItem.setOnClickListener {
                findNavController()
                    .navigate(SettingsFragmentDirections.actionNavFragmentSettingsToNavFragmentLanguages())
            }
            changeUsernameMenuItem.setOnClickListener {
                findNavController()
                    .navigate(
                        SettingsFragmentDirections.actionNavFragmentSettingsToNavFragmentChangeUsername(
                            tokenData.username, true
                        )
                    )
            }
            changePasswordMenuItem.setOnClickListener {
                findNavController()
                    .navigate(
                        SettingsFragmentDirections.actionNavFragmentSettingsToNavFragmentChangePassword(
                            requireCurrentPassword = true,
                            oldUsername = tokenData.username
                        )
                    )
            }
        }

        return binding.root
    }
}

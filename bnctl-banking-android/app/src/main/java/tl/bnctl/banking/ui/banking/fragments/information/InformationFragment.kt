package tl.bnctl.banking.ui.banking.fragments.information

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import tl.bnctl.banking.R
import tl.bnctl.banking.databinding.FragmentInformationBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.login.LoginActivity
import tl.bnctl.banking.ui.utils.DialogFactory


class InformationFragment : BaseFragment() {

    private var _binding: FragmentInformationBinding? = null
    private val binding get() = _binding!!

    private val informationViewModel: InformationViewModel by activityViewModels { InformationViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInformationBinding.inflate(inflater, container, false)
        val root: View = binding.root
        /*
        binding.newsMenuItem.setOnClickListener {
            findNavController().navigate(InformationFragmentDirections.actionGlobalNavFragmentNews())
        }
        binding.officesAndAtmsMenuItem.setOnClickListener {
            findNavController().navigate(InformationFragmentDirections.actionNavFragmentInformationToNavFragmentOfficesAndAtms())
        }
        binding.exchangeRatesMenuItem.setOnClickListener {
            findNavController()
                .navigate(InformationFragmentDirections.actionNavFragmentInformationToNavFragmentExchangeRates())
        }
        */
        binding.settingsMenuItem.setOnClickListener {
            findNavController()
                .navigate(InformationFragmentDirections.actionNavFragmentInformationToNavFragmentSettings())
        }
        binding.managePayeesMenuItem.setOnClickListener {
            findNavController()
                .navigate(
                    InformationFragmentDirections.actionNavFragmentInformationToNavFragmentTemplates(
                        false
                    )
                )
        }
        /*
        binding.switchUserMenuItem.setOnClickListener {
            findNavController()
                .navigate(InformationFragmentDirections.actionNavFragmentInformationToNavFragmentChangeCustomer())
        }
        */
        binding.logoutMenuItem.setOnClickListener {
            DialogFactory.createConfirmDialog(
                requireContext(),
                messageResource = R.string.information_logout_dialog_text,
                confirm = R.string.information_logout_dialog_confirmation_text,
                doOnConfirmation = {
                    try {
                        informationViewModel.logout()
                    } catch (e: Exception) {
                        Log.e(TAG, "Got error logging out", e)
                    }
                }).show()
        }

        informationViewModel.logoutStatus.observe(viewLifecycleOwner) {
            if (it) {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                ContextCompat.startActivity(requireContext(), intent, bundleOf())
                requireActivity().finish()
            }
        }

        return root
    }

    companion object {
        val TAG = InformationFragment::class.java.simpleName
    }
}

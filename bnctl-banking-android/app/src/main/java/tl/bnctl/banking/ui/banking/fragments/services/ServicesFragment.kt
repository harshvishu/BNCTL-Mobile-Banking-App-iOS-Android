package tl.bnctl.banking.ui.banking.fragments.services

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import tl.bnctl.banking.R
import tl.bnctl.banking.databinding.FragmentServicesBinding
import tl.bnctl.banking.ui.banking.fragments.services.currencyExchange.CurrencyExchangeFragment
import tl.bnctl.banking.ui.banking.fragments.services.insurance.confirm.InsuranceConfirmFragment
import tl.bnctl.banking.ui.banking.fragments.services.utilityBills.summary.UtilityBillSummaryFragment

class ServicesFragment : BasePermissionFragment() {

    private var _binding: FragmentServicesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServicesBinding.inflate(inflater, container, false)
        val keyToViewId = mapOf(
            UtilityBillSummaryFragment::class.java.toString() to binding.servicesUtilityBillsMenuItem.id.toString(),
            InsuranceConfirmFragment::class.java.toString() to binding.servicesInsuranceMenuItem.id.toString(),
            CurrencyExchangeFragment::class.java.toString() to  binding.servicesForeignCurrencyExchangeMenuItem.id.toString()
        )
        setupViewsToPermission(keyToViewId, container!!)
        setupBackButton()
        setupNavigation()
        return binding.root
    }

    private fun setupNavigation() {
        binding.servicesUtilityBillsMenuItem.setOnClickListener { view ->
            view.findNavController()
                .navigate(R.id.action_nav_fragment_services_to_nav_fragment_utility_bills)
        }

        binding.servicesInsuranceMenuItem.setOnClickListener { view ->
            view.findNavController()
                .navigate(R.id.action_nav_fragment_services_to_nav_fragment_insurance)
        }

        binding.servicesCashWithdrawalMenuItem.setOnClickListener { view ->
            view.findNavController()
                .navigate(R.id.action_nav_fragment_services_to_nav_fragment_cash_withdrawal)
        }

        binding.servicesForeignCurrencyExchangeMenuItem.setOnClickListener { view ->
            view.findNavController()
                .navigate(R.id.action_nav_fragment_services_to_nav_fragment_currency_exchange)
        }
    }

    private fun setupBackButton() {
        binding.toolbar.setNavigationOnClickListener {
            it.findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

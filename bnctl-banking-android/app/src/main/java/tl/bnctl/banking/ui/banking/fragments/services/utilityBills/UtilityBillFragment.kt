package tl.bnctl.banking.ui.banking.fragments.services.utilityBills

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.databinding.FragmentServiceUtilityBillBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.fragments.services.utilityBills.adapter.UtilityBillViewAdapter
import tl.bnctl.banking.ui.utils.DialogFactory

class UtilityBillFragment : BaseFragment() {

    private var _binding: FragmentServiceUtilityBillBinding? = null
    private val binding get() = _binding!!

    private val utilityBillViewModel: UtilityBillViewModel by viewModels { UtilityBillViewModelFactory() }
    private lateinit var utilityBillViewAdapter: UtilityBillViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServiceUtilityBillBinding.inflate(inflater, container, false)
        utilityBillViewAdapter = UtilityBillViewAdapter(utilityBillViewModel.myBillers)
        binding.serviceUtilityBillsList.adapter = utilityBillViewAdapter
        binding.serviceUtilityBillsPay.isEnabled = false
        binding.serviceUtilityBillsSelectAllCheckBox.isChecked = false
        binding.serviceUtilityBillsSelectAllCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                utilityBillViewModel.selectAll()
                utilityBillViewAdapter.notifyDataSetChanged()
            } else {
                utilityBillViewModel.unselectAll()
                utilityBillViewAdapter.notifyDataSetChanged()
            }
        }
        if (!utilityBillViewModel.eodResult.hasActiveObservers()) {
            utilityBillViewModel.eodResult.observe(viewLifecycleOwner) {
                if (it.error?.target.equals("sessionExpired")) {
                    DialogFactory.createSessionExpiredDialog(requireActivity()).show()
                } else if (utilityBillViewModel.myBillers.value != null) {
                    val selected = (utilityBillViewModel.myBillers.value as Result.Success).data
                        .filter { it.isSelected }
                    if (selected.isNotEmpty()) {
                        if (utilityBillViewModel.eodResult.value != null) {
                            if (utilityBillViewModel.eodResult.value!!.eodStarted) {
                                DialogFactory.createCancellableDialog(
                                    requireActivity(),
                                    R.string.error_utility_bills_eod_check
                                ).show()
                            } else {
                                requireView().findNavController().navigate(
                                    UtilityBillFragmentDirections.actionNavFragmentUtilityBillsToNavFragmentUtilityBillsSummary(
                                        selected.map { it.userBillerId }.toTypedArray(),
                                        selected.sumOf { it.billAmount.toBigDecimal() }
                                            .stripTrailingZeros()
                                            .toPlainString(),
                                        selected[0].currencyName
                                    )
                                )
                            }
                        }
                    } else {
                        DialogFactory.createCancellableDialog(
                            requireActivity(),
                            R.string.error_utility_bills_selected
                        ).show()
                    }
                }
            }
        }
        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.menu[0].setOnMenuItemClickListener {
            findNavController()
                .navigate(R.id.action_nav_fragment_utility_bills_to_nav_fragment_utility_bills_history)
            true
        }
        binding.serviceUtilityBillsPay.setOnClickListener {
            utilityBillViewModel.checkEoD()
        }
        fetchBillers()
        return binding.root
    }

    private fun fetchBillers() {
        utilityBillViewModel.fetchMyBillers()
        binding.loadingIndicator.visibility = View.VISIBLE
        utilityBillViewModel.myBillers.observe(viewLifecycleOwner) {
            if (it is Result.Error) {
                handleResultError(it, R.string.error_loading_billers)
                binding.serviceUtilityBillsPay.isEnabled = false
                binding.loadingIndicator.visibility = View.GONE
            } else if (it is Result.Success) {
                val billers = it.data
                utilityBillViewAdapter.notifyItemRangeChanged(0, billers.size)
                utilityBillViewAdapter.notifyDataSetChanged()
                binding.serviceUtilityBillsPay.isEnabled = billers.isNotEmpty()
                binding.loadingIndicator.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        utilityBillViewModel.clearBillers()
        utilityBillViewAdapter.notifyDataSetChanged()
        _binding = null
    }
}
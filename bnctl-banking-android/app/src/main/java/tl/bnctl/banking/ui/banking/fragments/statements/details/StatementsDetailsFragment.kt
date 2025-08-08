package tl.bnctl.banking.ui.banking.fragments.statements.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import tl.bnctl.banking.databinding.FragmentStatementsDetailsBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.utils.DateUtils
import tl.bnctl.banking.ui.utils.NumberUtils

class StatementsDetailsFragment : BaseFragment() {

    private var _binding: FragmentStatementsDetailsBinding? = null
    private val binding get() = _binding!!

    private val navArgs: StatementsDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatementsDetailsBinding.inflate(inflater, container, false)

        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.toolbarTitle.text = getString(navArgs.titleResourceId)
        binding.fragmentStatementsDetailsAmount.text = NumberUtils.formatAmount(navArgs.selectedItem.amount)
        binding.fragmentStatementsDetailsCurrency.text = navArgs.selectedItem.currency
        if (navArgs.selectedItem.dateOfExecution != null) {
            binding.fragmentStatementsDetailsDate.text =
                DateUtils.formatDateAndTimeToUserPreference(
                    requireContext(),
                    navArgs.selectedItem.dateOfExecution!!
                )
        }
        binding.fragmentStatementsDetailsFromValue.text = navArgs.selectedItem.sourceAccount

        val reason = navArgs.selectedItem.reason
        val beneficiary = navArgs.selectedItem.beneficiary
        if (reason.isBlank()) {
            binding.fragmentStatementsDetailsReasonContainer.visibility = View.GONE
        } else {
            binding.fragmentStatementsDetailsReasonContainer.visibility = View.VISIBLE
            binding.fragmentStatementsDetailsReasonValue.text = navArgs.selectedItem.reason
        }

        if (beneficiary.isNullOrBlank()) {
            binding.fragmentStatementsDetailsBeneficiaryContainer.visibility = View.GONE
        } else {
            binding.fragmentStatementsDetailsBeneficiaryContainer.visibility = View.VISIBLE
            binding.fragmentStatementsDetailsBeneficiary.text = beneficiary
        }

        val destinationAccount = navArgs.selectedItem.destinationAccount
        if (destinationAccount.isNullOrBlank()) {
            binding.fragmentStatementsDetailsDestinationContainer.visibility = View.GONE
        } else {
            binding.fragmentStatementsDetailsDestinationContainer.visibility = View.VISIBLE
            binding.fragmentStatementsDetailsToValue.text = destinationAccount
        }

        val additionalInfo = navArgs.selectedItem.additionalInfo
        if (additionalInfo.isBlank()) {
            binding.fragmentStatementsDetailsProviderContainer.visibility = View.GONE
        } else {
            binding.fragmentStatementsDetailsProviderContainer.visibility = View.VISIBLE
            binding.fragmentStatementsDetailsProviderValue.text = additionalInfo
        }

        val status = navArgs.selectedItem.status
        if (status.isBlank()) {
            binding.fragmentStatementsDetailsStatus.visibility = View.GONE
        } else {
            binding.fragmentStatementsDetailsStatus.visibility = View.VISIBLE
            val statusId =
                resources.getIdentifier(
                    "statements_details_status_label_$status",
                    "string",
                    requireActivity().packageName
                )
            binding.fragmentStatementsDetailsStatus.text =
                if (statusId == 0) status else getString(statusId)
        }

        if (navArgs.selectedItem.transferIdIssuer != null) {
            binding.fragmentStatementsDetailsTransferIdValue.text =
                navArgs.selectedItem.transferIdIssuer
            binding.fragmentStatementsDetailsTransferIdContainer.visibility = View.VISIBLE
        } else {
            binding.fragmentStatementsDetailsTransferIdContainer.visibility = View.GONE
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
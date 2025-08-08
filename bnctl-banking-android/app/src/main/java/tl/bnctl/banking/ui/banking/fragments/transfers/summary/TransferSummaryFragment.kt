package tl.bnctl.banking.ui.banking.fragments.transfers.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import tl.bnctl.banking.R
import tl.bnctl.banking.data.transfers.enums.TransferType
import tl.bnctl.banking.data.transfers.model.TransferConfirmRequest
import tl.bnctl.banking.data.transfers.model.TransferEnd
import tl.bnctl.banking.databinding.FragmentTransferSummaryBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.utils.DialogFactory
import tl.bnctl.banking.ui.utils.NumberUtils

class TransferSummaryFragment : BaseFragment() {

    private var _binding: FragmentTransferSummaryBinding? = null
    private val binding get() = _binding!!

    private val transferSummaryArgs: TransferSummaryFragmentArgs by navArgs()
    private val transferSummaryViewModel: TransferSummaryViewModel
            by viewModels { TransferSummaryViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTransferSummaryBinding.inflate(inflater, container, false)
        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.menu[0].setOnMenuItemClickListener {
            DialogFactory.createConfirmDialog(
                requireContext(),
                R.string.common_dialog_message_cancel_transaction
            ) {
                findNavController().currentDestination?.parent?.let { destination ->
                    findNavController().popBackStack(
                        destination.startDestinationId, false
                    )
                }
            }.show()
            true
        }
        setUpConfirmationFlow()
        populateViewModel()
        populateView()
        return binding.root
    }

    private fun setUpConfirmationFlow() {
        transferSummaryViewModel.creationResult.observe(viewLifecycleOwner) {
            binding.buttonTransferConfirm.isEnabled = true
            if (it.success != null) {
                requireView().findNavController()
                    .navigate(
                        TransferSummaryFragmentDirections.actionNavFragmentSummaryToNavFragmentTransferConfirm(
                            TransferConfirmRequest(
                                it.success.validationRequestId,
                                it.success.objectId
                            ),
                            transferSummaryViewModel.from.value?.accountId
                        )
                    )
            }
            if (it.error != null) run {
                val errorString = it.error.getErrorString()
                val stringId =
                    resources.getIdentifier(errorString, "string", requireActivity().packageName)
                val errorStringId =
                    if (stringId == 0) R.string.error_transfer_creation else stringId
                handleResultError(it.error, errorStringId, false)
                requireView().findNavController()
                    .navigate(
                        TransferSummaryFragmentDirections.actionNavTransferSummaryToNavFragmentTransferEnd(
                            TransferEnd(
                                false,
                                resources.getString(errorStringId)
                            )
                        )
                    )
            }
        }
        binding.buttonTransferConfirm.setOnClickListener {
            binding.buttonTransferConfirm.isEnabled = false
            transferSummaryViewModel.startTransferCreation()
        }
    }

    private fun populateViewModel() {
        transferSummaryViewModel.setAmount(transferSummaryArgs.summary.amount)
        transferSummaryViewModel.setCurrency(transferSummaryArgs.summary.currency)
        transferSummaryViewModel.setFeeAmount(transferSummaryArgs.summary.feeAmount)
        transferSummaryViewModel.setFeeCurrency(transferSummaryArgs.summary.feeCurrency)
        transferSummaryViewModel.setFrom(transferSummaryArgs.summary.from)
        transferSummaryViewModel.setTo(transferSummaryArgs.summary.to)
        transferSummaryViewModel.setReason(transferSummaryArgs.summary.reason)
        transferSummaryViewModel.setTransferType(transferSummaryArgs.summary.transferType)
        transferSummaryViewModel.setExecutionType(transferSummaryArgs.summary.executionType)
        transferSummaryViewModel.setAdditionalDetails(transferSummaryArgs.summary.additionalDetails)
        transferSummaryViewModel.setNewPayee(transferSummaryArgs.summary.newPayee)
    }

    private fun populateView() {
        with(binding) {
            binding.transferAmount.text = NumberUtils.formatAmount(
                transferSummaryViewModel.amount.value!!
            )
            transferCurrency.text = transferSummaryViewModel.currency.value
            sourceAccountName.text = transferSummaryViewModel.from.value?.accountTypeDescription
            sourceAccountNumber.text = transferSummaryViewModel.from.value?.iban
            destinationAccountName.text = transferSummaryViewModel.to.value?.beneficiary?.name
            destinationAccountNumber.text = transferSummaryViewModel.to.value?.accountNumber
            descriptionValue.text = transferSummaryViewModel.reason.value
            sourceAccountBalance.text = requireContext().resources
                .getString(R.string.transfer_summary_label_account_balance).format(
                    NumberUtils.formatAmount(
                        transferSummaryViewModel.from.value?.balance?.available
                    ),
                    transferSummaryViewModel.from.value?.currencyName
                )

            if (transferSummaryViewModel.transferType.value != TransferType.BETWEENACC) {
                destinationAccountBalance.visibility = View.GONE
            } else {
                destinationAccountBalance.visibility = View.VISIBLE
                destinationAccountBalance.text = requireContext().resources
                    .getString(R.string.transfer_summary_label_account_balance).format(
                        NumberUtils.formatAmount(
                            transferSummaryViewModel.to.value?.balance
                        ),
                        transferSummaryViewModel.to.value?.currencyName
                    )

            }
            buttonEditAmount.setOnClickListener {
                findNavController().popBackStack()
            }
            buttonEditSourceAccount.setOnClickListener {
                findNavController().popBackStack()
            }
            buttonEditDestinationAccount.setOnClickListener {
                findNavController().popBackStack()
            }
            buttonEditDescription.setOnClickListener {
                findNavController().popBackStack()
            }
            feeValue.text = String.format(
                "%s %s",
                NumberUtils.formatAmount(
                    transferSummaryViewModel.feeAmount.value
                ),
                transferSummaryViewModel.feeCurrency.value
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

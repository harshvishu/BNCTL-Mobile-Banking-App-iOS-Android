package tl.bnctl.banking.ui.banking.fragments.accounts.additional

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import tl.bnctl.banking.R
import tl.bnctl.banking.databinding.FragmentAccountAdditionalDetailsBinding
import tl.bnctl.banking.ui.BaseFragment

class AccountAdditionalDetailsFragment : BaseFragment() {

    private var _binding: FragmentAccountAdditionalDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountAdditionalDetailsBinding.inflate(inflater, container, false)
        val clipboard: ClipboardManager =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }

        val beneficiary = requireArguments().getString("beneficiary")!!
        binding.beneficiaryValue.text = beneficiary
        binding.buttonCopyBeneficiary.setOnClickListener {
            copyDataToClipboard(beneficiary, clipboard)
        }

        val accountNumber = requireArguments().getString("accountNumber")!!
        binding.accountNumberValue.text = accountNumber
        binding.buttonCopyAccountNumber.setOnClickListener {
            copyDataToClipboard(accountNumber, clipboard)
        }

        val bic = requireArguments().getString("bic")!!
        binding.bicValue.text = bic
        binding.buttonCopyBic.setOnClickListener {
            copyDataToClipboard(bic, clipboard)
        }

        val typeAccount = requireArguments().getString("typeAccount")!!
        binding.accountTypeValue.text = typeAccount

        val currency = requireArguments().getString("currency")!!
        binding.currencyValue.text = currency

        return binding.root
    }

    private fun copyDataToClipboard(dataForCopy: String, clipboard: ClipboardManager) {
        val clip: ClipData = ClipData.newPlainText("copiedData", dataForCopy)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), R.string.toast_message_copied_to_clipboard, Toast.LENGTH_SHORT).show()
    }
}
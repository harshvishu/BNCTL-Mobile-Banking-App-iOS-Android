package tl.bnctl.banking.ui.banking.fragments.cards

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import tl.bnctl.banking.R
import tl.bnctl.banking.data.cards.CardStatus
import tl.bnctl.banking.data.cards.model.Card
import tl.bnctl.banking.databinding.FragmentCardDetailsBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.fragments.cards.transaction_history.CardTransactionHistoryFragment
import tl.bnctl.banking.ui.utils.NumberUtils

class CardDetailsFragment : BaseFragment() {
    companion object {
        const val CARD_ARGUMENT = "card"
    }

    private var _binding: FragmentCardDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var card: Card

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCardDetailsBinding.inflate(layoutInflater, container, false)
        val root: View = binding.root

        card = requireArguments().get(CardTransactionHistoryFragment.CARD_ARGUMENT) as Card

        with(binding) {

            val statusStringResource: Int = when (CardStatus.fromString(card.cardStatus)) {
                CardStatus.ACTIVE -> R.string.cards_status_active
                CardStatus.BLOCKED -> R.string.card_status_blocked
                CardStatus.DEACTIVATED -> R.string.card_status_deactivated
                CardStatus.PRODUCED_NOT_RECEIVED -> R.string.card_status_produced_not_received
                else -> R.string.card_status_unknown
            }

            if (card.cardStatus == "active") {
                tvCardStatus.setTextColor(requireContext().getColor(R.color.green))
            } else {
                tvCardStatus.setTextColor(requireContext().getColor(R.color.red))
            }

            tvCardStatus.text = getString(statusStringResource)
            tvCardNumber.text = card.cardNumber
            tvCardHolder.text = card.cardPrintName
            tvCardOwner.text = "N/A" // TODO: Don't know how to get this
            tvValidThru.text = formatValidThru(card.expiryDate)
            tvLinkedTo.text = card.cardAccountNumber
            tvCurrency.text = card.currency
            tvCardType.text = card.cardProductLabel

            tvCardAvailability.text =
                NumberUtils.formatAmount(card.availableBalance)
            tvApprovedOverdraft.text =
                NumberUtils.formatAmount(card.approvedOverdraft)

            // Labels depend on the card type
            if (card.cardType.equals("debit")) {
                tvLabelApprovedOverdraft.setText(R.string.cards_label_approved_overdraft)
            } else if (card.cardType.equals("credit")) {
                tvLabelApprovedOverdraft.setText(R.string.cards_label_credit_limit)
            }

            toolbar.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        return root;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /**
     * Date is expected as YYYY-MM-DD.
     * Can be done with a date formatter, but let's YOLO it for now
     */
    private fun formatValidThru(expiryDate: String?): String {
        if (expiryDate == null || expiryDate.isBlank()) {
            return ""
        }
        return "${expiryDate.substring(5, 7)}/${expiryDate.substring(2, 4)}"
    }
}

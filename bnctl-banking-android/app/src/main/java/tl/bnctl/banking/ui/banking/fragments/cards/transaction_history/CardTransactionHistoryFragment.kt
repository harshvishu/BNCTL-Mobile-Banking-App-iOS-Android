package tl.bnctl.banking.ui.banking.fragments.cards.transaction_history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.MediatorLiveData
import androidx.navigation.fragment.findNavController
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.cards.model.Card
import tl.bnctl.banking.databinding.FragmentCardTransactionHistoryBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.fragments.statements.StatementsViewModel
import tl.bnctl.banking.ui.banking.fragments.statements.ViewStatementProperty
import tl.bnctl.banking.ui.utils.DateUtils

class CardTransactionHistoryFragment : BaseFragment() {
    companion object {
        const val CARD_ARGUMENT = "card"
    }

    private var _binding: FragmentCardTransactionHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var card: Card

    private val cardTransactionHistoryViewModel: CardTransactionHistoryViewModel by viewModels { CardTransactionHistoryViewModelFactory() }
    private val statementsViewModel: StatementsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupCardStatements()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardTransactionHistoryBinding.inflate(layoutInflater)

        card = requireArguments().get(CARD_ARGUMENT) as Card

        setupBackButton()
        setupLoadingIndicator()
        displayCardStatements()

        return binding.root
    }

    // TODO: Extract this logic to a parent abstract method
    private fun setupLoadingIndicator() {
        with(binding) {
            loadingIndicator.visibility = View.VISIBLE
        }
        val doneLoadingMediatorData = MediatorLiveData<Boolean>()
        doneLoadingMediatorData.value = false
        doneLoadingMediatorData.addSource(cardTransactionHistoryViewModel.isLoadingCardStatements) {
            doneLoadingMediatorData.postValue(!it)
        }
        // Hide loader when everything is done loading
        doneLoadingMediatorData.observe(viewLifecycleOwner) { doneLoading ->
            if (doneLoading) {
                binding.loadingIndicator.visibility = View.GONE
            } else {
                binding.loadingIndicator.visibility = View.VISIBLE
            }
        }
    }

    private fun setupCardStatements() {
        statementsViewModel.raiseShouldFetchStatementsFlag()
        statementsViewModel.showProperty(ViewStatementProperty.TRANSACTION_TYPE)
    }

    private fun displayCardStatements() {
        cardTransactionHistoryViewModel.cardStatement.observe(viewLifecycleOwner) {
            if (it is Result.Error) {
                handleResultError(it, R.string.cards_error_loading_card_transaction_history)
            }
            if (it is Result.Success) {
                statementsViewModel.setAccountStatements(it.data)
            }
        }
        statementsViewModel.shouldFetchStatements.observe(viewLifecycleOwner) {
            if (it) {
                val filter = statementsViewModel.statementsFilter.value!!
                cardTransactionHistoryViewModel.fetchCardStatement(
                    card.accountId!! + '-' + card.cardNumber,
                    DateUtils.formatDateISO(filter.startDate),
                    DateUtils.formatDateISO(filter.endDate)
                )
                statementsViewModel.dropShouldFetchStatementsFlag()
            }
        }
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

    private fun setupBackButton() {
        with(binding) {
            toolbar.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}

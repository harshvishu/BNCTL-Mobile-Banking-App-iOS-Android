package tl.bnctl.banking.ui.banking.fragments.services.utilityBills.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.MediatorLiveData
import androidx.navigation.fragment.findNavController
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.databinding.FragmentUtilityBillHistoryBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.fragments.statements.StatementsViewModel

class UtilityBillHistoryFragment : BaseFragment() {

    private var _binding: FragmentUtilityBillHistoryBinding? = null
    private val binding get() = _binding!!

    private val utilityBillHistoryViewModel: UtilityBillHistoryViewModel by viewModels { UtilityBillHistoryViewModelFactory() }
    private val statementsViewModel: StatementsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUtilityBillHistory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUtilityBillHistoryBinding.inflate(inflater, container, false)
        binding.loadingIndicator.visibility = View.VISIBLE
        setupBackButton()
        setupLoadingIndicator()
        setUpStatements()
        return binding.root
    }

    private fun setupLoadingIndicator() {
        with(binding) {
            loadingIndicator.visibility = View.VISIBLE
        }
        val doneLoadingMediatorData = MediatorLiveData<Boolean>()
        doneLoadingMediatorData.value = false
        doneLoadingMediatorData.addSource(utilityBillHistoryViewModel.isLoadingUtilityBillPaymentHistory) {
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

    private fun setupUtilityBillHistory() {
        statementsViewModel.raiseShouldFetchStatementsFlag()
    }

    private fun setUpStatements() {
        utilityBillHistoryViewModel.billPaymentHistory.observe(viewLifecycleOwner) {
            binding.loadingIndicator.visibility = View.GONE
            if (it is Result.Error) {
                handleResultError(it, R.string.error_loading_transfer_history)
            }
            if (it is Result.Success) {
                statementsViewModel.setAccountStatements(it.data)
            }
        }
        statementsViewModel.shouldFetchStatements.observe(viewLifecycleOwner) {
            if (it) {
                val filter = statementsViewModel.statementsFilter.value!!
                utilityBillHistoryViewModel.fetchBillPayments(filter)
                statementsViewModel.dropShouldFetchStatementsFlag()
            }
        }
    }

    private fun setupBackButton() {
        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
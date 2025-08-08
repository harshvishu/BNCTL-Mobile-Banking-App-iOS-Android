package tl.bnctl.banking.ui.banking.fragments.transfers.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.MediatorLiveData
import androidx.navigation.fragment.findNavController
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.databinding.FragmentTransferHistoryBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.fragments.statements.StatementsViewModel
import tl.bnctl.banking.ui.banking.fragments.statements.adapter.StatementsFilterData

class TransferHistoryFragment : BaseFragment() {

    private var _binding: FragmentTransferHistoryBinding? = null
    private val binding get() = _binding!!

    private val transferHistoryViewModel: TransferHistoryViewModel by viewModels { TransferHistoryViewModelFactory() }
    private val statementsViewModel: StatementsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransferHistory()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransferHistoryBinding.inflate(inflater, container, false)

        setupBackButton()
        setupLoadingIndicator()
        displayTransferHistory()

        return binding.root
    }

    private fun setupLoadingIndicator() {
        with(binding) {
            loadingIndicator.visibility = View.VISIBLE
        }
        val doneLoadingMediatorData = MediatorLiveData<Boolean>()
        doneLoadingMediatorData.value = false
        doneLoadingMediatorData.addSource(transferHistoryViewModel.isLoadingTransferHistory) {
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

    private fun setupTransferHistory() {
        statementsViewModel.raiseShouldFetchStatementsFlag()
    }

    private fun displayTransferHistory() {
        transferHistoryViewModel.transferHistory.observe(viewLifecycleOwner) {
            statementsViewModel.dropFetchingStatementsFlag()
            binding.loadingIndicator.visibility = View.GONE
            if (it is Result.Error) {
                handleResultError(it, R.string.error_loading_transfer_history)
            }
            if (it is Result.Success) {
                statementsViewModel.setAccountStatements(it.data.records)
                statementsViewModel.setPagination(it.data.pagination)
                if (it.data.pagination.totalPages > 1) {
                    statementsViewModel.showLoadMoreButton()
                } else {
                    statementsViewModel.hideLoadMoreButton()
                }
            }
        }
        statementsViewModel.shouldFetchStatements.observe(viewLifecycleOwner) {
            if (it) {
                binding.loadingIndicator.visibility = View.VISIBLE
                val filter = statementsViewModel.statementsFilter.value!!
                transferHistoryViewModel.fetchTransferHistory(filter)
                statementsViewModel.raiseFetchingStatementsFlag()
                statementsViewModel.dropShouldFetchStatementsFlag()
            }
        }
        statementsViewModel.shouldFetchNextStatementsPage.observe(viewLifecycleOwner) {
            if (it) {
                binding.loadingIndicator.visibility = View.VISIBLE
                val filter = statementsViewModel.statementsFilter.value!!
                val nextPageFilter = StatementsFilterData(
                    filter.startDate,
                    filter.endDate,
                    filter.pageNumber + 1,
                    filter.pageSize
                )
                statementsViewModel.setFilter(nextPageFilter)
                statementsViewModel.raiseFetchingStatementsFlag()
                transferHistoryViewModel.fetchTransferHistory(nextPageFilter, true)
                statementsViewModel.dropShouldFetchNextStatementsPageFlag()
            }
        }
        transferHistoryViewModel.transferHistoryPage.observe(viewLifecycleOwner) {
            binding.loadingIndicator.visibility = View.GONE
            statementsViewModel.dropFetchingStatementsFlag()
            if (it is Result.Error) {
                handleResultError(it, R.string.error_loading_transfer_history)
            }
            if (it is Result.Success) {
                statementsViewModel.addAccountStatementsPage(it.data.records)
                statementsViewModel.setPagination(it.data.pagination)

                if (it.data.pagination.pageNumber < it.data.pagination.totalPages) {
                    statementsViewModel.showLoadMoreButton()
                } else {
                    statementsViewModel.hideLoadMoreButton()
                }

            }
        }
        statementsViewModel.loadMoreButtonVisible.observe(viewLifecycleOwner) {
            if (it) {
                binding.loadMoreTransfers.visibility = View.VISIBLE
            } else {
                binding.loadMoreTransfers.visibility = View.GONE
            }
        }
        binding.loadMoreTransfers.setOnClickListener {
            statementsViewModel.raiseShouldFetchNextStatementsPageFlag()
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
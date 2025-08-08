package tl.bnctl.banking.ui.banking.fragments.statements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import tl.bnctl.banking.R
import tl.bnctl.banking.data.accounts.model.StatementsDataHolder
import tl.bnctl.banking.databinding.FragmentStatementsBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.fragments.statements.adapter.StatementsFilterData
import tl.bnctl.banking.ui.banking.fragments.statements.adapter.StatementsViewAdapter
import tl.bnctl.banking.ui.banking.fragments.statements.filter.StatementsFilterFragmentDirections
import tl.bnctl.banking.ui.utils.DateUtils

/**
 * This fragment will display all the statements that are provided to it using the StatementsViewModel
 * Note StatementsFragment does not make the requests for the statements data and this has to be provided by the parent Fragment
 * also since that's the flow it's recommended to use shouldFetchStatements flag from StatementsViewModel to see if refresh of the data is needed
 * This is recommended since the filter information gets loaded after the parent fragment is created and it'll create additional request
 */
abstract class StatementsFragment : BaseFragment() {

    private var _binding: FragmentStatementsBinding? = null
    private val binding get() = _binding!!

//    private val statementsViewModel: StatementsViewModel by viewModels({ requireParentFragment() })

    protected lateinit var statementsViewModel: StatementsViewModel
    protected abstract fun initStatementsViewModel()

    /**
     * This method will be called from within the view adapter when it encounters a null element.
     * The null element is added from the DataSource if the backend returns more than one page
     * Not the best design, but beats having to count elements and track scrolling events
     */
    protected abstract fun onNextPageRequested()

    private lateinit var statementsViewAdapter: StatementsViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initStatementsViewModel()
        _binding = FragmentStatementsBinding.inflate(inflater, container, false)
        statementsViewAdapter = StatementsViewAdapter(
            statementsViewModel.isDetailedViewEnabled.value!!,
            statementsViewModel.detailedViewTitleResource.value!!,
            statementsViewModel.shouldViewStatementProperty.value!!
        ) { onNextPageRequested() }

        binding.fragmentStatementsRecyclerView.adapter = statementsViewAdapter
        binding.fragmentStatementsRecyclerView.layoutAnimation = null

        // Set up the RecyclerView
        val layoutManager = LinearLayoutManager(requireContext())
        binding.fragmentStatementsRecyclerView.layoutManager = layoutManager

        displayFilter(statementsViewModel.statementsFilter.value!!)
        setUpStatements()
        setUpStatementsFilter()
        return binding.root
    }

    private fun setUpStatementsFilter() {
        binding.fragmentStatementsFilterButton.setOnClickListener {
            requireView().findNavController()
                .navigate(
                    StatementsFilterFragmentDirections.actionGlobalNavFragmentStatementsFilter(
                        StatementsFragment::class.simpleName.toString(),
                        statementsViewModel.statementsFilter.value
                    )
                )
        }
        requireParentFragment().parentFragmentManager.setFragmentResultListener(
            StatementsFragment::class.simpleName.toString(),
            viewLifecycleOwner
        ) { _, result ->
            result.getParcelable<StatementsFilterData>("filter")?.let {
                statementsViewModel.setFilter(it)
                statementsViewModel.raiseShouldFetchStatementsFlag()
            }
        }
        statementsViewModel.statementsFilter.observe(viewLifecycleOwner) {
            displayFilter(it)
        }
    }

    private fun setUpStatements() {
        statementsViewModel.allStatements.observe(viewLifecycleOwner) {
            statementsViewModel.dropFetchingStatementsFlag()
            if (it.isEmpty()) {
                binding.fragmentStatementsNoItemsMessage.visibility = View.VISIBLE
                binding.fragmentStatementsRecyclerView.visibility = View.GONE
            } else {
                binding.fragmentStatementsNoItemsMessage.visibility = View.GONE
                binding.fragmentStatementsRecyclerView.visibility = View.VISIBLE
                statementsViewAdapter.submitList(it as List<StatementsDataHolder?>)
            }
        }
        statementsViewModel.statementsPage.observe(viewLifecycleOwner) {
            statementsViewModel.dropFetchingStatementsFlag()
            if (it.isNotEmpty()) {
                // Add next statements page to the adapter

                val newStatementsList = mutableListOf<StatementsDataHolder?>()
                val oldStatementsList = statementsViewModel.allStatements.value!!
                newStatementsList.addAll(oldStatementsList)
                newStatementsList.addAll(it as List<StatementsDataHolder?>)
                statementsViewModel.setAccountStatements(newStatementsList)
            }
        }
    }

    private fun displayFilter(filterData: StatementsFilterData) {
        val startDate = DateUtils.formatDate(requireContext(), filterData.startDate)
        val endDate = DateUtils.formatDate(requireContext(), filterData.endDate)
        binding.fragmentStatementsFilterRange.text = requireContext().resources
            .getString(R.string.statements_range).format(
                startDate,
                endDate
            )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
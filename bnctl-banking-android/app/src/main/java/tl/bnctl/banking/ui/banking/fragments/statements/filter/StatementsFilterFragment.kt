package tl.bnctl.banking.ui.banking.fragments.statements.filter

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import tl.bnctl.banking.databinding.FragmentStatementsFilterBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.fragments.statements.adapter.StatementsFilterData
import tl.bnctl.banking.ui.utils.DateUtils
import java.util.*

/**
 * The information for the filter is sent using the parentFragmentManager
 * Make sure the fragment which will listen for the response is on the same level as this fragment
 * The response is going to be sent to the resultKey provided in the navArgs
 *
 * Required navArgs(filter: StatementsFilterData, resultKey: String)
 */
class StatementsFilterFragment : BaseFragment() {

    private var _binding: FragmentStatementsFilterBinding? = null
    private val binding get() = _binding!!

    private val statementsFilterFragmentArgs: StatementsFilterFragmentArgs by navArgs()

    private val statementsFilterViewModel: StatementsFilterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatementsFilterBinding.inflate(inflater, container, false)

        populateViewModel()
        setupStartDate()
        setupEndDate()
        setupApplyFilters()
        setupBackButton()

        statementsFilterViewModel.statementsFilter.observe(viewLifecycleOwner) {
            binding.startDate.text = DateUtils.formatDate(requireContext(), it.startDate)
            binding.endDate.text = DateUtils.formatDate(requireContext(), it.endDate)
        }

        return binding.root
    }

    private fun populateViewModel() {
        statementsFilterFragmentArgs.filter?.let { statementsFilterViewModel.initFilter(it) }
        statementsFilterViewModel.setResultKey(statementsFilterFragmentArgs.resultKey)
    }

    private fun getFilterStartDate(): Calendar {
        val date = Calendar.getInstance()
        date.time = statementsFilterViewModel
            .statementsFilter.value!!.startDate
        return date
    }

    private fun getFilterEndDate(): Calendar {
        val date = Calendar.getInstance()
        date.time = statementsFilterViewModel
            .statementsFilter.value!!.endDate
        return date
    }

    private fun setupStartDate() {
        binding.startDate.text = DateUtils.formatDate(
            requireContext(),
            statementsFilterViewModel.statementsFilter.value!!.startDate
        )
        val onDateSelectListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val newStartDate = Calendar.getInstance()
            newStartDate.set(Calendar.YEAR, year)
            newStartDate.set(Calendar.MONTH, month)
            newStartDate.set(Calendar.DATE, day)
            statementsFilterViewModel.updateStartDate(newStartDate.time)
        }
        val startDateLayout = binding.startDateLayout
        startDateLayout.isClickable = true
        startDateLayout.setOnClickListener {
            val startDate = getFilterStartDate()
            val picker = DatePickerDialog(
                requireActivity(),
                onDateSelectListener,
                startDate.get(Calendar.YEAR),
                startDate.get(Calendar.MONTH),
                startDate.get(Calendar.DATE)
            )
            Calendar.getInstance().time.also { picker.datePicker.maxDate = it.time }
            picker.show()
        }

        statementsFilterViewModel.startDateError.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.startDateError.text = resources.getString(it)
                binding.startDateError.visibility = View.VISIBLE
            } else {
                binding.startDateError.text = null
                binding.startDateError.visibility = View.GONE
            }
        }
    }

    private fun setupEndDate() {
        binding.endDate.text = DateUtils.formatDate(
            requireContext(),
            statementsFilterViewModel.statementsFilter.value!!.endDate
        )

        val onDateSelectListener =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                val newEndDate = Calendar.getInstance()
                newEndDate.set(Calendar.YEAR, year)
                newEndDate.set(Calendar.MONTH, month)
                newEndDate.set(Calendar.DATE, day)
                statementsFilterViewModel.updateEndDate(newEndDate.time)
            }
        val endDateLayout = binding.endDateLayout
        endDateLayout.isClickable = true
        endDateLayout.setOnClickListener {
            val endDate = getFilterEndDate()
            val picker = DatePickerDialog(
                requireActivity(),
                onDateSelectListener,
                endDate.get(Calendar.YEAR),
                endDate.get(Calendar.MONTH),
                endDate.get(Calendar.DATE)
            )
            if (
                statementsFilterViewModel.statementsFilter.value != null
            ) {
                picker.datePicker.minDate =
                    statementsFilterViewModel.statementsFilter.value!!.startDate.time
            }
            Calendar.getInstance().time.also { picker.datePicker.maxDate = it.time }
            picker.show()
        }

        statementsFilterViewModel.endDateError.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.endDateError.text = resources.getString(it)
                binding.endDateError.visibility = View.VISIBLE
            } else {
                binding.endDateError.text = null
                binding.endDateError.visibility = View.GONE
            }
        }
    }

    private fun setupBackButton() {
        binding.toolbar.menu[0].setOnMenuItemClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupApplyFilters() {
        binding.applyFiltersButton.setOnClickListener {
            if (validateDates()) {
                val bundle = Bundle()
                val filter = statementsFilterViewModel.statementsFilter.value!!
                bundle.putParcelable(
                    "filter", StatementsFilterData(
                        filter.startDate,
                        filter.endDate,
                        1, // Resetting page number
                        filter.pageSize
                    )
                )
                parentFragmentManager.setFragmentResult(
                    statementsFilterViewModel.resultKey.value.toString(),
                    bundle
                )
                findNavController().popBackStack()
            }
        }
    }

    private fun validateDates(): Boolean {
        statementsFilterViewModel.validateStartDate()
        statementsFilterViewModel.validateEndDate()

        return statementsFilterViewModel.startDateError.value == null
                && statementsFilterViewModel.endDateError.value == null
    }

}
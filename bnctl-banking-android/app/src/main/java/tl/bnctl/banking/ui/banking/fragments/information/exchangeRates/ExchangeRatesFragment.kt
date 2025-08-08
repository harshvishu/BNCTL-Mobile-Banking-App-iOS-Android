package tl.bnctl.banking.ui.banking.fragments.information.exchangeRates

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.databinding.FragmentExchangeRatesBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.fragments.information.exchangeRates.adapter.ExchangeRatesViewAdapter
import java.util.*

class ExchangeRatesFragment : BaseFragment() {

    private var _binding: FragmentExchangeRatesBinding? = null
    private val binding get() = _binding!!
    private lateinit var exchangeRateViewModel: ExchangeRatesViewModel

    private var isLoadingExchangeRates = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exchangeRateViewModel = ViewModelProvider(
            this,
            ExchangeRatesViewModelFactory()
        ).get(ExchangeRatesViewModel::class.java);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExchangeRatesBinding.inflate(inflater, container, false)
        setupBackButton()
        setupRefreshButton()
        setupCalendarButton()
        displayExchangeRates()

        return binding.root
    }

    private fun displayExchangeRates() {
        val exchangeRatesViewAdapter = ExchangeRatesViewAdapter(
            exchangeRateViewModel.exchangeRates, requireContext()
        )
        binding.exchangeRatesRecyclerView.adapter = exchangeRatesViewAdapter
        isLoadingExchangeRates = true
        setupLoadingIndicator()

        exchangeRateViewModel.date.observe(viewLifecycleOwner, {
            exchangeRateViewModel.getExchangeRates()
        })
        exchangeRateViewModel.exchangeRates.observe(viewLifecycleOwner, {
            isLoadingExchangeRates = false
            setupLoadingIndicator()
            if (it == null || it is Result.Error) {
                handleResultError(it as Result.Error, R.string.error_loading_exchange_rates)
                binding.exchangeRatesNoData.visibility = View.VISIBLE
                binding.exchangeRatesRecyclerView.visibility = View.GONE
                return@observe
            }
            if ((it as Result.Success).data.isEmpty()) {
                binding.exchangeRatesNoData.visibility = View.VISIBLE
                binding.exchangeRatesRecyclerView.visibility = View.GONE
            } else {
                exchangeRatesViewAdapter.notifyDataSetChanged()
                binding.exchangeRatesNoData.visibility = View.GONE
                binding.exchangeRatesRecyclerView.visibility = View.VISIBLE
            }
        })
    }

    private fun setupBackButton() {
        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRefreshButton() {
        binding.refreshButton.setOnClickListener {
            isLoadingExchangeRates = true
            setupLoadingIndicator()
            exchangeRateViewModel.getExchangeRates()
        }
    }

    private val onDateSelectListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
        val date = Calendar.getInstance()
        date.set(Calendar.YEAR, year)
        date.set(Calendar.MONTH, month)
        date.set(Calendar.DATE, day)
        isLoadingExchangeRates = true
        setupLoadingIndicator()
        exchangeRateViewModel.updateDate(date.time)
    }

    private fun setupCalendarButton() {
        binding.toolbar.menu[0].setOnMenuItemClickListener {
            val date = exchangeRateViewModel.date.value!!
            val cal = Calendar.getInstance()
            cal.time = date
            val picker = DatePickerDialog(
                requireActivity(),
                onDateSelectListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DATE)
            )
            Calendar.getInstance().time.also { picker.datePicker.maxDate = it.time }
            picker.show()
            true
        }
    }

    private fun setupLoadingIndicator() {
        // TODO If possible find a better way to control the loading indicator
        if (!isLoadingExchangeRates) {
            binding.loadingIndicator.visibility = View.GONE
        } else {
            binding.loadingIndicator.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

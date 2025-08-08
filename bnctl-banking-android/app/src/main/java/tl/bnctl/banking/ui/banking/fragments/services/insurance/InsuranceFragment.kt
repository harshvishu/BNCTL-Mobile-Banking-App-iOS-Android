package tl.bnctl.banking.ui.banking.fragments.services.insurance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.databinding.FragmentInsuranceBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.fragments.services.insurance.adapters.InsuranceViewAdapter

class InsuranceFragment : BaseFragment() {

    private var _binding: FragmentInsuranceBinding? = null
    private val binding get() = _binding!!

    private val insuranceViewModel: InsuranceViewModel by navGraphViewModels(R.id.nav_fragment_insurance) { InsuranceViewModelFactory() }
    private lateinit var insuranceViewAdapter: InsuranceViewAdapter

    private var isLoadingInsurances = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        insuranceViewAdapter = InsuranceViewAdapter(insuranceViewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInsuranceBinding.inflate(inflater, container, false)
        setupBackButton()
        displayInsurances()

        return binding.root
    }

    private fun displayInsurances() {
        binding.insurancesRecyclerView.adapter = insuranceViewAdapter
        isLoadingInsurances = true
        setupLoadingIndicator()
        insuranceViewModel.fetchInsurances()
        insuranceViewModel.insurances.observe(viewLifecycleOwner) {
            isLoadingInsurances = false
            setupLoadingIndicator()
            if (it == null || it is Result.Error) {
                val error = it as Result.Error
                handleResultError(error, R.string.error_loading_insurances)
                binding.insurancesNoData.visibility = View.VISIBLE
                binding.insurancesRecyclerView.visibility = View.GONE
                return@observe
            }
            if ((it as Result.Success).data.isEmpty()) {
                binding.insurancesNoData.visibility = View.VISIBLE
                binding.insurancesRecyclerView.visibility = View.GONE
            } else {
                insuranceViewAdapter.notifyDataSetChanged()
                binding.insurancesNoData.visibility = View.GONE
                binding.insurancesRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun setupBackButton() {
        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupLoadingIndicator() {
        if (!isLoadingInsurances) {
            binding.loadingIndicator.visibility = View.GONE
        } else {
            binding.loadingIndicator.visibility = View.VISIBLE
        }
    }
}

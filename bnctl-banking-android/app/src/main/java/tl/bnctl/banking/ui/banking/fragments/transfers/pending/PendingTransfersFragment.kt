package tl.bnctl.banking.ui.banking.fragments.transfers.pending

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.databinding.FragmentPendingTransfersBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.fragments.transfers.pending.adapters.PendingTransfersViewAdapter

class PendingTransfersFragment : BaseFragment() {

    private var _binding: FragmentPendingTransfersBinding? = null
    private val binding get() = _binding!!

    private val pendingTransfersViewModel: PendingTransfersViewModel by navGraphViewModels(R.id.nav_graph_transfers) { PendingTransfersViewModelFactory() }
    private lateinit var pendingTransfersViewAdapter: PendingTransfersViewAdapter
    private var isLoadingPendingTransfers = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pendingTransfersViewAdapter = PendingTransfersViewAdapter(
            pendingTransfersViewModel, pendingTransfersViewModel.pendingTransfers
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPendingTransfersBinding.inflate(inflater, container, false)
        pendingTransfersViewModel.numSelectedPendingTransfers.observe(viewLifecycleOwner) {
            val areButtonsEnabled = it > 0
            binding.transferPendingRejectButton.isEnabled = areButtonsEnabled
            binding.transferPendingConfirmButton.isEnabled = areButtonsEnabled
        }
        setupBackButton()
        binding.selectAll.setOnClickListener {
            if (it is CheckBox) {
                if (it.isChecked) {
                    pendingTransfersViewModel.selectAllPendingTransfers()

                } else {
                    pendingTransfersViewModel.unselectAllPendingTransfers()
                }
            }
        }
        displayPendingTransfers()
        setupActionButtons()

        return binding.root
    }

    private fun displayPendingTransfers() {
        binding.transfersPendingRecyclerView.adapter = pendingTransfersViewAdapter
        isLoadingPendingTransfers = true
        setupLoadingIndicator()
        pendingTransfersViewModel.fetchPendingTransfers()
        pendingTransfersViewModel.pendingTransfers.observe(viewLifecycleOwner) {
            isLoadingPendingTransfers = false
            setupLoadingIndicator()
            if (it == null || it is Result.Error) {
                val error = it as Result.Error
                handleResultError(error, R.string.error_loading_pending_transfers)
                binding.pendingTransfersNoData.visibility = View.VISIBLE
                binding.transfersPendingRecyclerView.visibility = View.GONE
                return@observe
            }
            if ((it as Result.Success).data.isEmpty()) {
                binding.pendingTransfersNoData.visibility = View.VISIBLE
                binding.transfersPendingRecyclerView.visibility = View.GONE
            } else {
                pendingTransfersViewAdapter.notifyDataSetChanged()
                binding.pendingTransfersNoData.visibility = View.GONE
                binding.transfersPendingRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    private fun setupBackButton() {
        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupActionButtons() {
        binding.transferPendingConfirmButton.setOnClickListener {
            pendingTransfersViewModel.confirmSelectedPendingTransfers()
            requireView().findNavController()
                .navigate(
                    PendingTransfersFragmentDirections.actionNavFragmentTransferPendingToNavFragmentTransferConfirm(
                        "confirm"
                    )
                )
        }

        binding.transferPendingRejectButton.setOnClickListener {
            pendingTransfersViewModel.rejectSelectedPendingTransfers()
            requireView().findNavController()
                .navigate(
                    PendingTransfersFragmentDirections.actionNavFragmentTransferPendingToNavFragmentTransferConfirm(
                        "reject"
                    )
                )
        }
    }

    private fun setupLoadingIndicator() {
        if (!isLoadingPendingTransfers) {
            binding.loadingIndicator.visibility = View.GONE
        } else {
            binding.loadingIndicator.visibility = View.VISIBLE
        }
    }

}
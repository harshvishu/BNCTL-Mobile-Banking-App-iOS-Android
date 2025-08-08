package tl.bnctl.banking.ui.banking.fragments.transfers.end

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import tl.bnctl.banking.R
import tl.bnctl.banking.databinding.FragmentTransferEndBinding
import tl.bnctl.banking.ui.BaseFragment

open class TransferEndFragment : BaseFragment() {

    private var _binding: FragmentTransferEndBinding? = null
    protected val binding get() = _binding!!

    private val transferEndFragmentArgs: TransferEndFragmentArgs by navArgs()

    private val transferEndViewModel: TransferEndViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransferEndBinding.inflate(inflater, container, false)
        transferEndViewModel.displayMessage.observe(viewLifecycleOwner) {
            binding.transferEndMessage.text = it
        }
        transferEndViewModel.transferSuccessful.observe(viewLifecycleOwner) {
            if (it) {
                binding.transferEndIcon.setImageResource(R.drawable.ic_success)
                if (transferEndFragmentArgs.showTransferEndSubMessage &&
                    !transferEndFragmentArgs.transferEndSubMessage.isNullOrBlank()
                ) {
                    binding.transferEndSubMessage.visibility = View.VISIBLE
                    binding.transferEndSubMessage.text =
                        transferEndFragmentArgs.transferEndSubMessage
                } else {
                    binding.transferEndSubMessage.visibility = View.INVISIBLE
                }
            } else {
                binding.transferEndIcon.setImageResource(R.drawable.ic_error)
                binding.transferEndSubMessage.visibility = View.INVISIBLE
            }
        }
        binding.buttonOk.setOnClickListener {
            requireView().findNavController()
                .navigate(
                    TransferEndFragmentDirections.globalNavFragmentTransferHistory()
                )
        }
        populateViewModel()
        return binding.root
    }

    private fun populateViewModel() {
        transferEndViewModel.setTransferSuccessful(transferEndFragmentArgs.transferEnd.transferSuccess)
        transferEndViewModel.setMessageToDisplay(transferEndFragmentArgs.transferEnd.message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
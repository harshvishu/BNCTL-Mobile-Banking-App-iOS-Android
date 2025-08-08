package tl.bnctl.banking.ui.banking.fragments.transfers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import tl.bnctl.banking.R
import tl.bnctl.banking.databinding.FragmentTransfersBinding
import tl.bnctl.banking.ui.banking.fragments.services.BasePermissionFragment
import tl.bnctl.banking.ui.banking.fragments.transfers.create.screens.betweenacc.TransferBetweenOwnAccountsFragment
import tl.bnctl.banking.ui.banking.fragments.transfers.create.screens.iban.IbanTransferFragment
import tl.bnctl.banking.ui.banking.fragments.transfers.create.screens.intrabank.InternalTransferFragment
import tl.bnctl.banking.ui.banking.fragments.transfers.pending.PendingTransfersFragment
import tl.bnctl.banking.ui.banking.fragments.transfers.templates.TemplatesFragment

class TransfersFragment : BasePermissionFragment() {

    private var _binding: FragmentTransfersBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransfersBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val keyToViewId = mapOf(
            TransferBetweenOwnAccountsFragment::class.java.toString() to binding.transfersBetweenAccountsMenuItem.id.toString(),
            IbanTransferFragment::class.java.toString() to binding.transfersToNationalIbanMenuItem.id.toString(),
            InternalTransferFragment::class.java.toString() to binding.transfersInternalMenuItem.id.toString(),
            TemplatesFragment::class.java.toString() to binding.transfersTransferToPayeeMenuItem.id.toString(),
            PendingTransfersFragment::class.java.toString() to binding.transfersPendingMenuItem.id.toString(),
        )
        setupViewsToPermission(keyToViewId, container!!)

        binding.transfersBetweenAccountsMenuItem.setOnClickListener { view ->
            view.findNavController()
                .navigate(R.id.action_nav_transfers_to_nav_fragment_transfer_between_own_accounts)
        }
        binding.transfersToNationalIbanMenuItem.setOnClickListener { view ->
            view.findNavController()
                .navigate(R.id.action_nav_fragment_transfers_to_nav_fragment_iban_transfer)
        }
        binding.transfersInternalMenuItem.setOnClickListener { view ->
            view.findNavController()
                .navigate(R.id.action_nav_transfers_to_nav_fragment_internal_transfer)
        }
        binding.transfersTransferToPayeeMenuItem.setOnClickListener { view ->
            view.findNavController()
                .navigate(
                    TransfersFragmentDirections.actionNavTransfersToNavFragmentTemplates(true)
                )
        }
        binding.transfersPendingMenuItem.setOnClickListener { view ->
            view.findNavController()
                .navigate(R.id.action_nav_transfers_to_nav_fragment_pending_transfers)
        }
        binding.transfersHistoryMenuItem.setOnClickListener { view ->
            view.findNavController()
                .navigate(R.id.action_nav_fragment_transfers_to_nav_fragment_transfer_history)
        }
        return root
    }
}

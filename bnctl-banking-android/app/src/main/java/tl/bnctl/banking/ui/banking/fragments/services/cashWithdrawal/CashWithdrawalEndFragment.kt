package tl.bnctl.banking.ui.banking.fragments.services.cashWithdrawal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import tl.bnctl.banking.ui.banking.fragments.transfers.end.TransferEndFragment

class CashWithdrawalEndFragment : TransferEndFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        binding.buttonOk.setOnClickListener {
            requireView().findNavController()
                .navigate(
                    CashWithdrawalEndFragmentDirections.actionNavFragmentCashWithdrawalEndToNavFragmentServices(
                    )
                )
        }
        return view
    }
}
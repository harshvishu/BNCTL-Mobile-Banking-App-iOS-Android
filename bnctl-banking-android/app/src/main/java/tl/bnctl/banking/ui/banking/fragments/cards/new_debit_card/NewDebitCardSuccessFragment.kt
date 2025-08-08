package tl.bnctl.banking.ui.banking.fragments.cards.new_debit_card

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import tl.bnctl.banking.databinding.FragmentNewDebitCardSuccessBinding
import tl.bnctl.banking.ui.BaseFragment

class NewDebitCardSuccessFragment : BaseFragment() {

    private var _binding: FragmentNewDebitCardSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewDebitCardSuccessBinding.inflate(layoutInflater, container, false)

        binding.newDebitCardSuccessOkButton.setOnClickListener {
            requireView().findNavController()
                .navigate(
                    NewDebitCardSuccessFragmentDirections
                        .navFragmentNewDebitCardSuccessToNavFragmentCards()
                )
        }
        return binding.root
    }
}
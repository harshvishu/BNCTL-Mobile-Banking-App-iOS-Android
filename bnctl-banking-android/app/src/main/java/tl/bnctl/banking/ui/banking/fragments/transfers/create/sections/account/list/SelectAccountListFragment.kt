package tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.account.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import tl.bnctl.banking.data.accounts.model.Account
import tl.bnctl.banking.databinding.FragmentSelectAccountListBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.fragments.transfers.create.adapters.SelectAccountAdapter

/**
 * This fragment's purpose is to show a list from provided account with the option to select one.
 * The fragment uses direction to receive arguments and fragment result api to return response.
 * Use SelectAccountListFragmentDirections.actionGlobalNavFragmentSelectAccountList method
 * pass along the accounts for selection and the result key on which the other fragment will expect the result.
 */
class SelectAccountListFragment : BaseFragment() {

    private var _binding: FragmentSelectAccountListBinding? = null
    private val binding get() = _binding!!

    // Expect the available accounts for selection passes using the args
    private val selectAccountListFragmentArgs: SelectAccountListFragmentArgs by navArgs()

    private val selectAccountListViewModel: SelectAccountListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSelectAccountListBinding.inflate(inflater, container, false)
        binding.selectAccountListTitle.text =
            resources.getText(selectAccountListFragmentArgs.fragmentTitle)
        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }
        selectAccountListViewModel.adapter.observe(viewLifecycleOwner) {
            binding.selectAccountList.adapter = it
        }
        populateViewModel()
        return binding.root
    }

    private fun populateViewModel() {
        selectAccountListViewModel.setAdapter(
            SelectAccountAdapter(
                selectAccountListFragmentArgs.accounts.toList(),
                onAccountClickHandler
            )
        )
        selectAccountListViewModel.setResultKey(selectAccountListFragmentArgs.resultKey)
    }

    // Return response to the fragment that has sent the data via the fragment result api and popBackStack
    // This allows this fragment to communicate freely from any nav graph
    // Note: The fragment that will communicate with this fragment will need to be on the same level of fragment management
    private val onAccountClickHandler: (Account) -> Unit = {
        val bundle = Bundle()
        bundle.putParcelable("selectedAccount", it)
        parentFragmentManager.setFragmentResult(
            selectAccountListViewModel.resultKey.value.toString(),
            bundle
        )
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
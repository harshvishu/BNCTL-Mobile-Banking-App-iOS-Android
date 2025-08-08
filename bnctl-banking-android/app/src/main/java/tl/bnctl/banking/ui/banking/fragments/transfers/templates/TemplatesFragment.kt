package tl.bnctl.banking.ui.banking.fragments.transfers.templates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.templates.model.Template
import tl.bnctl.banking.data.transfers.enums.TransferType
import tl.bnctl.banking.databinding.FragmentTemplatesBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.fragments.transfers.templates.adapter.TemplatesViewAdapter

class TemplatesFragment : BaseFragment() {

    private var _binding: FragmentTemplatesBinding? = null
    private val binding get() = _binding!!

    private val templatesViewModel: TemplatesViewModel
            by viewModels { TemplatesViewModelFactory() }
    private lateinit var templatesViewAdapter: TemplatesViewAdapter

    private val navArgs: TemplatesFragmentArgs by navArgs()

    companion object {
        val TAG: String = TemplatesFragment::class.java.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTemplatesBinding.inflate(inflater, container, false)
        templatesViewAdapter =
            TemplatesViewAdapter(
                templatesViewModel.templates,
                requireContext(),
                onTemplateClickHandler
            )
        binding.templatesRecyclerView.adapter = templatesViewAdapter
        // Setting this to null, because otherwise the app crashes when deleting a template
        binding.templatesRecyclerView.itemAnimator = null

        setupToolbar()
        setupLoadingIndicator()
        fetchTemplates()

        return binding.root
    }

    private fun setupToolbar() {
        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.toolbar.menu[0].setOnMenuItemClickListener {
            findNavController().navigate(
                TemplatesFragmentDirections.actionNavFragmentTemplatesToNavFragmentCreateTemplate()
            )
            true
        }
    }

    private fun setupLoadingIndicator() {
        binding.loadingIndicator.visibility = View.VISIBLE
    }

    private fun fetchTemplates() {
        templatesViewModel.templateFetch()
        templatesViewModel.templates.observe(viewLifecycleOwner) {
            binding.loadingIndicator.visibility = View.GONE
            if (it == null || it is Result.Error) {
                handleResultError(it as Result.Error, R.string.error_loading_templates)
            } else {
                if ((it as Result.Success).data.isEmpty()) {
                    binding.templatesNoData.visibility = View.VISIBLE
                }
                templatesViewAdapter.notifyItemRangeChanged(0, it.data.size)
            }
        }
    }

    private val onTemplateClickHandler: (Template) -> Unit = {
        if (navArgs.directToTransferCreation) {
            navigateToTransferCreation(it)
        } else {
            findNavController().navigate(
                TemplatesFragmentDirections.actionNavFragmentTemplatesToNavFragmentTemplateDetails(
                    it
                )
            )
        }
    }

    private fun navigateToTransferCreation(template: Template) {
        when (template.transferType) {
            TransferType.BETWEENACC.type -> {
                findNavController().navigate(
                    TemplatesFragmentDirections.actionNavFragmentTemplatesToNavFragmentTransferBetweenOwnAccounts(
                        template
                    )
                )
            }
            TransferType.INTRABANK.type -> findNavController().navigate(
                TemplatesFragmentDirections.actionNavFragmentTemplatesToNavFragmentInternalTransfer(
                    template
                )
            )
            TransferType.INTERBANK.type -> findNavController().navigate(
                TemplatesFragmentDirections.actionNavFragmentTemplatesToNavFragmentIbanTransfer(
                    template
                )
            )
            else -> {
                // Do nothing
            }
        }
    }

}
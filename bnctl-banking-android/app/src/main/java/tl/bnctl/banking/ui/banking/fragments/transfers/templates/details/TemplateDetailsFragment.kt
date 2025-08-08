package tl.bnctl.banking.ui.banking.fragments.transfers.templates.details

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import tl.bnctl.banking.R
import tl.bnctl.banking.data.templates.model.Template
import tl.bnctl.banking.databinding.FragmentTemplateDetailsBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.fragments.transfers.templates.TemplatesViewModelFactory
import tl.bnctl.banking.ui.utils.DialogFactory

class TemplateDetailsFragment : BaseFragment() {

    private var _binding: FragmentTemplateDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var modalBottomSheet: TemplateDetailsModalBottomSheet
    private lateinit var template: Template

    private val detailsViewModel: TemplateDetailsViewModel by viewModels { TemplatesViewModelFactory() }

    private lateinit var loadingDialog: AlertDialog

    companion object {
        val TAG: String = TemplateDetailsFragment::class.java.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTemplateDetailsBinding.inflate(inflater, container, false)

        template = requireArguments().get("template") as Template
        populateTemplateDetails(template)
        modalBottomSheet = TemplateDetailsModalBottomSheet(onBottomSheetClickHandler)

        setupUI()
        parentFragmentManager.setFragmentResultListener(
            TemplateDetailsFragment::class.simpleName.toString(),
            viewLifecycleOwner
        ) { _, result ->
            populateTemplateDetails(result.get("template") as Template)
        }
        return binding.root
    }

    private fun setupUI() {
        setupToolbar()

        loadingDialog = DialogFactory.createLoadingDialog(requireContext())

        detailsViewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                loadingDialog.show()
            } else {
                loadingDialog.dismiss()
            }
        }
        detailsViewModel.deleteResult.observe(viewLifecycleOwner) { deletionSuccess ->
            loadingDialog.dismiss()
            val messageResId = if (deletionSuccess) {
                R.string.template_deletion_success
            } else {
                R.string.template_deletion_error
            }
            val resultDialog = DialogFactory.createInformativeDialog(
                requireContext(),
                null,
                messageResId,
                R.string.common_button_dismiss
            ) {
                if (deletionSuccess) {
                    findNavController().popBackStack()
                }
            }
            resultDialog.show()
        }
    }

    private val onBottomSheetClickHandler: (View) -> Unit = {
        modalBottomSheet.dismiss()
        when (it.id) {
            R.id.template_details_edit -> navigateToEditTemplateScreen()
            R.id.template_details_delete -> showConfirmDeleteDialog()
        }
    }

    private fun navigateToEditTemplateScreen() {
        findNavController().navigate(
            TemplateDetailsFragmentDirections.actionNavFragmentTemplateDetailsToNavFragmentTemplateEdit(
                template
            )
        )
    }

    private fun showConfirmDeleteDialog() {
        DialogFactory.createConfirmDialog(
            requireContext(), R.string.template_details_are_you_sure_you_want_to_delete,
            doOnConfirmation = onDeleteTemplate
        ).show()
    }

    private val onDeleteTemplate = {
        detailsViewModel.deleteTemplate(template.payeeId)
    }


    private fun setupToolbar() {

        binding.toolbar.menu[0].setOnMenuItemClickListener {
            modalBottomSheet.show(childFragmentManager, TemplateDetailsModalBottomSheet.TAG)
            true
        }
        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun populateTemplateDetails(template: Template) {
        with(binding) {
            // Use template beneficiary as fragment title
            toolbarTitle.text = template.name

            // Set template details
            transferType.text = getTemplateTypeLabel(template.transferType)
            payeeEmail.text = template.email
            accountNumber.text = template.accountNumber
            templateCurrency.text = template.currency
        }
    }

    private fun getTemplateTypeLabel(transferType: String?): String {
        val stringId = resources.getIdentifier(
            "transfer_type_$transferType", "string", requireActivity().packageName
        )

        return try {
            getString(stringId)
        } catch ( ex: Resources.NotFoundException) {
            Log.e(TAG, "Resource for type $transferType not found!")
            "";
        }
    }
}
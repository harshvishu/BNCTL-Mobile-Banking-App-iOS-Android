package tl.bnctl.banking.ui.banking.fragments.transfers.templates.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tl.bnctl.banking.databinding.TemplateDetailsModalBottomSheetBinding

class TemplateDetailsModalBottomSheet(
    private val onClickHandler: (View) -> Unit
) :
    BottomSheetDialogFragment() {

    private var _binding: TemplateDetailsModalBottomSheetBinding? = null
    private val binding get() = _binding!!

    companion object {
        val TAG: String = TemplateDetailsModalBottomSheet::class.java.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TemplateDetailsModalBottomSheetBinding.inflate(inflater, container, false)

        initListeners()

        return binding.root
    }

    private fun initListeners() {
        with(binding) {
            templateDetailsEdit.setOnClickListener(onClickHandler)
            templateDetailsDelete.setOnClickListener(onClickHandler)
        }
    }
}
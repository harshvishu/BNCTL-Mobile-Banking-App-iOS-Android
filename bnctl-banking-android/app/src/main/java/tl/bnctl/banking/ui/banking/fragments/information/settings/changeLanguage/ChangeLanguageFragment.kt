package tl.bnctl.banking.ui.banking.fragments.information.settings.changeLanguage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import tl.bnctl.banking.BuildConfig
import tl.bnctl.banking.R
import tl.bnctl.banking.databinding.FragmentChangeLanguageBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.utils.DialogFactory
import tl.bnctl.banking.util.LocaleHelper
import java.util.*


class ChangeLanguageFragment : BaseFragment() {

    private var _binding: FragmentChangeLanguageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeLanguageBinding.inflate(inflater, container, false)

        binding.changeLanguageToolbar.setOnClickListener {
            findNavController().popBackStack()
        }

        val currentLanguage = LocaleHelper.getCurrentLanguage(requireContext())
        val linearLayout: LinearLayout = binding.root.findViewById(R.id.settings_options)

        BuildConfig.SUPPORTED_LANGUAGES.forEach { language ->
            val languageButtonChoice =
                inflater.inflate(R.layout.language_button, container, false) as Button
            languageButtonChoice.id = View.generateViewId()
            val locale = Locale(language)
            val languageWholeName = locale.getDisplayLanguage(locale)
            languageButtonChoice.text = languageWholeName.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    locale
                ) else it.toString()
            }
            languageButtonChoice.setCompoundDrawablesRelativeWithIntrinsicBounds(
                requireContext().resources.getIdentifier(
                    "ic_language_${language}",
                    "drawable",
                    requireContext().packageName
                ), 0, if (currentLanguage == language) R.drawable.ic_check else 0, 0
            )

            if (currentLanguage != language) {
                languageButtonChoice.setOnClickListener {
                    createDialog(language)
                }
            }

            linearLayout.addView(languageButtonChoice)
        }

        return binding.root
    }

    private fun createDialog(language: String) {
        DialogFactory.createConfirmDialog(
            requireContext(),
            messageResource = R.string.language_dialog_message,
            confirm = R.string.language_dialog_confirm,
            reject = R.string.language_dialog_reject,
            doOnConfirmation = {
                LocaleHelper.setLocale(requireContext(), language)
                requireActivity().recreate()
            }).show()
    }
}

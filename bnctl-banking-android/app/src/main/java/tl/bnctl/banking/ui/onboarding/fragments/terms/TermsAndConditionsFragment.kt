package tl.bnctl.banking.ui.onboarding.fragments.terms

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import tl.bnctl.banking.R
import tl.bnctl.banking.ui.BaseFragment

class TermsAndConditionsFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View =
            inflater.inflate(R.layout.fragment_terms_and_conditions, container, false)

        val nextButton = rootView.findViewById<Button>(R.id.fragment_t_c_btn_next)
        nextButton.setOnClickListener {
            findNavController().navigate(TermsAndConditionsFragmentDirections.actionNavFragmentTermsAndConditionsToLoginNavigation())
        }

        val termsAndConditionsTextView = rootView.findViewById<TextView>(R.id.fragment_t_c_text)
        termsAndConditionsTextView.movementMethod = ScrollingMovementMethod()

        return rootView
    }

    companion object {
        val TAG: String = TermsAndConditionsFragment::class.java.name
    }
}
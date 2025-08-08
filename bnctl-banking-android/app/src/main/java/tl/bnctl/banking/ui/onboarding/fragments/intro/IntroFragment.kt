package tl.bnctl.banking.ui.onboarding.fragments.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import tl.bnctl.banking.databinding.FragmentIntroBinding
import tl.bnctl.banking.ui.BaseFragment

class IntroFragment : BaseFragment() {

    private var _binding: FragmentIntroBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIntroBinding.inflate(inflater, container, false)
        binding.fragmentIntroBtnNext.setOnClickListener {
            findNavController().navigate(IntroFragmentDirections.actionNavFragmentIntroToNavFragmentTermsAndConditions())
        }
        return binding.root;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmField
        val TAG: String = IntroFragment::class.java.name
    }
}
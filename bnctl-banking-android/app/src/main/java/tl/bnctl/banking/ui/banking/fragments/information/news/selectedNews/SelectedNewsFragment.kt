package tl.bnctl.banking.ui.banking.fragments.information.news.selectedNews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import tl.bnctl.banking.databinding.FragmentNewsSelectedBinding
import tl.bnctl.banking.ui.BaseFragment

class SelectedNewsFragment : BaseFragment() {

    companion object {
        val TAG: String = SelectedNewsFragment::class.java.name
    }

    private var _binding: FragmentNewsSelectedBinding? = null
    private val binding get() = _binding!!

    private val navArgs: SelectedNewsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,

        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsSelectedBinding.inflate(inflater, container, false)

        val selectedNews = navArgs.selectedNews
        binding.newsPreviewText.text = selectedNews.text
        binding.newsPreviewTitle.text = selectedNews.title
        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }
}

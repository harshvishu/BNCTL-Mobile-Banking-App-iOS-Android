package tl.bnctl.banking.ui.banking.fragments.information.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.news.model.News
import tl.bnctl.banking.databinding.FragmentNewsBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.utils.DateUtils
import java.util.*
import java.util.Calendar.*

class NewsFragment : BaseFragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private val newsViewModel: NewsViewModel by activityViewModels { NewsViewModelFactory() }

    private var isLoadingNews = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        newsViewModel.fetchNews()
        isLoadingNews = true
        setupLoadingIndicator()
        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
            newsViewModel.clearData()
        }
        newsViewModel.news.observe(viewLifecycleOwner) {
            isLoadingNews = false
            setupLoadingIndicator()
            if (it is Result.Success) {
                var listNews: List<News> = it.data
                listNews = listNews.sortedWith { item1, item2 -> item2.date.compareTo(item1.date) }
                val currentDate: Date = getInstance().time
                var dateIteration: Date = getInstance().time
                val calendarYesterday = getInstance()
                calendarYesterday.add(DAY_OF_MONTH, -1)
                val yesterdayDate: Date = calendarYesterday.time
                var cardContainerForGivenDate =
                    inflater.inflate(R.layout.item_container_news, container, false)

                val lastItem = listNews.last()
                val firstItem = listNews.first()

                val todayIsoString = DateUtils.formatDateISO(currentDate)
                val yesterdayIsoString = DateUtils.formatDateISO(yesterdayDate)
                listNews.forEach { item ->
                    run {
                        var hasDateChanged = false
                        if (compareTwoDates(item.date, dateIteration) != 0) {
                            dateIteration = item.date
                            hasDateChanged = true
                        }
                        if (hasDateChanged) {
                            // add previous ready card with news
                            if (item != firstItem) {
                                binding.linearLayoutContainer.addView(cardContainerForGivenDate)
                            }
                            // create new card for news
                            cardContainerForGivenDate =
                                inflater.inflate(R.layout.item_container_news, container, false)
                            setCardHolderDate(
                                cardContainerForGivenDate, todayIsoString,
                                yesterdayIsoString, item
                            )
                        }
                        // new card
                        val viewCard: View =
                            inflater.inflate(R.layout.item_card_news, container, false)
                        // viewCard.setNa
                        viewCard.setOnClickListener { _ ->
                            findNavController()
                                .navigate(
                                    NewsFragmentDirections.actionNavFragmentNewsToNavFragmentSelectedNews(
                                        item
                                    )
                                )
                        }
                        val title = viewCard.findViewById<TextView>(R.id.title_item_news)
                        title.text = item.title
                        val text = viewCard.findViewById<TextView>(R.id.text_item_news)
                        text.text = item.text

                        val newsContainer =
                            cardContainerForGivenDate.findViewById<LinearLayout>(R.id.content_card_container)
                        newsContainer.addView(viewCard)
                        if (item == firstItem) {
                            setCardHolderDate(
                                cardContainerForGivenDate,
                                todayIsoString,
                                yesterdayIsoString,
                                item
                            )
                        }
                        if (item == lastItem) {
                            binding.linearLayoutContainer.addView(cardContainerForGivenDate)
                        }
                    }
                }
            } else if (it is Result.Error) {
                handleResultError(it, R.string.error_loading_news)
            }
        }

        return binding.root
    }

    companion object {
        val TAG: String = NewsFragment::class.java.name
    }

    private fun setCardHolderDate(
        cardContainerForGivenDate: View,
        todayIsoString: String,
        yesterdayIsoString: String,
        item: News
    ) {
        val headerDateView =
            cardContainerForGivenDate.findViewById<TextView>(R.id.item_header_title)
        when (DateUtils.formatDateISO(item.date)) {
            todayIsoString -> headerDateView.text =
                resources.getString(R.string.news_label_today)
            yesterdayIsoString -> headerDateView.text =
                resources.getString(R.string.news_label_yesterday)
            else -> { // todo check if the date should be with the user preferred format
                val cal = getInstance()
                cal.time = item.date
                item.date
                headerDateView.text = "${cal.get(DAY_OF_MONTH)} ${
                    resources.getString(
                        resources.getIdentifier(
                            "news_label_month_${
                                cal.get(MONTH) + 1
                            }", "string", requireActivity().packageName
                        )
                    )
                } ${cal.get(YEAR)}"
            }
        }
    }

    private fun setupLoadingIndicator() {
        if (!isLoadingNews) {
            binding.loadingIndicator.visibility = View.GONE
        } else {
            binding.loadingIndicator.visibility = View.VISIBLE
        }
    }

    private fun compareTwoDates(d1: Date, d2: Date): Int {
        val calDate1 = getInstance()
        calDate1.time = d1
        val calDate2 = getInstance()
        calDate2.time = d2
        if (calDate1.get(YEAR) != calDate2.get(YEAR))
            return calDate1.get(YEAR) - calDate2.get(YEAR)
        if (calDate1.get(MONTH) != calDate2.get(MONTH))
            return calDate1.get(MONTH) - calDate2.get(MONTH)
        return calDate1.get(DAY_OF_MONTH) - calDate2.get(DAY_OF_MONTH)
    }
}
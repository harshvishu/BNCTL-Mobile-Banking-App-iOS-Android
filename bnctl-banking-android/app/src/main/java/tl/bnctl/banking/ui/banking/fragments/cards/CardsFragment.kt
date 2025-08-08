package tl.bnctl.banking.ui.banking.fragments.cards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.cards.model.Card
import tl.bnctl.banking.databinding.FragmentCardsBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.fragments.cards.credit_card_statement.CreditCardStatementFragment
import tl.bnctl.banking.ui.banking.fragments.cards.transaction_history.CardTransactionHistoryFragment
import tl.bnctl.banking.ui.utils.NumberUtils
import tl.bnctl.banking.util.ResourceProvider
import java.math.BigDecimal

class CardsFragment : BaseFragment() {

    private lateinit var currentCard: Card
    private lateinit var cardsViewModel: CardsViewModel
    private lateinit var cardsViewAdapter: CardsViewAdapter

    private var _binding: FragmentCardsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        cardsViewModel =
            ViewModelProvider(this, CardsViewModelFactory())[CardsViewModel::class.java]

        _binding = FragmentCardsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initUI()
        fetchCards()
        observeCards()
        setupViewPager()

        return root
    }

    private fun setupViewPager() {
        binding.contentViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {}

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager.SCROLL_STATE_SETTLING || state == ViewPager.SCROLL_STATE_DRAGGING) {
                    updateUI()
                }
            }
        })
    }

    private fun observeCards() {
        cardsViewModel.cards.observe(viewLifecycleOwner, Observer {
            binding.loadingIndicator.visibility = View.GONE
            if (it == null || it is Result.Error) {
                val error = it as Result.Error
                handleResultError(error, R.string.error_loading_cards)
                return@Observer
            }
            if (cardsViewModel.getCards().count() > 0) {
                currentCard = cardsViewModel.getCards()[0]
                binding.cardsScrollView.visibility = View.VISIBLE
                updateUI()
            }
            cardsViewAdapter.notifyDataSetChanged()
        })
    }

    /**
     * Initiate the adapter, do some binding-related stuff, set initial state to all UI components
     */
    private fun initUI() {
        cardsViewAdapter = CardsViewAdapter(cardsViewModel.cards)

        with(binding) {
            viewPagerTabLayout.setupWithViewPager(contentViewPager)
            contentViewPager.adapter = cardsViewAdapter
            contentViewPager.clipToPadding = false
            contentViewPager.pageMargin =
                (ResourceProvider.getPixelValue(requireContext(), 64f))
            val padding =
                (ResourceProvider.getPixelValue(requireContext(), 96f))
            contentViewPager.setPadding(padding, 0, padding, 0)

            btnCardDetails.setOnClickListener {
                startCardDetailsActivity()
            }
            btnCardActivity.setOnClickListener {
                startCardActivityActivity()
            }
            btnCreditCardStatements.setOnClickListener {
                startCreditCardStatementsActivity()
            }
            binding.toolbar.menu[0].setOnMenuItemClickListener {
                findNavController()
                    .navigate(
                        R.id.action_nav_fragment_cards_to_nav_fragment_new_debit_card
                    )
                true
            }
        }
    }

    private fun fetchCards() {
        binding.loadingIndicator.visibility = View.VISIBLE
        binding.cardsScrollView.visibility = View.GONE
        cardsViewModel.fetchCards()
    }

    /**
     * Update all card-related UI components
     */
    private fun updateUI() {
        if (cardsViewAdapter.count > 0) {
            val currentCard = getCurrentCard()
            val balance: BigDecimal = BigDecimal.valueOf(currentCard.availableBalance)

            binding.noCardsWrapper.visibility = View.GONE
            binding.cardsScrollView.visibility = View.VISIBLE

            // Show page indicator only if there's more than one card available
            binding.viewPagerTabLayout.visibility =
                if (cardsViewAdapter.count > 1) View.VISIBLE else View.GONE

            // Available balance
            binding.tvAvailableBalanceMain.text =
                NumberUtils.bigDecimalToStringNoDecimal(balance)
            binding.tvAvailableBalanceSecondary.text =
                NumberUtils.bigDecimalRemainderToStringWithTrailingZero(balance)
            binding.tvAvailableBalanceCurrency.text = currentCard.currency

            // Blocked amount
            binding.tvBlockedBalance.text =
                NumberUtils.formatAmount(currentCard.blockedAmount)
            binding.tvBlockedBalanceCurrency.text = currentCard.currency

            // Card type
            if (currentCard.cardType.equals("debit")) {
                binding.toolbarTitle.text = getText(R.string.cards_debit_card)
                binding.btnCreditCardStatements.visibility = View.GONE
                binding.tvBlockedBalanceLabel.text = getString(R.string.cards_blocked_amount)
            } else if (currentCard.cardType.equals("credit")) {
                binding.toolbarTitle.text = getText(R.string.cards_credit_card)
                binding.btnCreditCardStatements.visibility = View.VISIBLE
                binding.tvBlockedBalanceLabel.text = getString(R.string.cards_credit_blocked_amount)
                binding.tvBlockedBalance.text =
                    NumberUtils.formatAmount(currentCard.approvedOverdraft)
            }
        } else {
            binding.cardsScrollView.visibility = View.GONE
            binding.noCardsWrapper.visibility = View.VISIBLE
        }
    }

    private fun getCurrentCard(): Card {
        val cards = cardsViewModel.getCards()
        if (cards.count() > 0) {
            if (binding.contentViewPager.currentItem < cards.size) {
                if (currentCard !== cards[binding.contentViewPager.currentItem]) {
                    currentCard = cards[binding.contentViewPager.currentItem]
                }
            }
        }
        return currentCard
    }

    private fun startCardDetailsActivity() {
        requireView().findNavController()
            .navigate(
                R.id.action_nav_fragment_cards_to_nav_fragment_card_details,
                bundleOf(CardDetailsFragment.CARD_ARGUMENT to currentCard)
            )
    }

    private fun startCardActivityActivity() {
        requireView().findNavController()
            .navigate(
                R.id.action_nav_fragment_cards_to_nav_fragment_card_transaction_history,
                bundleOf(CardTransactionHistoryFragment.CARD_ARGUMENT to currentCard)
            )
    }

    private fun startCreditCardStatementsActivity() {
        requireView().findNavController()
            .navigate(
                R.id.action_nav_fragment_cards_to_nav_fragment_credit_card_statement,
                bundleOf(CreditCardStatementFragment.CARD_ARGUMENT to currentCard)
            )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
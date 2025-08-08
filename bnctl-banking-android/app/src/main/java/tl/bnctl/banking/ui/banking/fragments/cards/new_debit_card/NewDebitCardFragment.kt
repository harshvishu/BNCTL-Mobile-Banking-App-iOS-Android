package tl.bnctl.banking.ui.banking.fragments.cards.new_debit_card

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.RadioButton
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.lifecycle.MediatorLiveData
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.accounts.model.Account
import tl.bnctl.banking.data.branches.model.Branch
import tl.bnctl.banking.data.cards.model.CardProduct
import tl.bnctl.banking.data.cards.model.NewDebitCardResult
import tl.bnctl.banking.databinding.FragmentNewDebitCardBinding
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.banking.fragments.cards.new_debit_card.adapter.BranchesAdapter
import tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.account.source.SelectAccountViewModelFactory
import tl.bnctl.banking.ui.banking.fragments.transfers.create.sections.account.source.SelectSourceAccountViewModel
import tl.bnctl.banking.ui.utils.DialogFactory
import tl.bnctl.banking.ui.utils.NumberUtils
import tl.bnctl.banking.util.Constants


class NewDebitCardFragment : BaseFragment() {

    private var _binding: FragmentNewDebitCardBinding? = null
    private val binding get() = _binding!!

    private val selectSourceAccountViewModel: SelectSourceAccountViewModel by viewModels { SelectAccountViewModelFactory() }
    private val newDebitCardViewModel: NewDebitCardViewModel by viewModels { NewDebitCardViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNewDebitCardBinding.inflate(layoutInflater, container, false)
        selectSourceAccountViewModel.accountFetch(this.javaClass.toString())
        newDebitCardViewModel.fetchCardProducts()
        newDebitCardViewModel.fetchBranches()
        setupUI()

        return binding.root;
    }

    private fun setupUI() {
        setupLoadingIndicator()
        setupAccountField()
        setupCardProductField()
        setupBranches()
        setupEmbossNameField()
        setupPickupLocationField()
        setupStatementsFields()
        setupAgreementCheckbox()

        binding.toolbar.menu[0].setOnMenuItemClickListener {
            findNavController().popBackStack()
            true
        }

        binding.newDebitCardConfirmButton.isEnabled = true

        newDebitCardViewModel.result.observe(viewLifecycleOwner) {
            if (it == null || it is Result.Error) {
                val error = it as Result.Error
                handleResultError(error, R.string.new_debit_card_error_unknown)
            } else {
                val result = (it as Result.Success<NewDebitCardResult>).data
                if (result.success) {
                    requireView().findNavController()
                        .navigate(
                            NewDebitCardFragmentDirections.navFragmentNewDebitCardToNavFragmentNewDebitCardSuccess()
                        )
                } else {
                    DialogFactory.createCancellableDialog(
                        requireActivity(),
                        R.string.new_debit_card_error_unknown
                    ).show()
                }
            }
        }
    }

    /**
     * Use mediatorLiveData to synchronize all the loading parts of the screen.
     * When the accounts, branches and card products are done loading, the screen will be shown.
     */
    private fun setupLoadingIndicator() {
        val doneLoadingMediatorData = MediatorLiveData<Boolean>()
        doneLoadingMediatorData.value = false
        doneLoadingMediatorData.addSource(newDebitCardViewModel.loadingCardProducts) {
            doneLoadingMediatorData.postValue(
                !newDebitCardViewModel.loadingBranches.value!!
                        && !newDebitCardViewModel.loadingAccounts.value!!
                        && !it
            )
        }
        doneLoadingMediatorData.addSource(newDebitCardViewModel.loadingBranches) {
            doneLoadingMediatorData.postValue(
                !newDebitCardViewModel.loadingCardProducts.value!!
                        && !newDebitCardViewModel.loadingAccounts.value!!
                        && !it
            )
        }
        doneLoadingMediatorData.addSource(newDebitCardViewModel.loadingAccounts) {
            doneLoadingMediatorData.postValue(
                !newDebitCardViewModel.loadingCardProducts.value!!
                        && !newDebitCardViewModel.loadingBranches.value!!
                        && !it
            )
        }

        // Hide loader and show rest of screen when everything is done
        doneLoadingMediatorData.observe(viewLifecycleOwner) { doneLoading ->
            if (doneLoading) {
                binding.loadingIndicator.visibility = View.GONE
                binding.layoutWrapper.visibility = View.VISIBLE
            } else {
                binding.loadingIndicator.visibility = View.VISIBLE
                binding.layoutWrapper.visibility = View.INVISIBLE
            }
        }
    }

    private fun setupAgreementCheckbox() {
        binding.newDebitCardAgreeCheckbox.setOnCheckedChangeListener { button, checked ->
            newDebitCardViewModel.setAgreed(checked)
        }
        newDebitCardViewModel.agreementError.observe(viewLifecycleOwner) {
            val focusable: Boolean
            if (it != null) {
                focusable = true
                binding.newDebitCardAgreeCheckbox.error = resources.getString(it)
            } else {
                focusable = false
                binding.newDebitCardAgreeCheckbox.error = null
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                binding.newDebitCardAgreeCheckbox.focusable =
                    if (focusable) View.FOCUSABLE else View.NOT_FOCUSABLE
            }
            binding.newDebitCardAgreeCheckbox.isFocusableInTouchMode = focusable
        }
    }

    private fun setupBranches() {
        newDebitCardViewModel.branches.observe(viewLifecycleOwner) {
            if (it == null || it is Result.Error) {
                val error = it as Result.Error
                if (error.code.equals(Constants.SESSION_EXPIRED_CODE)) {
                    DialogFactory.createSessionExpiredDialog(requireContext()).show()
                } else {
                    DialogFactory.createCancellableDialog(
                        requireActivity(),
                        R.string.error_loading_card_products
                    ).show()
                }
            } else {
                val branches: List<Branch> = (it as Result.Success<List<Branch>>).data

                val branchesAdapter = BranchesAdapter(
                    requireContext(),
                    R.layout.item_simple_id_name_spinner_item,
                    branches,
                    Branch("0", getString(R.string.new_debit_card_placeholder_pickup_location))
                )
                binding.newDebitCardPickupLocation.adapter = branchesAdapter
            }

            binding.newDebitCardConfirmButton.setOnClickListener {
                onConfirmPressed()
            }
        }
    }

    private fun setupAccountField() {
        newDebitCardViewModel.setAccountLoading(true)
        selectSourceAccountViewModel.selectedAccount.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.sourceAccountBalance.text =
                    NumberUtils.formatAmount(it?.balance?.available)
                binding.sourceAccountName.text = it?.accountName
                binding.sourceAccountNumber.text = it?.accountNumber
            }
        }

        selectSourceAccountViewModel.accountsWithPermission.observe(viewLifecycleOwner) {
            newDebitCardViewModel.setAccountLoading(false)
            if (it == null || it is Result.Error) {
                val error = it as Result.Error
                if (error.code.equals(Constants.SESSION_EXPIRED_CODE)) {
                    DialogFactory.createSessionExpiredDialog(requireContext()).show()
                } else {
                    DialogFactory.createCancellableDialog(
                        requireActivity(),
                        R.string.error_loading_accounts
                    ).show()
                }
            } else {
                val accounts =
                    (it as Result.Success<List<Account>>).data.filter { account ->
                        account.product.type == "operational" && selectSourceAccountViewModel.filterRequirement(
                            account
                        )
                    }
                val selectedAccount = selectSourceAccountViewModel.selectedAccount.value
                if (selectedAccount == null && accounts.isNotEmpty()) {
                    val preselectAccountNumber = selectSourceAccountViewModel.preselectAccount.value
                    var newlySelectedAccount = accounts[0]
                    if (!preselectAccountNumber.isNullOrBlank()) {
                        val preselectedAccount =
                            accounts.find { account -> account.accountNumber == preselectAccountNumber }
                        preselectedAccount?.let { acc -> newlySelectedAccount = acc }
                        selectSourceAccountViewModel.clearPreselectedAccount()
                    }
                    selectSourceAccountViewModel.selectAccount(newlySelectedAccount)
                    newDebitCardViewModel.setAccount(newlySelectedAccount.accountNumber)
                }
            }
        }

        binding.buttonEditSourceAccount.setOnClickListener {
            val accountsResult = selectSourceAccountViewModel.accountsWithPermission.value
            if (accountsResult != null && accountsResult is Result.Success) {
                val accounts =
                    accountsResult.data.filter { account ->
                        account.product.type == "operational" && selectSourceAccountViewModel.filterRequirement(
                            account
                        )
                    }
                requireView().findNavController()
                    .navigate(
                        NewDebitCardFragmentDirections.actionGlobalNavFragmentSelectAccountList(
                            accounts.toTypedArray(),
                            NewDebitCardFragment::class.simpleName.toString(),
                            R.string.new_debit_card_linked_to_title
                        )
                    )
            }
        }

        // Use fragment result API to listen for the result form SelectAccountListFragment
        // Note: the listener needs to be set on the same fragment manager as the SelectAccountListFragment's
        parentFragmentManager.setFragmentResultListener(
            NewDebitCardFragment::class.simpleName.toString(),
            viewLifecycleOwner
        ) { _, result ->
            result.getParcelable<Account>("selectedAccount")?.let {
                selectSourceAccountViewModel.selectAccount(it)
                newDebitCardViewModel.setAccount(it.accountNumber)
            }
        }

        newDebitCardViewModel.accountError.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.newDebitCardSourceAccountLabel.error = resources.getString(it)
            } else {
                binding.newDebitCardSourceAccountLabel.error = null
            }
        }
    }

    private fun setupStatementsFields() {
        binding.receiveStatementAtBranch.setOnCheckedChangeListener { radioButton, checked ->
            binding.newDebitCardStatementsEmail.visibility = View.GONE
            newDebitCardViewModel.setStatementsOnDemand(checked)
        }

        binding.receiveStatementByEmail.setOnCheckedChangeListener { radioButton, checked ->
            binding.newDebitCardStatementsEmail.visibility = View.VISIBLE
            newDebitCardViewModel.setStatementsOnEmail(checked)
        }

        binding.newDebitCardStatementsEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                newDebitCardViewModel.setStatementsEmail(text.toString())
            }
        })
        newDebitCardViewModel.emailError.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.newDebitCardStatementsEmail.error = resources.getString(it)
            } else {
                binding.newDebitCardStatementsEmail.error = null
            }
        }
    }

    private fun setupCardProductField() {
        newDebitCardViewModel.cardProducts.observe(viewLifecycleOwner) {
            if (it == null || it is Result.Error) {
                val error = it as Result.Error
                if (error.code.equals(Constants.SESSION_EXPIRED_CODE)) {
                    DialogFactory.createSessionExpiredDialog(requireContext()).show()
                } else {
                    DialogFactory.createCancellableDialog(
                        requireActivity(),
                        R.string.error_loading_card_products
                    ).show()
                }
            } else {
                val products = (it as Result.Success<List<CardProduct>>).data
                binding.newDebitCardProductTypes.removeAllViews()
                for (product in products) {
                    val radioButton = RadioButton(requireContext())
                    radioButton.text = product.name
                    radioButton.setOnClickListener {
                        newDebitCardViewModel.setCardProductType(product)
                    }
                    if (newDebitCardViewModel.request.value!!.cardProductCode.equals(product.id)
                    ) {
                        radioButton.isChecked = true
                    }

                    binding.newDebitCardProductTypes.addView(radioButton)
                }
            }
        }

        newDebitCardViewModel.cardTypeError.observe(viewLifecycleOwner) {
            val radioButton = binding.newDebitCardProductTypes.getChildAt(0) as RadioButton
            val focusable: Boolean
            if (it != null) {
                radioButton.error = resources.getString(it)
                focusable = true
            } else {
                radioButton.error = null
                focusable = false
            }
            // Setting focusable (or not) here, otherwise you have to click the button twice to engage it
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                radioButton.focusable = if (focusable) View.FOCUSABLE else View.NOT_FOCUSABLE
            }
            radioButton.isFocusableInTouchMode = focusable
        }
    }

    private fun setupPickupLocationField() {
        binding.newDebitCardPickupLocation.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    if (binding.newDebitCardPickupLocation.selectedItemPosition > 0) {
                        newDebitCardViewModel.setPickupLocation(binding.newDebitCardPickupLocation.selectedItem as Branch)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }

        newDebitCardViewModel.locationError.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.newDebitCardPickupLocationWrapper.error = resources.getString(it)
            } else {
                binding.newDebitCardPickupLocationWrapper.error = null
            }
        }
    }

    private fun setupEmbossNameField() {
        val nameValidationRegex = Regex("[A-Za-z\\.\\s]*")
        val nameFilter =
            InputFilter { source, _, _, _, _, _ ->
                if (source != null && !source.matches(nameValidationRegex)) {
                    source.replace(Regex("[^A-Za-z\\.\\s]+"), "").uppercase()
                } else source.toString().uppercase()
            }

        val filters = arrayListOf(nameFilter)

        filters.addAll(binding.newDebitCardEmbossName.filters)
        binding.newDebitCardEmbossName.filters = filters.toTypedArray()

        binding.newDebitCardEmbossName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                newDebitCardViewModel.setEmbossName(text.toString())
            }
        })
        newDebitCardViewModel.embossNameError.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.newDebitCardEmbossName.error = resources.getString(it)
            } else {
                binding.newDebitCardEmbossName.error = null
            }
        }
    }

    private fun onConfirmPressed() {
        binding.newDebitCardConfirmButton.isEnabled = false
        val formValid = formIsValid()
        if (formValid) {
            newDebitCardViewModel.sendCardRequest()
        } else {
            binding.newDebitCardConfirmButton.isEnabled = true
        }
    }

    private fun formIsValid(): Boolean {
        with(newDebitCardViewModel) {
            validateAccount()
            validateCardType()
            validateEmbossName()
            validatePickupLocation()
            validateAgreed()
            validateEmail()

            return embossNameError.value == null
                    && cardTypeError.value == null
                    && embossNameError.value == null
                    && locationError.value == null
                    && agreementError.value == null
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

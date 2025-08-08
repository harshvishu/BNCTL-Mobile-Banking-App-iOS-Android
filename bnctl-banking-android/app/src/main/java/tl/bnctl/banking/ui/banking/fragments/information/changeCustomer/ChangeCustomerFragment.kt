package tl.bnctl.banking.ui.banking.fragments.information.changeCustomer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import tl.bnctl.banking.R
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.customer.model.Customer
import tl.bnctl.banking.databinding.FragmentChangeCustomerBinding
import tl.bnctl.banking.services.AuthenticationService
import tl.bnctl.banking.services.PermissionService
import tl.bnctl.banking.ui.BaseFragment
import tl.bnctl.banking.ui.onboarding.fragments.login.LoginViewModel
import tl.bnctl.banking.ui.onboarding.fragments.login.LoginViewModelFactory
import tl.bnctl.banking.ui.utils.DialogFactory

class ChangeCustomerFragment : BaseFragment() {

    private var _binding: FragmentChangeCustomerBinding? = null
    private val binding get() = _binding!!

    private lateinit var customerViewModel: ChangeCustomerViewModel
    private lateinit var loginViewModel: LoginViewModel
    private var isLoadingCustomers = false
    private var isCurrentUserChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customerViewModel = ViewModelProvider(
            this,
            ChangeCustomerViewModelFactory()
        )[ChangeCustomerViewModel::class.java]
        loginViewModel = ViewModelProvider(
            this,
            LoginViewModelFactory()
        )[LoginViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeCustomerBinding.inflate(inflater, container, false)

        binding.changeCustomerToolbar.setOnClickListener {
            findNavController().popBackStack()
        }

        val linearLayout: LinearLayout = binding.root.findViewById(R.id.customer_options)

        customerViewModel.fetchCustomers()
        isLoadingCustomers = true
        setupLoadingIndicator()

        customerViewModel.customers.observe(viewLifecycleOwner) { customersResult ->
            isLoadingCustomers = false
            setupLoadingIndicator()
            if (customersResult == null || customersResult is Result.Error) {
                handleResultError(customersResult as Result.Error, R.string.error_loading_customers)
            } else if (customersResult is Result.Success) {
                val currentCustomerId =
                    AuthenticationService.getInstance().getAuthToken()!!.split(" ")[1].split(".")[1]
                val customers = customersResult.data
                customers.forEach { customer ->
                    val button: LinearLayout = createButton(inflater, container, customer, currentCustomerId)
                    linearLayout.addView(button)
                }
            }
        }

        loginViewModel.currentUserResult.observe(viewLifecycleOwner) { loggedInUser ->
            if (loggedInUser != null && isCurrentUserChanged) {
                isCurrentUserChanged = false
                PermissionService.getInstance()
                    .updatePermissions(loggedInUser.authData.permissions)
                requireActivity().recreate()
            }
        }

        return binding.root
    }

    private fun createButton(
        inflater: LayoutInflater,
        container: ViewGroup?,
        customer: Customer,
        currentCustomerId: String
    ): LinearLayout {
        val customerButtonChoice =
            inflater.inflate(R.layout.list_item_change_customer, container, false) as LinearLayout
        View.generateViewId().also { customerButtonChoice.id = it }

        customerButtonChoice.children.forEach { childView ->
            if (childView.id == R.id.customer_initials) {
                childView as TextView
                val customerNames = customer.accountOwnerFullName.split(" ")
                val customerInitials = customerNames[0].first().uppercase() +
                        customerNames.last().first().uppercase()
                childView.text = customerInitials
            }
            if (childView.id == R.id.customer_full_name) {
                childView as TextView
                childView.text = customer.accountOwnerFullName
            }
            if (childView.id == R.id.customer_selected) {
                childView as ImageView
                if (customer.customerNumber != currentCustomerId) {
                    childView.visibility = View.GONE
                }
            }
        }

        if (currentCustomerId != customer.customerNumber) {
            customerButtonChoice.setOnClickListener {
                createDialog(customer.customerNumber)
            }
        }
        return customerButtonChoice
    }

    private fun createDialog(customerNumber: String) {
        DialogFactory.createConfirmDialog(
            requireContext(),
            messageResource = R.string.change_customer_dialog_message,
            confirm = R.string.change_customer_dialog_confirm,
            reject = R.string.change_customer_dialog_reject,
            doOnConfirmation = {
                isCurrentUserChanged = true
                AuthenticationService.getInstance()
                    .editCustomerNumberInAuthenticationData(customerNumber)
                loginViewModel.checkCurrentUser()
            }).show()
    }

    private fun setupLoadingIndicator() {
        // TODO If possible find a better way to control the loading indicator
        if (!isLoadingCustomers) {
            binding.loadingIndicator.visibility = View.GONE
        } else {
            binding.loadingIndicator.visibility = View.VISIBLE
        }
    }
}

package tl.bnctl.banking.services

import android.content.Context
import android.content.SharedPreferences
import android.widget.Button
import com.google.gson.Gson
import tl.bnctl.banking.data.accounts.model.Account
import tl.bnctl.banking.data.login.model.Permission
import tl.bnctl.banking.data.templates.model.Template
import tl.bnctl.banking.data.transfers.enums.TransferType
import tl.bnctl.banking.ui.banking.fragments.cards.new_debit_card.NewDebitCardFragment
import tl.bnctl.banking.ui.banking.fragments.services.currencyExchange.CurrencyExchangeFragment
import tl.bnctl.banking.ui.banking.fragments.services.insurance.confirm.InsuranceConfirmFragment
import tl.bnctl.banking.ui.banking.fragments.services.utilityBills.summary.UtilityBillSummaryFragment
import tl.bnctl.banking.ui.banking.fragments.transfers.create.screens.betweenacc.TransferBetweenOwnAccountsFragment
import tl.bnctl.banking.ui.banking.fragments.transfers.create.screens.iban.IbanTransferFragment
import tl.bnctl.banking.ui.banking.fragments.transfers.create.screens.intrabank.InternalTransferFragment
import tl.bnctl.banking.ui.banking.fragments.transfers.pending.PendingTransfersFragment
import tl.bnctl.banking.ui.banking.fragments.transfers.templates.TemplatesFragment

class PermissionService private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(
            tl.bnctl.banking.BankingApplication.appCode,
            Context.MODE_PRIVATE
        )

    private val permissionToMenuItem = HashMap<Button, Class<*>>()

    companion object {
        val TAG: String = PermissionService::class.java.simpleName

        @Volatile
        private var INSTANCE: PermissionService? = null

        fun initPermissionService(context: Context): PermissionService =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: PermissionService(context).also { INSTANCE = it }
            }

        fun getInstance(): PermissionService {
            return INSTANCE!!
        }

        private const val TRANSFER_BETWEEN_ACCOUNTS = "transfer.create"
        private const val TRANSFER_TO_NATIONAL_IBAN = "transfer.create.national"
        private const val INSURANCE_PAYMENT = "insurance.payment"
        private const val INTERNAL_CURRENCY_TRANSFER = "transfer.create"
        private const val FOREIGN_CURRENCY_EXCHANGE = "foreign.currency.exchange"
        private const val UTILITY_BILLS = "billpayment.simple.create "
        private const val NEW_DEBIT_CARD = "new.debit.card"
        private const val PAYEE_MANAGEMENT = "payee.fetch"
        private const val PENDING_TRANSFER_MANAGEMENT =
            "disabled" // TODO: Enable this when pending transfers are being implemented

        val PERMISSIONS = mapOf(
            TransferBetweenOwnAccountsFragment::class.java.toString() to TRANSFER_BETWEEN_ACCOUNTS,
            IbanTransferFragment::class.java.toString() to TRANSFER_TO_NATIONAL_IBAN,
            InsuranceConfirmFragment::class.java.toString() to INSURANCE_PAYMENT,
            InternalTransferFragment::class.java.toString() to INTERNAL_CURRENCY_TRANSFER,
            CurrencyExchangeFragment::class.java.toString() to FOREIGN_CURRENCY_EXCHANGE,
            UtilityBillSummaryFragment::class.java.toString() to UTILITY_BILLS,
            NewDebitCardFragment::class.java.toString() to NEW_DEBIT_CARD,
            TemplatesFragment::class.java.toString() to PAYEE_MANAGEMENT,
            PendingTransfersFragment::class.java.toString() to PENDING_TRANSFER_MANAGEMENT,
        )

        val REQUIRED_NUM_OF_ACCOUNTS_FOR_MENU_OPTION = mapOf(
            TRANSFER_BETWEEN_ACCOUNTS to 1,
            TRANSFER_TO_NATIONAL_IBAN to 1,
//            TRANSFER_TO_INTERNATIONAL_IBAN to 1,
            INSURANCE_PAYMENT to 1,
            INTERNAL_CURRENCY_TRANSFER to 1,
            FOREIGN_CURRENCY_EXCHANGE to 1,
            UTILITY_BILLS to 1,
            PAYEE_MANAGEMENT to 1
        )

        private val TRANSFER_TYPES_TO_FUNC = mapOf(
            TransferType.BETWEENACC.toString().lowercase() to TRANSFER_BETWEEN_ACCOUNTS,
            TransferType.INTRABANK.toString().lowercase() to INTERNAL_CURRENCY_TRANSFER,
            TransferType.INTERBANK.toString().lowercase() to TRANSFER_TO_NATIONAL_IBAN,
//            TransferType.INTERNATIONAL.toString().lowercase() to TRANSFER_TO_INTERNATIONAL_IBAN
        )

        private val REQUIRED_ACCOUNT_CODES_FOR_MENU_OPTION = mapOf(
            UTILITY_BILLS to listOf("1RACC", "1CACC")
        )
    }

//    fun hideMenusWithoutAccounts(containerLayout: ViewGroup) {
//        val listPermissions = getPermissions()
//        containerLayout.children.asIterable().forEach { button ->
//            if (permissionToMenuItem[button] != null) {
//                val permission: Permission? =
//                    listPermissions.find { permission -> permission.actionId == PERMISSIONS[permissionToMenuItem[button]] }
//                if (permission == null || permission.objectId.split(",").size < REQUIRED_NUM_OF_ACCOUNTS_FOR_MENU_OPTION[permission.actionId]!!) {
//                    button.visibility = View.GONE
//                }
//            }
//        }
//    }

    fun checkIfViewShouldBeDisplayed(viewKey: String): Boolean {
        val funcName = PERMISSIONS[viewKey] ?: return true
        val listPermissions = getPermissions()
        val permission: Permission =
            listPermissions.find { permission -> permission.actionId == funcName }
                ?: return false
        val numOfAccountsForPermission = permission.objectId.split(',').size
        val requiredNumOfAccounts: Int = REQUIRED_NUM_OF_ACCOUNTS_FOR_MENU_OPTION[funcName]
            ?: return false
        return numOfAccountsForPermission >= requiredNumOfAccounts
    }

    fun prepareMenuDataForPermissions(menuItems: Map<Button, Class<*>>) {
        permissionToMenuItem.clear()
        permissionToMenuItem.putAll(menuItems)
    }

    fun updatePermissions(newCustomerPermissions: List<Permission>) {
        with(sharedPreferences.edit()) {
            putString("permissions", Gson().toJson(newCustomerPermissions))
            apply()
        }
    }

    fun filterAccountsAccordingToPermissions(
        requiredPermissions: List<String>?,
        fetchedAccounts: List<Account>
    ): List<Account> {
        if (fetchedAccounts.isEmpty()) {
            return fetchedAccounts
        }
        val permissionsList = getPermissions()
        return if (requiredPermissions == null || requiredPermissions.isEmpty()) {
            fetchedAccounts
        } else {
            val filteredAccounts = mutableListOf<Account>()
            val potentialPermissions =
                permissionsList.filter { permission -> permission.actionId in requiredPermissions }
            val foundObjectIds = potentialPermissions.map { permission -> permission.objectId }

            fetchedAccounts.forEach { account ->
                for (objectId in foundObjectIds) {
                    if (account.accountId == objectId || objectId == "%") {
                        filteredAccounts.add(account)
                    }
                }
            }
            filteredAccounts
        }
    }

    /**
     * Checks if the customer has the required permissions for the accounts
     */
    fun accountHasPermission(requiredPermissions: List<String>, account: Account): Boolean {
        if (requiredPermissions.isEmpty()) {
            return true
        }

        val permissions = getPermissions()
        val potentialPermissions =
            permissions.filter { permission -> permission.actionId in requiredPermissions }
        val foundPermissions = mutableListOf<String>()
        for (potentialPermission in potentialPermissions) {
            if (account.accountId == potentialPermission.objectId || potentialPermission.objectId == "%") {
                foundPermissions.add(potentialPermission.actionId)
            }
            if (foundPermissions == requiredPermissions) {
                return true
            }
        }
        return false
    }

    fun filterTemplatesAccordingToPermissions(fetchedTemplates: List<Template>): List<Template> {
        val resultTemplates = ArrayList<Template>()
        val listPermissions: List<Permission> = getPermissions()
        fetchedTemplates.forEach { template ->
            val transferFuncName = TRANSFER_TYPES_TO_FUNC[template.transferType]!!
            if (checkIfThereAreAvailableAccountsWithNeededPermissions(
                    transferFuncName,
                    listPermissions
                )
            ) {
                resultTemplates.add(template)
            }
        }
        return resultTemplates
    }

    private fun checkIfThereAreAvailableAccountsWithNeededPermissions(
        transferFuncName: String,
        listPermissions: List<Permission>
    ): Boolean {
        val neededPermission: Permission =
            listPermissions.find { permission -> permission.actionId == transferFuncName }
                ?: return false
        val requiredNumberOfAccounts: Int =
            REQUIRED_NUM_OF_ACCOUNTS_FOR_MENU_OPTION[transferFuncName]
                ?: return false
        return neededPermission.objectId.split(",").size >= requiredNumberOfAccounts
    }


    private fun getPermissions(): List<Permission> {
        val permissions: String? = sharedPreferences.getString("permissions", "")
        return Gson().fromJson(permissions, Array<Permission>::class.java).toCollection(ArrayList())
    }
}

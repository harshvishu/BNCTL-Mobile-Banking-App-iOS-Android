package tl.bnctl.banking.data.accounts

enum class AccountPermissions(var permission: String) {
    INITIATE("ledger.accountPermission.transactionInitiate"),
    APPROVE("ledger.accountPermission.transactionApprove");

    override fun toString(): String {
        return permission
    }
}
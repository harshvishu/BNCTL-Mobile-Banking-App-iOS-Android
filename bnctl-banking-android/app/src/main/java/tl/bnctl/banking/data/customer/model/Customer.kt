package tl.bnctl.banking.data.customer.model

data class Customer(
    val accountOwnerFullName: String,
    val customerNumber: String,
    val currentlySelected: Boolean = false,
    val userId: String = ""
)
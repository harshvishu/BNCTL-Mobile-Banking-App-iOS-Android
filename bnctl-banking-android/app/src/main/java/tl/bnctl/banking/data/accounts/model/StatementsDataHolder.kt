package tl.bnctl.banking.data.accounts.model

import java.io.Serializable

/**
 * Implement this interface in the data class you want to display in the statements fragment
 * override the parameters with the same names and implement getter that return the value of the parameter you want ot show
 */
interface StatementsDataHolder : Serializable {
    var amount: String
    var currency: String
    var beneficiary: String?
    var dateOfExecution: String?
    var status: String
    var reason: String
    var destinationAccount: String?
    var sourceAccount: String
    var additionalInfo: String
    var transactionType: String
    var transferDirection: String
    val transferId: String
    val transferIdIssuer: String?
}
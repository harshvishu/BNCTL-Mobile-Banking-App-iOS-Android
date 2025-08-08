package tl.bnctl.banking.data.transfers.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import tl.bnctl.banking.data.accounts.model.Account
import tl.bnctl.banking.data.payee.model.Payee
import tl.bnctl.banking.data.templates.model.TemplateRequest
import tl.bnctl.banking.data.transfers.enums.TransferExecution
import tl.bnctl.banking.data.transfers.enums.TransferType

@Parcelize
data class TransferSummary(
    var amount: Double,
    var currency: String,
    var feeAmount: Double,
    var feeCurrency: String,
    var from: Account,
    var to: Payee,
    var reason: String,
    var transferType: TransferType,
    var executionType: TransferExecution,
    var additionalDetails: @RawValue Map<String, Any>,
    var additionalReason: String = "",
    var useFallback: Boolean = false,
    var fallbackSmsCode: String = "",
    var fallbackPin: String = "",
    var newPayee: TemplateRequest? = null
) : Parcelable

package tl.bnctl.banking.data.billpayments.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BillPaymentApproveNumerousRequest(
    var billPayments: List<String>,
    var sourceAccount: String,
    var sourceAccountId: String,
    var sourceAccountHolder: String
) : Parcelable

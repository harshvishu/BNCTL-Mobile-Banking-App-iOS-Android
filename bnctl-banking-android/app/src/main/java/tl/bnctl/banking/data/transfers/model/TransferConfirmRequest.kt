package tl.bnctl.banking.data.transfers.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransferConfirmRequest(
    var validationRequestId: String,
    var secret: String
) : Parcelable

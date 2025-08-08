package tl.bnctl.banking.data.transfers.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransferEnd(
    var transferSuccess: Boolean,
    var message: String
) : Parcelable

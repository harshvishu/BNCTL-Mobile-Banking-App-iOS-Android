package tl.bnctl.banking.data.transfers.model

import tl.bnctl.banking.data.Result

data class TransferConfirmResult(
    val success: TransferConfirm? = null,
    val error: Result.Error? = null
)

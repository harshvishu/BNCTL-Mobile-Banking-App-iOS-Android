package tl.bnctl.banking.data.transfers.model

import tl.bnctl.banking.data.Result

data class TransferCreateResult(
    val success: TransferCreate? = null,
    val error: Result.Error? = null
)

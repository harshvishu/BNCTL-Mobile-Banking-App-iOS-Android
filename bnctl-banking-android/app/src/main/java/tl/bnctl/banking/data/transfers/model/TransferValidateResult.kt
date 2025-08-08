package tl.bnctl.banking.data.transfers.model

import tl.bnctl.banking.data.Result

data class TransferValidateResult(
    val success: TransferValidate? = null,
    val error: Result.Error? = null
)

package tl.bnctl.banking.data.eod.model

import tl.bnctl.banking.data.Result

data class EoDResult (
    val eodAlreadyChecked: Boolean = false,
    val eodStarted: Boolean = false,
    val error: Result.Error? = null
        )
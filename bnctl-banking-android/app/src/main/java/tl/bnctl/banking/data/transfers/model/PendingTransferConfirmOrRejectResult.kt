package tl.bnctl.banking.data.transfers.model

import tl.bnctl.banking.data.fallback.FallbackResponseParams

data class PendingTransferConfirmOrRejectResult(
    var status: TransferStatus,
    var fallback: FallbackResponseParams?
)
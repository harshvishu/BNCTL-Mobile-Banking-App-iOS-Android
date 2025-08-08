package tl.bnctl.banking.data.transfers.pending

import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.fallback.FallbackRequestParams
import tl.bnctl.banking.data.transfers.model.PendingTransfer
import tl.bnctl.banking.data.transfers.model.PendingTransferConfirmOrRejectResult

class PendingTransferRepository(
    private val dataSource: PendingTransferDataSource
) {

    var pendingTransfers: List<PendingTransfer>? = null

    suspend fun fetchPendingTransfers(): Result<List<PendingTransfer>> {
        val result = dataSource.fetchPendingTransfers()
        if (result is Result.Success) {
            pendingTransfers = result.data
        }
        return result
    }

    suspend fun rejectSelectedPendingTransfers(
        transferIds: List<String>,
        fallbackParams: FallbackRequestParams?
    ): Result<PendingTransferConfirmOrRejectResult> {
        return dataSource.rejectPendingTransfers(transferIds, fallbackParams)
    }

    suspend fun confirmSelectedPendingTransfers(
        transferIds: List<String>,
        fallbackParams: FallbackRequestParams?
    ): Result<PendingTransferConfirmOrRejectResult> {
        return dataSource.confirmPendingTransfers(transferIds, fallbackParams)
    }
}
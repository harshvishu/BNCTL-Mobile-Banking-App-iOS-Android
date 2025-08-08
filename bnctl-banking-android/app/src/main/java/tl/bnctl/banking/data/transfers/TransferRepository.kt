package tl.bnctl.banking.data.transfers

import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.transfers.model.*
import java.util.*

class TransferRepository(private val dataSource: TransferDataSource) {

    suspend fun fetchTransfers(
        fromDate: Date,
        toDate: Date,
        pageNumber: Int,
        pageSize: Int
    ): Result<TransferHistoryResult> {
        return dataSource.fetchTransfers(fromDate, toDate, pageNumber, pageSize)
    }

    suspend fun confirmTransfer(transferConfirmation: TransferConfirmRequest): Result<TransferConfirm> {
        return dataSource.confirmTransfer(transferConfirmation)
    }

    suspend fun createTransfer(transferSummary: TransferSummary): Result<TransferCreate> {
        return dataSource.createTransfer(transferSummary)
    }

    suspend fun createAndExecuteTransfer(transferSummary: TransferSummary): Result<TransferConfirm> {
        return dataSource.createAndExecuteTransfer(transferSummary)
    }
}
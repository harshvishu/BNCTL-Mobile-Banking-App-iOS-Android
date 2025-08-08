package tl.bnctl.banking.data.transfers

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.HttpException
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.common.Pagination
import tl.bnctl.banking.data.transfers.enums.TransferDirection
import tl.bnctl.banking.data.transfers.model.*
import tl.bnctl.banking.services.AuthenticationService
import tl.bnctl.banking.ui.utils.DateUtils
import tl.bnctl.banking.util.Constants
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class TransferDataSource(private val transferService: TransferService) {
    companion object {
        val TAG: String = TransferDataSource::class.java.simpleName
    }

    val gson: Gson = Gson()

    suspend fun fetchTransfers(
        fromDate: Date,
        toDate: Date,
        pageNumber: Int,
        pageSize: Int
    ): Result<TransferHistoryResult> {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error(
                    "Session expired",
                    Constants.SESSION_EXPIRED_CODE
                )
            Log.d(
                TAG,
                "Fetching transfers with filter: Date: ($fromDate-$toDate), pageNumber: $pageNumber, pageSize: $pageSize"
            )

            val transfersResult: JsonObject
            transferService.fetchTransfers(
                accessToken,
                DateUtils.formatDateISO(fromDate), DateUtils.formatDateISO(toDate),
                pageNumber,
                pageSize
            ).also { transfersResult = it }
            val transfersList: List<TransferHistory> =
                transfersResult.getAsJsonArray("records").map { transferJsonObject ->
                    val transfer =
                        gson.fromJson(transferJsonObject.toString(), TransferHistory::class.java)
                    if (transfer.transferDirection == TransferDirection.DEBIT.direction) {
                        transfer.amount =
                            BigDecimal(transfer.amount).multiply(BigDecimal.valueOf(-1)).toString()
                    }
                    transfer
                }.toMutableList()
            val pagination = gson.fromJson(
                transfersResult.getAsJsonObject("pagination").toString(),
                Pagination::class.java
            )

            return Result.Success(TransferHistoryResult(transfersList, pagination))
        } catch (retrofitEx: HttpException) {
            return Result.createError(retrofitEx)
        } catch (ex: Exception) {
            return Result.createError(ex)
        }
    }

    // Use createAndExecuteTransfer for the BNCTL project
    suspend fun createTransfer(transferSummary: TransferSummary): Result<TransferCreate> {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)
            var creationResult: JsonObject
            try {
                val request = mutableMapOf(
                    "amount" to BigDecimal.valueOf(transferSummary.amount)
                        .setScale(2, RoundingMode.HALF_UP).toString(),
                    "description" to transferSummary.reason,
                    "additionalDescription" to transferSummary.additionalReason,
                    "sourceAccount" to transferSummary.from.accountNumber,
                    "sourceAccountId" to transferSummary.from.accountId,
                    "sourceAccountCurrency" to transferSummary.from.currencyName,
                    "destinationAccount" to transferSummary.to.accountNumber,
                    "destinationAccountCurrency" to transferSummary.currency,
                    "recipientName" to transferSummary.to.beneficiary.name,
                    "transferType" to transferSummary.transferType.type,
                    "executionType" to transferSummary.executionType.time,
                    "additionalDetails" to transferSummary.additionalDetails
                )

                if (transferSummary.newPayee != null) {
                    request["newPayee"] = transferSummary.newPayee!!
                }
                transferService.createTransfer(
                    accessToken, request
                ).also { creationResult = it }
            } catch (retrofitEx: HttpException) {
                return Result.createError(retrofitEx)
            }

            val transferCreateResult =
                Gson().fromJson(
                    creationResult.get("result").toString(),
                    TransferCreate::class.java
                )
            return Result.Success(transferCreateResult)
        } catch (ex: Exception) {
            return Result.createError(ex)
        }
    }

    suspend fun createAndExecuteTransfer(transferSummary: TransferSummary): Result<TransferConfirm> {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)
            var creationResult: JsonObject
            try {
                transferService.createAndExecuteTransfer(
                    accessToken, mapOf(
                        "amount" to BigDecimal.valueOf(transferSummary.amount)
                            .setScale(2, RoundingMode.HALF_UP).toString(),
                        "description" to transferSummary.reason,
                        "additionalDescription" to transferSummary.additionalReason,
                        "sourceAccount" to transferSummary.from.accountNumber,
                        "sourceAccountId" to transferSummary.from.accountId,
                        "sourceAccountCurrency" to transferSummary.from.currencyName,
                        "destinationAccount" to transferSummary.to.accountNumber,
                        "destinationAccountCurrency" to transferSummary.currency,
                        "recipientName" to transferSummary.to.beneficiary.name,
                        "transferType" to transferSummary.transferType.type,
                        "executionType" to transferSummary.executionType.time,
                        "additionalDetails" to transferSummary.additionalDetails
                    )
                ).also { creationResult = it }
            } catch (retrofitEx: HttpException) {
                return Result.createError(retrofitEx)
            }
            val transferCreateAndExecuteResult =
                Gson().fromJson(
                    creationResult.get("result").toString(),
                    TransferConfirm::class.java
                )
            return Result.Success(transferCreateAndExecuteResult)
        } catch (ex: Exception) {
            return Result.createError(ex)
        }
    }

    suspend fun confirmTransfer(transferConfirmation: TransferConfirmRequest): Result<TransferConfirm> {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)
            var confirmationResult: JsonObject
            try {
                transferService.confirmTransfer(
                    accessToken, transferConfirmation.validationRequestId,
                    transferConfirmation.secret,
                ).also { confirmationResult = it }
            } catch (retrofitEx: HttpException) {
                return Result.createError(retrofitEx)
            }

            val transferConfirmResult =
                Gson().fromJson(
                    confirmationResult.get("result").toString(),
                    TransferConfirm::class.java
                )
            return Result.Success(transferConfirmResult)
        } catch (ex: Exception) {
            return Result.createError(ex)
        }
    }
}
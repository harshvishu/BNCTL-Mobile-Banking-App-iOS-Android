package tl.bnctl.banking.data.transfers.pending

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.HttpException
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.fallback.FallbackRequestParams
import tl.bnctl.banking.data.transfers.model.PendingTransfer
import tl.bnctl.banking.data.transfers.model.PendingTransferConfirmOrRejectResult
import tl.bnctl.banking.services.AuthenticationService
import tl.bnctl.banking.util.Constants

class PendingTransferDataSource(private val pendingTransferService: PendingTransferService) {

    val gson: Gson = Gson()

    suspend fun fetchPendingTransfers(): Result<List<PendingTransfer>> {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)
            val pendingTransfersResult: JsonObject
            pendingTransferService.fetchPendingTransfers(accessToken)
                .also { pendingTransfersResult = it }
            val pendingTransfers: JsonArray = pendingTransfersResult.get("result") as JsonArray

            val pendingTransfersList: List<PendingTransfer> =
                pendingTransfers.map { pendingTransferJsonObject ->
                    gson.fromJson(pendingTransferJsonObject.toString(), PendingTransfer::class.java)
                }
            return Result.Success(pendingTransfersList)
        } catch (e: Throwable) {
            Log.e(
                PendingTransferDataSource::class.java.canonicalName,
                "Error getting pending transfers",
                e
            )
            return Result.Error("Error getting pending transfers: ${e.localizedMessage}")
        }
    }

    suspend fun rejectPendingTransfers(
        transferIds: List<String>,
        fallbackParams: FallbackRequestParams?
    ): Result<PendingTransferConfirmOrRejectResult> {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)

            val body: MutableMap<String, Any> = mutableMapOf(
                "transfers" to transferIds,
            )

            if (fallbackParams != null) {
                body["fallbackParams"] = fallbackParams
            }

            val rejectPendingTransfers: JsonObject =
                pendingTransferService.rejectPendingTransfers(
                    accessToken,
                    body
                )
            val rejectPendingTransfersResult: PendingTransferConfirmOrRejectResult =
                gson.fromJson(
                    rejectPendingTransfers.toString(),
                    PendingTransferConfirmOrRejectResult::class.java
                )

            return Result.Success(rejectPendingTransfersResult)
        } catch (e: Throwable) {
            Log.e(
                PendingTransferDataSource::class.java.canonicalName,
                "Error rejecting pending transfers",
                e
            )
            if (e is HttpException) {
                return Result.createError(e)
            }
            return Result.Error("Error rejecting pending transfers: ${e.localizedMessage}")
        }
    }

    suspend fun confirmPendingTransfers(
        transferIds: List<String>,
        fallbackParams: FallbackRequestParams?
    ): Result<PendingTransferConfirmOrRejectResult> {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)
            val body: MutableMap<String, Any> = mutableMapOf(
                "transfers" to transferIds,
            )

            if (fallbackParams != null) {
                body["fallbackParams"] = fallbackParams
            }

            val confirmPendingTransfers: JsonObject =
                pendingTransferService.confirmPendingTransfers(
                    accessToken,
                    body
                )
            val confirmPendingTransfersResult: PendingTransferConfirmOrRejectResult =
                gson.fromJson(
                    confirmPendingTransfers.toString(),
                    PendingTransferConfirmOrRejectResult::class.java
                )

            return Result.Success(confirmPendingTransfersResult)
        } catch (e: Throwable) {
            Log.e(
                PendingTransferDataSource::class.java.canonicalName,
                "Error confirming pending transfers",
                e
            )
            if (e is HttpException) {
                return Result.createError(e)
            }
            return Result.Error("Error confirming pending transfers: ${e.localizedMessage}")
        }
    }
}
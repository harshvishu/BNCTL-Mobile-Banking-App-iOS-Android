package tl.bnctl.banking.data.templates

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.HttpException
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.templates.model.Bank
import tl.bnctl.banking.data.templates.model.Template
import tl.bnctl.banking.data.templates.model.TemplateRequest
import tl.bnctl.banking.data.transfers.enums.TransferType
import tl.bnctl.banking.services.AuthenticationService
import tl.bnctl.banking.util.Constants

class TemplatesDataSource(
    private val templatesService: TemplatesService
) {

    val gson: Gson = Gson()

    companion object {
        val TAG: String = TemplatesDataSource::class.java.simpleName

        val TEMPLATE_TYPE_TO_TRANSFER_TYPE = mapOf(
            "0" to TransferType.INTRABANK.toString().lowercase(),
            "1" to TransferType.INTERBANK.toString().lowercase(),
            "2" to TransferType.INTERNATIONAL.toString().lowercase(),
        )

        // Reverse map so we can do lookups by transfer type
        val TRANSFER_TYPE_TO_TEMPLATE_TYPE_ID =
            TEMPLATE_TYPE_TO_TRANSFER_TYPE.entries.associateBy({ it.value }) { it.key }
    }

    suspend fun templatesFetch(): Result<List<Template>> {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)
            val templatesResult: JsonObject
            templatesService.fetchTemplates(accessToken).also { templatesResult = it }
            val templatesArray = templatesResult.getAsJsonArray("result")
            val templatesList: List<Template> = templatesArray.map { templateJsonObject ->
                val template = gson.fromJson(templateJsonObject.toString(), Template::class.java)
                setTemplateTransferType(template)
                template
            }
            return Result.Success(templatesList)
        } catch (retrofitEx: HttpException) {
            return Result.createError(retrofitEx)
        } catch (ex: Exception) {
            return Result.createError(ex)
        }
    }

    suspend fun fetchBanks(): Result<List<Bank>> {
        Log.v(TAG, "Getting banks")
        return try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)
            val banksResult: JsonObject = templatesService.fetchBanks(accessToken)
            val banksListObject = (banksResult.get("result").asJsonObject)
            val banksList = banksListObject.asJsonObject.keySet().map { bankId ->
                gson.fromJson(banksListObject[bankId].toString(), Bank::class.java)
            }
            Result.Success(banksList)
        } catch (retrofitEx: HttpException) {
            Result.createError(retrofitEx)
        } catch (ex: Exception) {
            Result.createError(ex)
        }
    }

    suspend fun deleteTemplate(payeeId: String): Result<Boolean> {
        val accessToken = AuthenticationService.getInstance().getAuthToken()
            ?: return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)
        return try {
            templatesService.deleteTemplate(accessToken, payeeId)
            Result.Success(true)
        } catch (ex: Exception) {
            Result.createError(ex)
        }
    }

    suspend fun createTemplate(templateRequest: TemplateRequest): Result<Template>? {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)
            val templatesResult: JsonObject
            templatesService.createTemplate(accessToken, templateRequest)
                .also { templatesResult = it }
            val templateJsonObject = templatesResult.getAsJsonObject("result")
            val template = gson.fromJson(templateJsonObject.toString(), Template::class.java)

            return Result.Success(template)
        } catch (retrofitEx: HttpException) {
            return Result.createError(retrofitEx)
        } catch (ex: Exception) {
            return Result.createError(ex)
        }
    }

    suspend fun editTemplate(payeeId: String, templateRequest: TemplateRequest): Result<Template>? {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)
            val templatesResult: JsonObject
            templatesService.editTemplate(accessToken, payeeId, templateRequest)
                .also { templatesResult = it }
            val templateJsonObject = templatesResult.getAsJsonObject("result")
            val template = gson.fromJson(templateJsonObject.toString(), Template::class.java)

            return Result.Success(template)
        } catch (retrofitEx: HttpException) {
            return Result.createError(retrofitEx)
        } catch (ex: Exception) {
            return Result.createError(ex)
        }
    }

    private fun transferTypeFromTemplateAccountTypeId(type: String?): String? {
        return TEMPLATE_TYPE_TO_TRANSFER_TYPE[type]
    }

    private fun setTemplateTransferType(template: Template) {
        if (template.type == "bank") {
            template.transferType =
                transferTypeFromTemplateAccountTypeId(template.accountTypeId)
        } else {
            // This *should* only happen when there's a payee with type wallet
            template.transferType = TransferType.UNKNOWN.toString().lowercase()
            Log.w(
                TAG,
                "Payee with invalid type found! id: ${template.payeeId}, type: ${template.type}"
            )
        }
    }
}

package tl.bnctl.banking.data.cards

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.accounts.model.AccountStatement
import tl.bnctl.banking.data.accounts.model.StatementsDataHolder
import tl.bnctl.banking.data.cards.model.*
import tl.bnctl.banking.services.AuthenticationService
import tl.bnctl.banking.util.Constants
import java.io.InputStream
import kotlin.reflect.KClass

class CardsDataSource(private val cardsService: CardsService) {

    val gson: Gson = Gson()

    suspend fun cardFetch(): Result<List<Card>> {
        Log.v(TAG, "Getting cards")
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)
            val cardsResult: JsonObject = cardsService.cards(accessToken)
            val cardsList: List<Card> =
                (cardsResult.get("result") as JsonArray).map { cardJsonObject ->
                    gson.fromJson(cardJsonObject.toString(), Card::class.java)
                }
            Log.v(TAG, "Returned ${cardsList.size} cards")
            return Result.Success(cardsList)
        } catch (retrofitEx: HttpException) {
            return Result.createError(retrofitEx)
        } catch (ex: Exception) {
            return Result.createError(ex)
        }
    }

    suspend fun fetchCardProducts(): Result<List<CardProduct>> {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)
            val productsResult: JsonObject = cardsService.fetchCardProducts(accessToken)
            val productsList: List<CardProduct> =
                (productsResult.get("result") as JsonArray).map { productJsonObject ->
                    gson.fromJson(productJsonObject.toString(), CardProduct::class.java)
                }
            return Result.Success(productsList)
        } catch (retrofitEx: HttpException) {
            return Result.createError(retrofitEx)
        } catch (ex: Exception) {
            return Result.createError(ex)
        }
    }

    suspend fun newDebitCard(request: NewDebitCardRequest): Result<NewDebitCardResult> {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)
            val newDebitCardResult = cardsService.newDebitCardRequest(accessToken, request)

            val success = newDebitCardResult.get("success").asBoolean
            return Result.Success(NewDebitCardResult(success))
        } catch (retrofitEx: HttpException) {
            return Result.createError(retrofitEx)
        } catch (ex: Exception) {
            return Result.createError(ex)
        }
    }

    suspend fun fetchCreditCardStatements(
        cardNumber: String,
        fromDate: String,
        toDate: String
    ): Result<List<CreditCardStatement>> {
        val e:Execute<String, JsonObject> = object : Execute<String, JsonObject> {
            override fun exec(
                accessToken: String,
                cardNumber: String,
                fromDate: String,
                toDate: String): JsonObject {
               return runBlocking  {
                    return@runBlocking cardsService.fetchCreditCardStatements( accessToken, cardNumber, fromDate, toDate)
                }
            }
        }
        return fetchCardStatement(cardNumber, fromDate, toDate, e, CreditCardStatement::class)
    }

    suspend fun fetchCardStatement(
        cardNumber: String,
        fromDate: String,
        toDate: String
    ): Result<List<StatementsDataHolder>> {
        val e: Execute<String, JsonObject> = object : Execute<String, JsonObject> {
            override fun exec(
                accessToken: String,
                cardNumber: String,
                fromDate: String,
                toDate: String
            ): JsonObject {
                return runBlocking {
                    return@runBlocking cardsService.fetchCardStatements(accessToken,
                        CardStatementRequest(cardNumber, fromDate, toDate))
                }
            }
        }
        return fetchCardStatement(cardNumber, fromDate, toDate, e, AccountStatement::class)
    }

    private fun <T: Any> fetchCardStatement(
        cardNumber: String,
        fromDate: String,
        toDate: String,
        func: Execute<String, JsonObject>,
        resultClass: KClass<T>
    ): Result<List<T>> {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)
            val statementResult: JsonObject = func.exec(
                accessToken,
                cardNumber,
                fromDate,
                toDate
            )
            val statementList: List<T> =
                (statementResult.get("result") as JsonArray).map { productJsonObject ->
                    gson.fromJson(productJsonObject.toString(), resultClass.java)
                }
            return Result.Success(statementList)
        } catch (retrofitEx: HttpException) {
            return Result.createError(retrofitEx)
        } catch (ex: Exception) {
            return Result.createError(ex)
        }
    }

    interface Execute<A, T> {
       fun exec(accessToken: A, cardNumber: A, fromDate: A, toDate: A): T
    }

    /**
     * Suppressing BlockingMethodInNonBlockingContext
     */
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun downloadCreditCardStatement(
        fileName: String
    ): Result<InputStream> {
        try {
            val accessToken = AuthenticationService.getInstance().getAuthToken()
                ?: return Result.Error("Session expired", Constants.SESSION_EXPIRED_CODE)

            val fileResult = cardsService.downloadCreditCardStatement(
                accessToken,
                fileName
            )
            if (fileResult.errorBody() != null) {
                throw Exception(fileResult.errorBody()!!.string())
            }
            val inputStream: InputStream = fileResult.body()!!.byteStream()
            return Result.Success(inputStream)
        } catch (retrofitEx: HttpException) {
            return Result.createError(retrofitEx)
        } catch (ex: Exception) {
            return Result.createError(ex)
        }
    }

    companion object {
        const val TAG = "CardsDataSource"
    }
}
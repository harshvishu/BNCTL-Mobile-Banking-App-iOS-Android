package tl.bnctl.banking.data

import android.util.Log
import com.google.gson.Gson
import retrofit2.HttpException
import tl.bnctl.banking.util.Constants
import tl.bnctl.banking.util.camelToSnakeCase
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Generalized Result class,that represents the two possible outcomes
 * when consuming the Solution Controller APIs.
 * Success just has an arbitrary "data" field in it.
 * Error has "message", "code" and "target", which are used to display error messages to the end user.
 */
sealed class Result<out T : Any> {

    data class Success<out T : Any>(val data: T) : Result<T>()

    class Error(
        val message: String,
        val code: String? = null,
        val target: String? = null,
        val details: String? = null,
        val statusCode: Int? = 500,
    ) : Result<Nothing>() {
        /**
         * This is a helper function that allows easy parsing of the possible errors.
         * The logic is as follows.
         * Take the following error response:
         * <code>
         *   {
         *     "error": {
         *       "code": "AuthenticationException",
         *       "target": "invalidUsername"
         *       "message": "Login error: invalid username"
         *     }
         *   }
         * <code>
         * We take the code and target and do the following steps:
         *   1. We start with "error"
         *   2. If code and target are null, just append "_generic" and return "error_generic"
         *   3. The "code" AuthenticationException is snake-cased and reduced. Result -> "authentication"
         *   4. The "target" is snake-cased. Result -> "invalid_username"
         *   5. Everything is concatenated to the following string -> "error_authentication_invalid_username"
         *
         * @return An error resource string.
         */
        fun getErrorString(): String {
            val stringBuilder = StringBuilder("error")
            if (code == null && target == null) stringBuilder.append("_generic")
            if (code != null) stringBuilder.append(
                "_" + code.replace("Exception", "").camelToSnakeCase()
            )
            if (target != null) stringBuilder.append("_" + target.camelToSnakeCase())
            if (details != null) stringBuilder.append("_" + details.camelToSnakeCase())
            return stringBuilder.toString()
        }
    }

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[message=$message]"
        }
    }

    companion object {
        val TAG: String = Result::class.java.simpleName
        // Use details to specify the error group, otherwise a generic group of error will be used
        fun createError(retrofitEx: HttpException, details: String? = null): Error {
            val errorBodyStr = retrofitEx.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(errorBodyStr, Map::class.java)
            // Try to parse the error from the backend. If it's not a valid error object, return generic error
            Log.e(TAG, "createError: $errorBodyStr")
            val error: Map<*, *> = try {
                errorBody["error"] as Map<*, *>
            } catch (e: Exception) {
                mapOf(
                    "message" to errorBodyStr,
                    "code" to Constants.SESSION_EXPIRED_CODE,
                    "target" to if (errorBody != null && errorBody.containsKey("target")) errorBody["target"] else "unknown"
                )
            }
            var message = ""
            if (error["message"] != null) {
                message = error["message"] as String
            }

            return Error(
                message,
                error["code"] as String,
                error["target"] as String?,
                details,
                retrofitEx.code()
            )
        }

        fun createError(exception: Exception): Error {
            if (exception is HttpException) {
                return createError(exception)
            }
            if ((exception is IOException || exception is SocketTimeoutException || exception is UnknownHostException) && exception.cause != null) {
                val cause: Throwable = exception.cause!!
                if (cause is ConnectException || cause is SocketTimeoutException || cause is UnknownHostException) {
                    return Error(cause.toString(), null, "noConnection")
                }
            }
            return Error(exception.message!!, "UnknownException")
        }
    }
}

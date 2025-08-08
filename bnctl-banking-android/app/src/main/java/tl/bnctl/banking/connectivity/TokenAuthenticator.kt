package tl.bnctl.banking.connectivity

import android.util.Log
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.gson.JsonObject
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import tl.bnctl.banking.data.login.LoginService
import tl.bnctl.banking.services.AuthenticationService

/**
 * Used for refreshing the JWT token when it expires while the user is using the app
 */
class TokenAuthenticator : Authenticator {

    // TODO: This is not an ideal solution. This will most definitely break at some stupid point in time
    // TODO: Parse the response here and decide how to proceed based on the exception
    private val ignoredEndpoints = arrayListOf("login", "refresh", "current", "confirm")

    override fun authenticate(route: Route?, response: Response): Request? {
        // Don't try to refresh after getting 401 for some endpoints
        val pathSegments = response.request.url.pathSegments
        val lastPathSegment = pathSegments[pathSegments.size - 1]
        Log.d(TAG, "authenticate: IN")
        if (ignoredEndpoints.contains(lastPathSegment)) {
            return null;
        }
        Firebase.crashlytics.log("authenticate: requesting new token for ${response.request.url}")

        val authenticationService =
            RetrofitService.getInstance().getService(LoginService::class.java)
        val refreshToken = AuthenticationService.getInstance().getRefreshToken()
        lateinit var newAccessToken: String

        val callSync: retrofit2.Call<JsonObject> = authenticationService.refreshToken(
            mapOf(
                "refreshToken" to refreshToken!!
            )
        )
        val execute = try {
            callSync.execute()
        } catch (e: Exception) {
            Firebase.crashlytics.log("Error requesting new token: ${e.message}")
            throw e
        }
        if (execute.isSuccessful && execute.body() != null) {
            val responseJsonObject = execute.body()!!.asJsonObject.get("result").asJsonObject
            newAccessToken = responseJsonObject.get("accessToken").asString
            val newRefreshToken = responseJsonObject.get("refreshToken").asString
            AuthenticationService.getInstance().saveRefreshedToken(newAccessToken, newRefreshToken)
            return response.request.newBuilder()
                .header("Authorization", newAccessToken)
                .build();
        } else {
            Firebase.crashlytics.log("Error requesting new token: ${execute.errorBody()}")
            return null;
        }
    }

    companion object {
        val TAG = TokenAuthenticator::class.java.simpleName
    }
}
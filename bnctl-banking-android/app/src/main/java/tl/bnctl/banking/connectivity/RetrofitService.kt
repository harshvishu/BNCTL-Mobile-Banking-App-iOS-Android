package tl.bnctl.banking.connectivity

import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tl.bnctl.banking.BuildConfig
import tl.bnctl.banking.services.SettingsService
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Singleton Retrofit services container.
 * This class holds reference to a Retrofit builder,
 * and to a Map of all API services that this app consumes.
 */
class RetrofitService private constructor() {

    private val loggingInterceptor: HttpLoggingInterceptor by lazy { HttpLoggingInterceptor() }

    private var httpClient: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(
            150,
            TimeUnit.SECONDS
        ) // 2 min, 30 sec.. in order to handle SCA expired, which takes 2 minutes to expire.
        .connectTimeout(10, TimeUnit.SECONDS)
        .apply {
            if (BuildConfig.DEBUG)
                addInterceptor(loggingInterceptor)
        }
        .authenticator(TokenAuthenticator())
        .addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            requestBuilder.addHeader("Accept-Language", Locale.getDefault().toLanguageTag())
            try {
                val request = requestBuilder.build()
                if (!request.url.toString().contains("login")) {
                    Firebase.crashlytics.log(
                        "Request: ${request.url}; method: ${request.method}; ${if (request.body != null) "body: ${request.body}" else ""}"
                    )
                }
                val response: Response = chain.proceed(request)
                if (response.code >= 500) {
                    val errorText = "${response}; responseBody: ${response.peekBody(Long.MAX_VALUE).string()}"
                    Firebase.crashlytics.log(errorText)
                    Firebase.crashlytics.recordException(Exception(errorText))
                }
                response
            } catch (e: Exception) {
                when (e) {
                    is ConnectException,
                    is SocketTimeoutException,
                    is UnknownHostException -> {
                        throw IOException(e)
                    }
                    else -> throw e
                }
            }
        }
        .build()

    private var retrofit: Retrofit = Retrofit.Builder()
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(SettingsService.getInstance().environment.host)
        .build()

    private val apiServices = HashMap<String, Any>()

    init {
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    }

    fun <T : Any> getService(serviceClass: Class<T>): T {
        val retrofitService = getInstance()
        if (apiServices.containsKey(serviceClass.name)) {
            return apiServices[serviceClass.name] as T
        }
        val service = retrofitService.retrofit.create(serviceClass)
        apiServices[serviceClass.name] = service!!
        return service
    }

    companion object {
        val TAG = RetrofitService::class.java.simpleName

        @Volatile
        private var INSTANCE: RetrofitService? = null

        fun getInstance(): RetrofitService =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: RetrofitService().also { INSTANCE = it }
            }
    }
}
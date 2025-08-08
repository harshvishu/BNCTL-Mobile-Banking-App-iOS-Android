package tl.bnctl.banking.data.config

import com.google.gson.Gson
import retrofit2.HttpException
import tl.bnctl.banking.BuildConfig
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.config.model.ApplicationConfigurationDto

class AppConfigDataSource(
    private val appConfigService: AppConfigService
) {

    val gson: Gson = Gson()

    suspend fun getAppConfig(): Result<ApplicationConfigurationDto> {
        return try {
            val getAppConfigResult =
                appConfigService.getAppConfig(BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
            Result.Success(
                gson.fromJson(
                    getAppConfigResult.get("result").toString(),
                    ApplicationConfigurationDto::class.java
                )
            )
        } catch (retrofitEx: HttpException) {
            Result.createError(retrofitEx)
        } catch (ex: Exception) {
            Result.createError(ex)
        }
    }

}
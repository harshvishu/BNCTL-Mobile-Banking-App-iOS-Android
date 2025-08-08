package tl.bnctl.banking.services

import android.content.Context
import tl.bnctl.banking.BuildConfig
import tl.bnctl.banking.model.settings.Environment

class SettingsService private constructor(context: Context) {

    val environment: Environment = Environment(BuildConfig.BASE_URL, BuildConfig.ENV_NAME)

    companion object {
        @Volatile
        private var INSTANCE: SettingsService? = null

        fun initSettingsService(context: Context): SettingsService =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingsService(context).also { INSTANCE = it }
            }

        fun getInstance(): SettingsService {
            return INSTANCE!!
        }
    }

}
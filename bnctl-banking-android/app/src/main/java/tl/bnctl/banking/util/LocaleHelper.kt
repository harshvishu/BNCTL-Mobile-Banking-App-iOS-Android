package tl.bnctl.banking.util

import android.annotation.TargetApi
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class LocaleHelper {

    companion object {
        private const val SELECTED_LANGUAGE = "selectedLanguage"

        fun onAttach(context: Context): Context {
            val lang: String = getPersistedData(context, Locale.getDefault().language)
            return setLocale(context, lang)
        }

        private fun getPersistedData(context: Context, defaultLanguage: String): String {
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences(
                    tl.bnctl.banking.BankingApplication.appCode,
                    AppCompatActivity.MODE_PRIVATE
                )
            return sharedPreferences.getString(SELECTED_LANGUAGE, defaultLanguage)!!
        }

        fun setLocale(context: Context, language: String): Context {
            persist(context, language)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return updateResources(context, language)
            }
            return updateResourcesLegacy(context, language)
        }

        fun getCurrentLanguage(context: Context): String {
            return getPersistedData(context, Locale.getDefault().language)
        }

        private fun persist(context: Context, defaultLanguage: String) {
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences(
                    tl.bnctl.banking.BankingApplication.appCode,
                    AppCompatActivity.MODE_PRIVATE
                )
            sharedPreferences.edit().putString(SELECTED_LANGUAGE, defaultLanguage).apply()
        }

        @TargetApi(Build.VERSION_CODES.N)
        private fun updateResources(context: Context, language: String): Context {
            val locale = Locale(language)
            Locale.setDefault(locale)
            val configuration: Configuration = context.resources.configuration
            configuration.setLocale(locale)
            return context.createConfigurationContext(configuration)
        }

        @Suppress("DEPRECATION")
        private fun updateResourcesLegacy(context: Context, language: String): Context {
            val locale = Locale(language)
            Locale.setDefault(locale)
            val resources: Resources = context.resources
            val configuration: Configuration = resources.configuration
            configuration.setLocale(locale)
            resources.updateConfiguration(configuration, resources.displayMetrics)
            return context
        }
    }
}

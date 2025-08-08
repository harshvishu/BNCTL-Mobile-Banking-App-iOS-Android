package tl.bnctl.banking.data.config

import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.config.model.ApplicationConfigurationDto

class AppConfigRepository(private val dataSource: AppConfigDataSource) {

    suspend fun getAppConfig(): Result<ApplicationConfigurationDto> {
        return dataSource.getAppConfig()
    }


}
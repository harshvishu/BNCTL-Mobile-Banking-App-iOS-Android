package tl.bnctl.banking.data.templates

import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.templates.model.Bank
import tl.bnctl.banking.data.templates.model.Template
import tl.bnctl.banking.data.templates.model.TemplateRequest

class TemplatesRepository(private val dataSource: TemplatesDataSource) {

    var templates: List<Template>? = null
        private set

    suspend fun templatesFetch(): Result<List<Template>> {
        val result = dataSource.templatesFetch()
        if (result is Result.Success) {
            templates = result.data
        }
        return result
    }

    suspend fun fetchBanks(): Result<List<Bank>> {
        return dataSource.fetchBanks()
    }

    suspend fun deleteTemplate(payeeId: String): Result<Boolean> {
        return dataSource.deleteTemplate(payeeId)
    }

    suspend fun createTemplate(templateRequest: TemplateRequest): Result<Template>? {
        return dataSource.createTemplate(templateRequest)
    }

    suspend fun editTemplate(payeeId: String, templateRequest: TemplateRequest): Result<Template>? {
        return dataSource.editTemplate(payeeId, templateRequest)
    }

}
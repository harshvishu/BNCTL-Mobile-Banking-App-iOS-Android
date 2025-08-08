package tl.bnctl.banking.ui.banking.fragments.transfers.templates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tl.bnctl.banking.connectivity.RetrofitService
import tl.bnctl.banking.data.templates.TemplatesDataSource
import tl.bnctl.banking.data.templates.TemplatesRepository
import tl.bnctl.banking.data.templates.TemplatesService
import tl.bnctl.banking.ui.banking.fragments.transfers.templates.details.TemplateDetailsViewModel

class TemplatesViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Use this factory for all the template-related ViewModel creation to avoid duplicating code
        if (modelClass.isAssignableFrom(TemplatesViewModel::class.java)) {
            return TemplatesViewModel(
                templatesRepository = TemplatesRepository(
                    dataSource = TemplatesDataSource(getTemplatesService())
                )
            ) as T
        }
        if (modelClass.isAssignableFrom(TemplateDetailsViewModel::class.java)) {
            return TemplateDetailsViewModel(
                templatesRepository = TemplatesRepository(
                    dataSource = TemplatesDataSource(getTemplatesService())
                )
            ) as T
        }
        if (modelClass.isAssignableFrom(CreateEditTemplateViewModel::class.java)) {
            return CreateEditTemplateViewModel(
                templatesRepository = TemplatesRepository(
                    dataSource = TemplatesDataSource(getTemplatesService())
                )
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }

    private fun getTemplatesService(): TemplatesService {
        val retrofit = RetrofitService.getInstance()
        return retrofit.getService(TemplatesService::class.java)
    }
}
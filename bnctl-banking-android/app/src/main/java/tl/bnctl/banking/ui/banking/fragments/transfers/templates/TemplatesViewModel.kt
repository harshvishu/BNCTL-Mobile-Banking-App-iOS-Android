package tl.bnctl.banking.ui.banking.fragments.transfers.templates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.templates.TemplatesRepository
import tl.bnctl.banking.data.templates.model.Template

class TemplatesViewModel(
    private val templatesRepository: TemplatesRepository
) : ViewModel() {

    private val _templates = MutableLiveData<Result<List<Template>>>().apply {}
    val templates: LiveData<Result<List<Template>>> = _templates

    fun templateFetch() {
        viewModelScope.launch(Dispatchers.IO) {
            var result = templatesRepository.templatesFetch()
            // Not filtering templates according to permissions for BNCTL
            /*if (result is Result.Success) {
                result = Result.Success(
                    PermissionService.getInstance()
                        .filterTemplatesAccordingToPermissions(result.data)
                )
            }*/
            _templates.postValue(result)
        }
    }

    fun setTemplates(templates: Result<List<Template>>) {
        _templates.postValue(templates)
    }
}
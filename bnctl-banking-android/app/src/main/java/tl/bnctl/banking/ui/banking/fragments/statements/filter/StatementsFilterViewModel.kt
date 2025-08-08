package tl.bnctl.banking.ui.banking.fragments.statements.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tl.bnctl.banking.R
import tl.bnctl.banking.ui.banking.fragments.statements.adapter.StatementsFilterData
import java.util.*

class StatementsFilterViewModel : ViewModel() {

    private val _statementsFilter = MutableLiveData<StatementsFilterData>().apply {
        StatementsFilterData(Date(), Date())
    }
    private val _resultKey = MutableLiveData<String>()

    val statementsFilter: LiveData<StatementsFilterData> = _statementsFilter
    val resultKey: LiveData<String> = _resultKey

    private val _startDateError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    val startDateError: LiveData<Int?> = _startDateError

    private val _endDateError: MutableLiveData<Int?> by lazy { MutableLiveData<Int?>() }
    val endDateError: LiveData<Int?> = _endDateError


    fun updateStartDate(newStartDate: Date) {
        _statementsFilter.postValue(
            _statementsFilter.value!!.copy(
                startDate = newStartDate,
                pageNumber = 1
            )
        )
    }

    fun updateEndDate(newEndDate: Date) {
        _statementsFilter.postValue(
            _statementsFilter.value!!.copy(
                endDate = newEndDate,
                pageNumber = 1
            )
        )
    }

    fun initFilter(initFilter: StatementsFilterData) {
        _statementsFilter.value = initFilter
    }

    fun setResultKey(resultKey: String) {
        _resultKey.value = resultKey
    }

    fun validateStartDate() {
        val startDate = _statementsFilter.value?.startDate
        if (startDate == null) {
            raiseStartDateError(R.string.common_error_field_required)
        } else if (_statementsFilter.value?.endDate != null &&
            _statementsFilter.value!!.endDate.before(startDate)
        ) {
            raiseStartDateError(R.string.start_date_must_be_before_end_date)
        } else {
            clearStartDateError()
        }

    }

    private fun clearStartDateError() {
        _startDateError.value = null
    }

    private fun raiseStartDateError(resourceId: Int) {
        _startDateError.value = resourceId
    }

    fun validateEndDate() {
        val endDate = _statementsFilter.value?.endDate
        if (endDate == null) {
            raiseEndDateError(R.string.common_error_field_required)
        } else if (_statementsFilter.value?.startDate != null &&
            _statementsFilter.value!!.startDate.after(endDate)
        ) {
            raiseEndDateError(R.string.end_date_must_be_after_start_date)
        } else {
            clearEndDateError()
        }
    }


    private fun clearEndDateError() {
        _endDateError.value = null
    }

    private fun raiseEndDateError(resourceId: Int) {
        _endDateError.value = resourceId
    }
}
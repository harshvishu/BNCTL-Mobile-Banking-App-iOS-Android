package tl.bnctl.banking.ui.banking.fragments.services.utilityBills.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.billpayments.BillPaymentRepository
import tl.bnctl.banking.data.billpayments.model.BillPaymentHistory
import tl.bnctl.banking.ui.banking.fragments.statements.adapter.StatementsFilterData

class UtilityBillHistoryViewModel(
    private val billPaymentRepository: BillPaymentRepository
) : ViewModel() {

    private val _billPaymentHistory = MutableLiveData<Result<List<BillPaymentHistory>>>().apply {}
    private val _isLoadingBillPaymentHistory: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>(false) }

    val billPaymentHistory: LiveData<Result<List<BillPaymentHistory>>> = _billPaymentHistory
    val isLoadingUtilityBillPaymentHistory: LiveData<Boolean> = _isLoadingBillPaymentHistory

    fun fetchBillPayments(filterData: StatementsFilterData) {
        _isLoadingBillPaymentHistory.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val result = billPaymentRepository.fetchBillPayments(
                filterData.startDate, filterData.endDate)
            _billPaymentHistory.postValue(result)
            _isLoadingBillPaymentHistory.postValue(false)
        }
    }
}
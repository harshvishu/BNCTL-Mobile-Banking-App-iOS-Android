package tl.bnctl.banking.ui.banking.fragments.services.utilityBills

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tl.bnctl.banking.data.Result
import tl.bnctl.banking.data.billers.BillerRepository
import tl.bnctl.banking.data.billers.model.UtilityBill
import tl.bnctl.banking.data.eod.EoDRepository
import tl.bnctl.banking.data.eod.model.EoDResult

class UtilityBillViewModel(
    private val billerRepository: BillerRepository,
    private val eoDRepository: EoDRepository
) : ViewModel() {

    private val _TAG = UtilityBillViewModel::class.simpleName

    private val _myBillers = MutableLiveData<Result<List<UtilityBill>>>().apply {}
    val myBillers: LiveData<Result<List<UtilityBill>>> = _myBillers

    private val _eodResult = MutableLiveData<EoDResult>()
    val eodResult: LiveData<EoDResult> = _eodResult

    fun fetchMyBillers() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = billerRepository.fetchMyBillers()
            _myBillers.postValue(result)
        }
    }

    fun selectAll() {
        changeBillersSelection(true)
    }

    fun unselectAll() {
        changeBillersSelection(false)
    }

    private fun changeBillersSelection(select: Boolean) {
        if (_myBillers.value is Result.Success) {
            val billers: List<UtilityBill> = (_myBillers.value as Result.Success).data
            repeat(billers.size) {
                val utilityBill = billers[it]
                utilityBill.isSelected = select && utilityBill.billAmount.toDouble() > 0
            }
            _myBillers.postValue(Result.Success(billers))
        }
    }

    fun checkEoD() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = eoDRepository.checkEoD()
            if (result is Result.Success) {
                _eodResult.postValue(EoDResult(true, result.data))
            } else {
                val errorResult: Result.Error = result as Result.Error
                _eodResult.postValue(EoDResult(error = errorResult))
                Log.e(_TAG, errorResult.message)
            }
        }
    }

    fun clearBillers() {
        _myBillers.value = null
    }

}

package xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.fcr.sberrunner.utils.ServiceStatesEnum

class RunViewModel : ViewModel() {

    private val _stateLiveData = MutableLiveData<ServiceStatesEnum>()
//    private val _locationLiveData = MutableLiveData<Location>()
//    private val _errorLiveData = SingleLiveEvent<String>()


    fun getCurrentLocation() {

    }

    override fun onCleared() {
        super.onCleared()
    }

    val stateLiveData: LiveData<ServiceStatesEnum>
        get() = _stateLiveData
//    val locationLiveData: LiveData<Location>
//        get() = _locationLiveData
//    val errorLiveData: LiveData<String>
//        get() = _errorLiveData
}
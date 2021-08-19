package xyz.fcr.sberrunner.viewmodels.main_viewmodels

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.fcr.sberrunner.utils.ServiceStatesEnum
import xyz.fcr.sberrunner.viewmodels.SingleLiveEvent

class RunViewModel : ViewModel() {

    private val _stateLiveData = MutableLiveData<ServiceStatesEnum>()
    private val _locationLiveData = MutableLiveData<Location>()
    private val _errorLiveData = SingleLiveEvent<String>()


    fun getCurrentLocation() {

    }

    override fun onCleared() {
        super.onCleared()
    }

    val progressLiveData: LiveData<ServiceStatesEnum>
        get() = _stateLiveData
    val locationLiveData: LiveData<Location>
        get() = _locationLiveData
    val errorLiveData: LiveData<String>
        get() = _errorLiveData
}
package xyz.fcr.sberrunner.viewmodels.main_viewmodels

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import io.reactivex.rxjava3.disposables.Disposable
import xyz.fcr.sberrunner.view.App
import javax.inject.Inject

class MapViewModel : ViewModel() {

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val _progressLiveData = MutableLiveData<Boolean>()
    private val _locationLiveData = MutableLiveData<Location>()
    private val _errorLiveData = MutableLiveData<String>()

    private var disposable: Disposable? = null

    init {
        App.appComponent.inject(mapViewModel = this@MapViewModel)
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        _progressLiveData.postValue(true)

        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            when {
                task.isSuccessful -> {
                    _locationLiveData.postValue(task.result)
                }

                else -> _errorLiveData.postValue(task.exception.toString())
            }

            _progressLiveData.postValue(false)
        }
    }

    override fun onCleared() {
        super.onCleared()

        disposable?.dispose()
        disposable = null
    }

    val progressLiveData: LiveData<Boolean>
        get() = _progressLiveData
    val locationLiveData: LiveData<Location>
        get() = _locationLiveData
    val errorLiveData: LiveData<String>
        get() = _errorLiveData
}
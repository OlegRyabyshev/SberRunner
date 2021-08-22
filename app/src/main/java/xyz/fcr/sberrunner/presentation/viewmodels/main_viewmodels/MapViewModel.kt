package xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import xyz.fcr.sberrunner.utils.Constants.FASTEST_LOCATION_UPDATE_INTERVAL
import xyz.fcr.sberrunner.utils.Constants.NON_VALID
import xyz.fcr.sberrunner.presentation.App
import xyz.fcr.sberrunner.presentation.viewmodels.SingleLiveEvent
import javax.inject.Inject

class MapViewModel @Inject constructor(private val fusedLocationProviderClient: FusedLocationProviderClient) : ViewModel() {

    private val _progressLiveData = MutableLiveData<Boolean>()
    private val _locationLiveData = MutableLiveData<Location>()
    private val _errorLiveData = SingleLiveEvent<String>()

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        _progressLiveData.postValue(true)

        fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
            val location: Location? = task.result
            if (location == null) {
                _errorLiveData.postValue(NON_VALID)
                requestUpdateLocation()
            } else {
                _locationLiveData.postValue(task.result)
            }

            _progressLiveData.postValue(false)
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestUpdateLocation() {
        val request = LocationRequest.create().apply {
            fastestInterval = FASTEST_LOCATION_UPDATE_INTERVAL
            priority = PRIORITY_HIGH_ACCURACY
            numUpdates = 1
        }

        fusedLocationProviderClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)

            result.lastLocation.let { location ->
                _locationLiveData.postValue(location)
            }
        }
    }

    val progressLiveData: LiveData<Boolean>
        get() = _progressLiveData
    val locationLiveData: LiveData<Location>
        get() = _locationLiveData
    val errorLiveData: LiveData<String>
        get() = _errorLiveData
}
package xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import xyz.fcr.sberrunner.presentation.viewmodels.SingleLiveEvent
import xyz.fcr.sberrunner.utils.Constants.MAP_LAT_KEY
import xyz.fcr.sberrunner.utils.Constants.MAP_LON_KEY
import xyz.fcr.sberrunner.utils.Constants.MOSCOW_LAT
import xyz.fcr.sberrunner.utils.Constants.MOSCOW_LON
import xyz.fcr.sberrunner.utils.Constants.NON_VALID
import javax.inject.Inject

class MapViewModel @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _progressLiveData = MutableLiveData<Boolean>()
    private val _locationLiveData = MutableLiveData<Location>()
    private val _historyLiveData = MutableLiveData<LatLng>()
    private val _errorLiveData = SingleLiveEvent<String>()

    private var cancellationToken: CancellationTokenSource? = null
    private var isAlreadyLoading = false

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {

        if (isAlreadyLoading) return

        isAlreadyLoading = true
        _progressLiveData.postValue(true)

        cancellationToken = CancellationTokenSource()
        fusedLocationProviderClient
            .getCurrentLocation(PRIORITY_HIGH_ACCURACY, cancellationToken!!.token)
            .addOnCompleteListener {
                when {
                    it.isSuccessful -> {
                        _progressLiveData.postValue(false)
                        _locationLiveData.postValue(it.result)

                        sharedPreferences.edit().apply {
                            putFloat(MAP_LAT_KEY, it.result.latitude.toFloat())
                            putFloat(MAP_LON_KEY, it.result.longitude.toFloat())
                            apply()
                        }
                    }

                    else -> {
                        _progressLiveData.postValue(false)
                        _errorLiveData.postValue(NON_VALID)
                    }
                }

                isAlreadyLoading = false
            }

    }

    fun setToLastKnownLocationIfAny() {
        val lat = sharedPreferences.getFloat(MAP_LAT_KEY, MOSCOW_LAT)
        val lon = sharedPreferences.getFloat(MAP_LON_KEY, MOSCOW_LON)
        _historyLiveData.postValue(LatLng(lat.toDouble(), lon.toDouble()))
    }

    override fun onCleared() {
        super.onCleared()

        cancellationToken?.cancel()
        cancellationToken = null
    }

    val progressLiveData: LiveData<Boolean>
        get() = _progressLiveData
    val locationLiveData: LiveData<Location>
        get() = _locationLiveData
    val historyLiveData: LiveData<LatLng>
        get() = _historyLiveData
    val errorLiveData: LiveData<String>
        get() = _errorLiveData
}
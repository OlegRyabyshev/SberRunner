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

/**
 * ViewModel экрана "Дом" со списком из всех забегов
 *
 * @param fusedLocationProviderClient [FusedLocationProviderClient] - объект для отслеживания геопозиции
 * @param sharedPreferences [SharedPreferences] - получения объекта SharedPreferences для сохранения данных
 */
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


    /**
     * Метод обновления текущей геопозиции
     */
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

                        if (it.result != null) {
                            _locationLiveData.postValue(it.result)
                            saveLocation(it.result)
                        } else {
                            _errorLiveData.postValue(NON_VALID)
                        }
                    }
                }

                isAlreadyLoading = false
            }

    }

    /**
     * Сохранение последей геопозиции
     */
    private fun saveLocation(location: Location) {
        sharedPreferences.edit().apply {
            putFloat(MAP_LAT_KEY, location.latitude.toFloat())
            putFloat(MAP_LON_KEY, location.longitude.toFloat())
            apply()
        }
    }

    /**
     * Метод получения последней геопозиции (или получения дефолтных значений, если их нету)
     */
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
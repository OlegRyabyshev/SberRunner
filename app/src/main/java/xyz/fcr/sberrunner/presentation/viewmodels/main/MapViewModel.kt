package xyz.fcr.sberrunner.presentation.viewmodels.main

import android.annotation.SuppressLint
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import xyz.fcr.sberrunner.data.repository.shared.ISharedPreferenceWrapper
import xyz.fcr.sberrunner.presentation.viewmodels.SingleLiveEvent
import xyz.fcr.sberrunner.utils.Constants.NON_VALID
import javax.inject.Inject

/**
 * ViewModel экрана "Дом" со списком из всех забегов
 *
 * @param fusedLocationProviderClient [FusedLocationProviderClient] - объект для отслеживания геопозиции
 * @param sharedPreferenceWrapper [ISharedPreferenceWrapper] - интерфейс упрощенного взаимодействия с SharedPreference
 */
class MapViewModel @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val sharedPreferenceWrapper: ISharedPreferenceWrapper
) : ViewModel() {

    private val _progressLiveData = MutableLiveData<Boolean>()
    private val _locationLiveData = MutableLiveData<Location>()
    private val _historyLiveData = MutableLiveData<LatLng>()
    private val _errorLiveData = SingleLiveEvent<String>()

    private var cancellationToken: CancellationTokenSource? = null
    private var isAlreadyLoading = false

    /**
     * Обновление текущей геопозиции
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
        sharedPreferenceWrapper.saveMapLatitude(location.latitude.toFloat())
        sharedPreferenceWrapper.saveMapLongitude(location.longitude.toFloat())
    }

    /**
     * Метод получения последней геопозиции (или получения дефолтных значений, если их нету)
     */
    fun setToLastKnownLocationIfAny() {
        val lat = sharedPreferenceWrapper.getMapLatitude()
        val lon = sharedPreferenceWrapper.getMapLongitude()
        _historyLiveData.postValue(LatLng(lat.toDouble(), lon.toDouble()))
    }

    /**
     * Обнуление disposable
     */
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
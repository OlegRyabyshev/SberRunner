package xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import xyz.fcr.sberrunner.data.model.Run
import xyz.fcr.sberrunner.data.repository.db.IDatabaseRepository
import xyz.fcr.sberrunner.data.repository.shared.ISharedPreferenceWrapper
import javax.inject.Inject

class RunViewModel @Inject constructor(
    private val databaseRepository: IDatabaseRepository,
    private val sharedPreferenceWrapper: ISharedPreferenceWrapper
) : ViewModel() {

    private val _historyLiveData = MutableLiveData<LatLng>()

    fun insertRun(run: Run) {
        databaseRepository.addRun(run)
    }

    fun setToLastKnownLocationIfAny() {
        val lat = sharedPreferenceWrapper.getRunLatitude()
        val lon = sharedPreferenceWrapper.getRunLongitude()
        _historyLiveData.postValue(LatLng(lat.toDouble(), lon.toDouble()))
    }

    /**
     * Сохранение последей геопозиции
     */
    fun saveLastLocation(location: LatLng) {
        sharedPreferenceWrapper.saveRunLatitude(location.latitude.toFloat())
        sharedPreferenceWrapper.saveMapLongitude(location.longitude.toFloat())
    }

    val historyLiveData: LiveData<LatLng>
        get() = _historyLiveData
}
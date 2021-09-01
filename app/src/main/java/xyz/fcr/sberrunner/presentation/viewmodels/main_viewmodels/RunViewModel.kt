package xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import io.reactivex.rxjava3.disposables.Disposable
import xyz.fcr.sberrunner.data.model.Run
import xyz.fcr.sberrunner.data.repository.db.IDatabaseRepository
import xyz.fcr.sberrunner.data.repository.shared.ISharedPreferenceWrapper
import xyz.fcr.sberrunner.utils.ISchedulersProvider
import javax.inject.Inject

class RunViewModel @Inject constructor(
    private val databaseRepository: IDatabaseRepository,
    private val schedulersProvider: ISchedulersProvider,
    private val sharedPreferenceWrapper: ISharedPreferenceWrapper
) : ViewModel() {

    private val _historyLiveData = MutableLiveData<LatLng>()

    private var disposableAddRun: Disposable? = null

    fun insertRun(run: Run) {
        disposableAddRun = databaseRepository.addRun(run)
            .subscribeOn(schedulersProvider.io())
            .subscribe()
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

    override fun onCleared() {
        super.onCleared()

        disposableAddRun?.dispose()
        disposableAddRun = null
    }

    val historyLiveData: LiveData<LatLng>
        get() = _historyLiveData
}
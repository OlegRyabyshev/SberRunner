package xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import io.reactivex.rxjava3.disposables.Disposable
import xyz.fcr.sberrunner.data.model.Run
import xyz.fcr.sberrunner.domain.IDatabaseInteractor
import xyz.fcr.sberrunner.data.repository.shared.ISharedPreferenceWrapper
import xyz.fcr.sberrunner.utils.ISchedulersProvider
import javax.inject.Inject

/**
 * ViewModel экрана "Бег".
 *
 * @param databaseInteractor [IDatabaseInteractor] - интерфейс взаимодейтвия с базой данных
 * @param schedulersProvider [ISchedulersProvider] - провайдер объектов Scheduler
 * @param sharedPreferenceWrapper [ISharedPreferenceWrapper] - интерфейс упрощенного взаимодействия с SharedPreference
 */
class RunViewModel @Inject constructor(
    private val databaseInteractor: IDatabaseInteractor,
    private val schedulersProvider: ISchedulersProvider,
    private val sharedPreferenceWrapper: ISharedPreferenceWrapper
) : ViewModel() {

    private val _historyLiveData = MutableLiveData<LatLng>()
    private val _unitsLiveData = MutableLiveData<Boolean>()
    private val _weightLiveData = MutableLiveData<Int>()

    private var disposableAddRun: Disposable? = null

    /**
     * Отправка забега в базу данных
     */
    fun insertRun(run: Run) {
        disposableAddRun = databaseInteractor.addRun(run)
            .subscribeOn(schedulersProvider.io())
            .subscribe()
    }

    /**
     * Получение последних координат из SharedPreference
     */
    fun setToLastKnownLocationIfAny() {
        val lat = sharedPreferenceWrapper.getRunLatitude()
        val lon = sharedPreferenceWrapper.getRunLongitude()

        _historyLiveData.postValue(LatLng(lat.toDouble(), lon.toDouble()))
    }

    /**
     * Выставление системы измерений.
     */
    fun setUnits() {
        _unitsLiveData.postValue(sharedPreferenceWrapper.isMetric())
    }

    /**
     * Выставление веса пользователя.
     */
    fun setWeight() {
        _weightLiveData.postValue(sharedPreferenceWrapper.getIntWeight())
    }

    /**
     * Сохранение последей геопозиции
     */
    fun saveLastLocation(location: LatLng) {
        sharedPreferenceWrapper.saveRunLatitude(location.latitude.toFloat())
        sharedPreferenceWrapper.saveRunLongitude(location.longitude.toFloat())
    }

    /**
     * Обнуление disposable
     */
    override fun onCleared() {
        super.onCleared()

        disposableAddRun?.dispose()
        disposableAddRun = null
    }

    val historyLiveData: LiveData<LatLng>
        get() = _historyLiveData
    val unitsLiveData: LiveData<Boolean>
        get() = _unitsLiveData
    val weightLiveData: LiveData<Int>
        get() = _weightLiveData
}
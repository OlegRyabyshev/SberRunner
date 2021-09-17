package xyz.fcr.sberrunner.presentation.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.Disposable
import xyz.fcr.sberrunner.data.datastore.shared.ISharedPreferenceWrapper
import xyz.fcr.sberrunner.domain.interactor.db.IDatabaseInteractor
import xyz.fcr.sberrunner.presentation.model.Run
import xyz.fcr.sberrunner.utils.schedulers.ISchedulersProvider
import javax.inject.Inject

/**
 * ViewModel экрана детализированной инфомации о забеге.
 *
 * @param databaseInteractor [IDatabaseInteractor] - интерфейс взаимодейтвия с базой данных
 * @param sharedPreferenceWrapper [ISharedPreferenceWrapper] - интерфейс упрощенного взаимодействия с SharedPreference
 * @param schedulersProvider [ISchedulersProvider] - провайдер объектов Scheduler
 */
class DetailedRunViewModel @Inject constructor(
    private val databaseInteractor: IDatabaseInteractor,
    private val sharedPreferenceWrapper: ISharedPreferenceWrapper,
    private val schedulersProvider: ISchedulersProvider
) : ViewModel() {

    private val _runLiveData = MutableLiveData<Run>()
    private val _unitsLiveData = MutableLiveData<Boolean>()

    private var disposable: Disposable? = null

    /**
     * Обновление livedata с текущим забегом
     *
     * @param timestamp [Long] - временная отметка бега
     */
    fun getRunFromDB(timestamp: Long) {
        disposable = databaseInteractor.getRun(timestamp)
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribe { run ->
                _runLiveData.postValue(run)
            }
    }

    /**
     * Обновление livedata с текущей выбранной системой измерения
     */
    fun setUnits() {
        _unitsLiveData.postValue(sharedPreferenceWrapper.isMetric())
    }

    override fun onCleared() {
        super.onCleared()

        disposable?.dispose()
        disposable = null
    }

    val runLiveData: LiveData<Run>
        get() = _runLiveData
    val unitsLiveData: LiveData<Boolean>
        get() = _unitsLiveData
}
package xyz.fcr.sberrunner.presentation.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.fcr.sberrunner.data.model.RunEntity
import xyz.fcr.sberrunner.domain.interactor.db.IDatabaseInteractor
import xyz.fcr.sberrunner.data.repository.shared.ISharedPreferenceWrapper
import javax.inject.Inject

/**
 * ViewModel экрана детализированной инфомации о забеге.
 *
 * @param databaseInteractor [IDatabaseInteractor] - интерфейс взаимодейтвия с базой данных
 * @param sharedPreferenceWrapper [ISharedPreferenceWrapper] - интерфейс упрощенного взаимодействия с SharedPreference
 */
class DetailedRunViewModel @Inject constructor(
    private val databaseInteractor: IDatabaseInteractor,
    private val sharedPreferenceWrapper: ISharedPreferenceWrapper
) : ViewModel() {

    private lateinit var _runLiveData: LiveData<RunEntity>

    private val _unitsLiveData = MutableLiveData<Boolean>()

    /**
     * Обновление livedata с текущим забегом
     *
     * @param runId [Int] - ID забега
     */
    fun getRunFromDB(runId: Int) {
        _runLiveData = databaseInteractor.getRun(runId)
    }

    /**
     * Обновление livedata с текущей выбранной системой измерения
     */
    fun setUnits() {
        _unitsLiveData.postValue(sharedPreferenceWrapper.isMetric())
    }

    val runLiveData: LiveData<RunEntity>
        get() = _runLiveData

    val unitsLiveData: LiveData<Boolean>
        get() = _unitsLiveData
}
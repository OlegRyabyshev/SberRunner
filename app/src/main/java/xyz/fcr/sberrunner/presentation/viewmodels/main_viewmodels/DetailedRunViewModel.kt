package xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.fcr.sberrunner.data.model.Run
import xyz.fcr.sberrunner.domain.IDatabaseInteractor
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

    private lateinit var _runLiveData: LiveData<Run>

    private val _unitsLiveData = MutableLiveData<Boolean>()

    fun getRunFromDB(runId: Int) {
        _runLiveData = databaseInteractor.getRun(runId)
    }

    fun setUnits() {
        _unitsLiveData.postValue(sharedPreferenceWrapper.isMetric())
    }

    val runLiveData: LiveData<Run>
        get() = _runLiveData

    val unitsLiveData: LiveData<Boolean>
        get() = _unitsLiveData
}
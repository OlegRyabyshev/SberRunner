package xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.fcr.sberrunner.data.model.Run
import xyz.fcr.sberrunner.data.repository.db.IDatabaseRepository
import xyz.fcr.sberrunner.data.repository.shared.ISharedPreferenceWrapper
import javax.inject.Inject

class DetailedRunViewModel @Inject constructor(
    private val databaseRepository: IDatabaseRepository,
    private val sharedPreferenceWrapper: ISharedPreferenceWrapper
) : ViewModel() {

    private lateinit var _runLiveData: LiveData<Run>

    private val _unitsLiveData = MutableLiveData<Boolean>()

    fun getRunFromDB(runId: Int) {
        _runLiveData = databaseRepository.getRun(runId)
    }

    fun setUnits() {
        _unitsLiveData.postValue(sharedPreferenceWrapper.isMetric())
    }

    val runLiveData: LiveData<Run>
        get() = _runLiveData

    val unitsLiveData: LiveData<Boolean>
        get() = _unitsLiveData
}
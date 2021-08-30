package xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import xyz.fcr.sberrunner.data.model.Run
import xyz.fcr.sberrunner.data.repository.db.IDatabaseRepository
import javax.inject.Inject

class DetailedRunViewModel @Inject constructor(
    private val databaseRepository: IDatabaseRepository
) : ViewModel() {

    private lateinit var _runLiveData: LiveData<Run>

    fun getRunFromDB(runId: Int) {
        _runLiveData = databaseRepository.getRun(runId)
    }

    val runLiveData: LiveData<Run>
        get() = _runLiveData
}
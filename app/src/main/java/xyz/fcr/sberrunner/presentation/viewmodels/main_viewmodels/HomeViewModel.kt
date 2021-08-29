package xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.fcr.sberrunner.data.model.Run
import xyz.fcr.sberrunner.data.repository.db.IDatabaseRepository
import xyz.fcr.sberrunner.presentation.viewmodels.SingleLiveEvent
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val databaseRepository: IDatabaseRepository
) : ViewModel() {

    private val _progressLiveData = MutableLiveData<Boolean>()
    private val _listLiveData = MutableLiveData<List<Run>>()
    private val _errorLiveData = SingleLiveEvent<String>()

    val listOfRuns = databaseRepository.getAllRuns()

    fun syncWithCloud() {

    }

    fun addRun(run: Run) {
        databaseRepository.addRun(run)
    }

    fun deleteRun(run: Run) {
        databaseRepository.deleteRun(run)
    }

    val progressLiveData: LiveData<Boolean>
        get() = _progressLiveData
    val listLiveData: LiveData<List<Run>>
        get() = _listLiveData
    val errorLiveData: LiveData<String>
        get() = _errorLiveData
}
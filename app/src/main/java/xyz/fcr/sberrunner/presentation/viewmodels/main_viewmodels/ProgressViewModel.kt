package xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.fcr.sberrunner.data.model.Run
import xyz.fcr.sberrunner.data.repository.db.IDatabaseRepository
import xyz.fcr.sberrunner.presentation.model.Progress
import xyz.fcr.sberrunner.presentation.viewmodels.SingleLiveEvent
import javax.inject.Inject

class ProgressViewModel @Inject constructor(
    private val databaseRepository: IDatabaseRepository
) : ViewModel() {
    private val _progressLiveData = MutableLiveData<Boolean>()
    private val _errorLiveData = SingleLiveEvent<String>()
    private val _progressListLiveData = SingleLiveEvent<List<Progress>>()

    val listOfRuns = databaseRepository.getAllRuns()

    fun loadInfo() {
        val list = listOfRuns.value

//        val progress: Progress =
//
//        _progressListLiveData.postValue()
    }

    val progressLiveData: LiveData<Boolean>
        get() = _progressLiveData
    val errorLiveData: LiveData<String>
        get() = _errorLiveData
}
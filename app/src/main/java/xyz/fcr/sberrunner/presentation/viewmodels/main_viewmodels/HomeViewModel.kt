package xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.Disposable
import xyz.fcr.sberrunner.data.room.RunEntity
import xyz.fcr.sberrunner.presentation.viewmodels.SingleLiveEvent
import javax.inject.Inject

class HomeViewModel @Inject constructor() : ViewModel() {

    private val _progressLiveData = MutableLiveData<Boolean>()
    private val _successLiveData = MutableLiveData<List<RunEntity>?>()
    private val _errorLiveData = SingleLiveEvent<String>()

    private var disposable: Disposable? = null

    fun loadListOfRuns() {
        _successLiveData.postValue(null)
    }

    override fun onCleared() {
        super.onCleared()

        disposable?.dispose()
        disposable = null
    }

    val progressLiveData: LiveData<Boolean>
        get() = _progressLiveData
    val successLiveData: LiveData<List<RunEntity>?>
        get() = _successLiveData
    val errorLiveData: LiveData<String>
        get() = _errorLiveData
}
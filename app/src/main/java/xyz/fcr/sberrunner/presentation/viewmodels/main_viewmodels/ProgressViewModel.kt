package xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.Disposable
import xyz.fcr.sberrunner.data.model.Run
import xyz.fcr.sberrunner.domain.IDatabaseInteractor
import xyz.fcr.sberrunner.data.repository.shared.ISharedPreferenceWrapper
import xyz.fcr.sberrunner.utils.ISchedulersProvider
import javax.inject.Inject

class ProgressViewModel @Inject constructor(
    private val databaseInteractor: IDatabaseInteractor,
    private val sharedPreferenceWrapper: ISharedPreferenceWrapper,
    private val schedulersProvider: ISchedulersProvider
) : ViewModel() {

    private val _unitsLiveData = MutableLiveData<Boolean>()
    private val _listOfRunsLiveData = MutableLiveData<List<Run>>()

    private var disposableList: Disposable? = null

    fun updateListOfRuns() {
        disposableList = databaseInteractor.getAllRuns()
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribe { value -> _listOfRunsLiveData.postValue(value) }
    }

    fun setUnits() {
        _unitsLiveData.postValue(sharedPreferenceWrapper.isMetric())
    }

    override fun onCleared() {
        super.onCleared()

        disposableList?.dispose()
        disposableList = null
    }

    val unitsLiveData: LiveData<Boolean>
        get() = _unitsLiveData
    val listOfRunsLiveData: LiveData<List<Run>>
        get() = _listOfRunsLiveData
}
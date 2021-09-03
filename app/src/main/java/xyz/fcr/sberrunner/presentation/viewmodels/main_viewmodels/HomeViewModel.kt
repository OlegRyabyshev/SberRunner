package xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.Disposable
import xyz.fcr.sberrunner.data.model.Run
import xyz.fcr.sberrunner.domain.IDatabaseInteractor
import xyz.fcr.sberrunner.presentation.viewmodels.SingleLiveEvent
import xyz.fcr.sberrunner.utils.ISchedulersProvider
import javax.inject.Inject

/**
 * ViewModel экрана "Дом" со списком из всех забегов
 *
 * @param databaseInteractor [IDatabaseInteractor] - репозиторий базы данных забегов
 */
class HomeViewModel @Inject constructor(
    private val databaseInteractor: IDatabaseInteractor,
    private val schedulersProvider: ISchedulersProvider
) : ViewModel() {

    private val _progressLiveData = MutableLiveData<Boolean>()
    private val _errorLiveData = SingleLiveEvent<String>()
    private val _listOfRunsLiveData = MutableLiveData<List<Run>>()

    private var disposableList: Disposable? = null
    private var disposableSync: Disposable? = null
    private var disposableAddRun: Disposable? = null
    private var disposableDeleteRun: Disposable? = null

    fun updateListOfRuns() {
        disposableList = databaseInteractor.getAllRuns()
            .doOnSubscribe { _progressLiveData.postValue(true) }
            .doAfterTerminate { _progressLiveData.postValue(false) }
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribe(
                { value -> _listOfRunsLiveData.postValue(value) },
                { error -> _errorLiveData.postValue(error.message) }
            )
    }

    /**
     * Метод синхронизации данных БД с облачным БД FireStore
     */
    fun syncWithCloud() {
        disposableSync = databaseInteractor.syncWithCloud()
            .doOnSubscribe {
                _progressLiveData.postValue(true)
            }
            .doAfterTerminate {
                _progressLiveData.postValue(false)
            }
            .subscribeOn(schedulersProvider.io())
            .subscribe({
                updateListOfRuns()
            }, { e ->
                _errorLiveData.postValue(e.message)
            })
    }

    /**
     * Метод добавления забегов в БД
     *
     * @param run [Run] - один забег
     */
    fun addRun(run: Run) {
        disposableAddRun = databaseInteractor.addRun(run)
            .doOnSubscribe {
                _progressLiveData.postValue(true)
            }
            .doAfterTerminate {
                _progressLiveData.postValue(false)
            }
            .subscribeOn(schedulersProvider.io())
            .subscribe({
                updateListOfRuns()
            }, { e ->
                _errorLiveData.postValue(e.message)
            })
    }

    /**
     * Метод удааления забегов из БД
     *
     * @param run [Run] - один забег
     */
    fun deleteRun(run: Run) {
        disposableDeleteRun = databaseInteractor.deleteRun(run)
            .doOnSubscribe {
                _progressLiveData.postValue(true)
            }
            .doAfterTerminate {
                _progressLiveData.postValue(false)
            }
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribe()
    }

    override fun onCleared() {
        super.onCleared()

        disposableList?.dispose()
        disposableList = null

        disposableSync?.dispose()
        disposableSync = null

        disposableDeleteRun?.dispose()
        disposableDeleteRun = null

        disposableAddRun?.dispose()
        disposableAddRun = null
    }

    val progressLiveData: LiveData<Boolean>
        get() = _progressLiveData
    val errorLiveData: LiveData<String>
        get() = _errorLiveData
    val listOfRunsLiveData: LiveData<List<Run>>
        get() = _listOfRunsLiveData
}
package xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.Disposable
import xyz.fcr.sberrunner.data.model.Run
import xyz.fcr.sberrunner.data.repository.db.IDatabaseRepository
import xyz.fcr.sberrunner.presentation.viewmodels.SingleLiveEvent
import xyz.fcr.sberrunner.utils.ISchedulersProvider
import javax.inject.Inject

/**
 * ViewModel экрана "Дом" со списком из всех забегов
 *
 * @param databaseRepository [IDatabaseRepository] - репозиторий базы данных забегов
 */
class HomeViewModel @Inject constructor(
    private val databaseRepository: IDatabaseRepository,
    private val schedulersProvider: ISchedulersProvider
) : ViewModel() {

    private val _progressLiveData = MutableLiveData<Boolean>()
    private val _errorLiveData = SingleLiveEvent<String>()

    private var disposableAddRun: Disposable? = null
    private var disposableDeleteRun: Disposable? = null

    val listOfRuns = databaseRepository.getAllRuns()

    /**
     * Метод синхронизации данных БД с облачным БД FireStore
     */
    fun syncWithCloud() {

    }

    /**
     * Метод добавления забегов в БД
     *
     * @param run [Run] - один забег
     */
    fun addRun(run: Run) {
        disposableAddRun = databaseRepository.addRun(run)
            .doOnSubscribe {
                _progressLiveData.postValue(true)
            }
            .doAfterTerminate {
                _progressLiveData.postValue(false)
            }
            .subscribeOn(schedulersProvider.io())
            .subscribe()
    }

    /**
     * Метод удааления забегов из БД
     *
     * @param run [Run] - один забег
     */
    fun deleteRun(run: Run) {
        disposableDeleteRun = databaseRepository.deleteRun(run)
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

        disposableDeleteRun?.dispose()
        disposableDeleteRun = null

        disposableAddRun?.dispose()
        disposableAddRun = null
    }


    val progressLiveData: LiveData<Boolean>
        get() = _progressLiveData
    val errorLiveData: LiveData<String>
        get() = _errorLiveData
}
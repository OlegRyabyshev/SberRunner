package xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import xyz.fcr.sberrunner.data.model.Run
import xyz.fcr.sberrunner.data.repository.db.IDatabaseRepository
import xyz.fcr.sberrunner.presentation.viewmodels.SingleLiveEvent
import javax.inject.Inject

/**
 * ViewModel экрана "Дом" со списком из всех забегов
 *
 * @param databaseRepository [IDatabaseRepository] - репозиторий базы данных забегов
 */
class HomeViewModel @Inject constructor(
    private val databaseRepository: IDatabaseRepository
) : ViewModel() {

    private val _progressLiveData = MutableLiveData<Boolean>()
    private val _errorLiveData = SingleLiveEvent<String>()

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
        databaseRepository.addRun(run)
    }

    /**
     * Метод удааления забегов из БД
     *
     * @param run [Run] - один забег
     */
    fun deleteRun(run: Run) {
        databaseRepository.deleteRun(run)
    }

    val progressLiveData: LiveData<Boolean>
        get() = _progressLiveData
    val errorLiveData: LiveData<String>
        get() = _errorLiveData
}
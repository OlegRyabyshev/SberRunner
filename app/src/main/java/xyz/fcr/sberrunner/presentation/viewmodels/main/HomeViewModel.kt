package xyz.fcr.sberrunner.presentation.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import xyz.fcr.sberrunner.data.model.RunEntity
import xyz.fcr.sberrunner.domain.db.IDatabaseInteractor
import xyz.fcr.sberrunner.domain.firebase.IFirebaseInteractor
import xyz.fcr.sberrunner.presentation.viewmodels.SingleLiveEvent
import xyz.fcr.sberrunner.utils.ISchedulersProvider
import javax.inject.Inject

/**
 * ViewModel экрана "Дом" со списком из всех забегов
 *
 * @param databaseInteractor [IDatabaseInteractor] - интерфейс взаимодейтвия с базой данных
 * @param firebaseInteractor [IFirebaseInteractor] - интерфейс взаимодействия с firebase
 * @param schedulersProvider [ISchedulersProvider] - провайдер объектов Scheduler
 */
class HomeViewModel @Inject constructor(
    private val databaseInteractor: IDatabaseInteractor,
    private val firebaseInteractor: IFirebaseInteractor,
    private val schedulersProvider: ISchedulersProvider
) : ViewModel() {

    private var unitedList: List<RunEntity>? = null
    private val _progressLiveData = MutableLiveData<Boolean>()
    private val _errorLiveData = SingleLiveEvent<String>()
    private val _listOfRunsLiveData = MutableLiveData<List<RunEntity>>()

    private var compositeDisposable = CompositeDisposable()

    /**
     * Метод обновления списков забега
     */
    fun updateListOfRuns() {
        compositeDisposable.add(databaseInteractor.getAllRuns()
            .doOnSubscribe { _progressLiveData.postValue(true) }
            .doAfterTerminate { _progressLiveData.postValue(false) }
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribe(
                { value -> _listOfRunsLiveData.postValue(value) },
                { error -> _errorLiveData.postValue(error.message) }
            )
        )
    }

    /**
     * Метод синхронизации данных БД с облачным БД FireStore
     */
    fun initDbAndFirestoreSync() {
        compositeDisposable.add(firebaseInteractor.getAllRunsFromCloud()
            .doOnSubscribe {
                _progressLiveData.postValue(true)
            }
            .subscribeOn(schedulersProvider.io())
            .subscribe { task ->
                task.addOnCompleteListener {
                    when {
                        task.isSuccessful -> {
                            val listOfRuns = mutableListOf<RunEntity>()

                            task.result.forEach { document ->
                                listOfRuns.add(
                                    RunEntity(
                                        distanceInMeters = document.get("distanceInMeters") as Int,
                                        timestamp = document.get("timestamp") as Long,
                                        timeInMillis = document.get("timeInMillis") as Long,
                                        avgSpeedInKMH = document.get("avgSpeedInKMH") as Float,
                                        calories = document.get("calories") as Int
                                    )
                                )
                            }

                            getAllRunsFromDB(listOfRuns)
                        }

                        else -> {
                            _errorLiveData.postValue("Error in syncWithCloud")
                            _progressLiveData.postValue(false)
                        }
                    }
                }
            }
        )
    }

    private fun getAllRunsFromDB(listOfRuns: List<RunEntity>) {
        compositeDisposable.add(
            databaseInteractor.getAllRuns()
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe({ value ->
                    unitedList = getUnitedList(value, listOfRuns)
                    clearExistingListInCloud()
                }, {
                    _errorLiveData.postValue("Error in getListFromDB")
                    _progressLiveData.postValue(false)
                })
        )
    }

    private fun clearExistingListInCloud() {
        compositeDisposable.add(firebaseInteractor.removeAllRunsFromCloud()
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribe { task ->
                task.addOnCompleteListener {
                    when {
                        task.isSuccessful -> loadNewListToCloud()

                        else -> {
                            _errorLiveData.postValue("Error in loadDataToCloud")
                            _progressLiveData.postValue(false)
                        }
                    }
                }
            }
        )
    }

    private fun loadNewListToCloud() {
        compositeDisposable.add(firebaseInteractor.loadNewList(unitedList!!)
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribe { task ->
                task.addOnCompleteListener {
                    when {
                        task.isSuccessful -> {
                            clearExistingListInDB()
                        }
                        else -> {
                            _errorLiveData.postValue("Error in loadNewListToCloud")
                            _progressLiveData.postValue(false)
                        }
                    }
                }
            }
        )
    }

    private fun clearExistingListInDB() {
        compositeDisposable.add(
            databaseInteractor.clearRuns()
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe({
                    loadNewListToDB()
                }, {
                    _errorLiveData.postValue("Error in clearExistingListInDB")
                    _progressLiveData.postValue(false)
                })
        )
    }

    private fun loadNewListToDB() {
        if (unitedList != null) {
            compositeDisposable.add(
                databaseInteractor.addList(unitedList!!)
                    .subscribeOn(schedulersProvider.io())
                    .observeOn(schedulersProvider.ui())
                    .subscribe({
                        updateListOfRuns()
                    }, {
                        _errorLiveData.postValue("Error in loadNewListToDB")
                        _progressLiveData.postValue(false)
                    })
            )
        }
    }

    private fun getUnitedList(dbList: List<RunEntity>, firestoreList: List<RunEntity>): List<RunEntity> {
        val unitedList = mutableListOf<RunEntity>()

        unitedList.addAll(firestoreList)

        dbList.forEach { dbEntity ->
            if (dbEntity.toDeleteFlag) {
                unitedList.forEach {
                    if (it.timestamp == dbEntity.timestamp) {
                        unitedList.remove(it)
                    }
                }
            }
        }

        dbList.forEach { dbEntity ->
            if (!dbEntity.toDeleteFlag) {
                if (notContainsTimeStamp(unitedList, dbEntity.timestamp)) {
                    unitedList.add(dbEntity)
                }
            }
        }

        return unitedList
    }

    private fun notContainsTimeStamp(unitedList: List<RunEntity>, timestamp: Long): Boolean {
        unitedList.forEach { run ->
            if (run.timestamp == timestamp) return false
        }

        return true
    }

    /**
     * Метод удааления забегов из БД
     *
     * @param run [RunEntity] - один забег
     */
    fun setFlag(runID: Int, toDelete: Boolean) {
        compositeDisposable.add(databaseInteractor.switchToDeleteFlag(runID, toDelete)
            .doOnSubscribe {
                _progressLiveData.postValue(true)
            }
            .doAfterTerminate {
                _progressLiveData.postValue(false)
            }
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribe()
        )
    }

    /**
     * Обнуление disposable
     */
    override fun onCleared() {
        super.onCleared()

        compositeDisposable.clear()
        compositeDisposable.dispose()
    }

    val progressLiveData: LiveData<Boolean>
        get() = _progressLiveData
    val errorLiveData: LiveData<String>
        get() = _errorLiveData
    val listOfRunsLiveData: LiveData<List<RunEntity>>
        get() = _listOfRunsLiveData
}
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

    private val _progressLiveData = MutableLiveData<Boolean>()
    private val _errorLiveData = SingleLiveEvent<String>()
    private val _listOfRunsLiveData = MutableLiveData<List<RunEntity>>()

    private var compositeDisposable = CompositeDisposable()

    private var cloudListRuns: List<RunEntity> = emptyList()
    private var dbListRuns: List<RunEntity> = emptyList()

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
    // 1
    fun getAllRunsFromCloud() {
        compositeDisposable.add(firebaseInteractor.getAllRunsFromCloud()
            .doOnSubscribe {
                _progressLiveData.postValue(true)
            }
            .subscribeOn(schedulersProvider.io())
            .subscribe { task ->
                task.addOnCompleteListener {
                    when {
                        task.isSuccessful -> {
                            task.result.forEach { document ->

                                val list = mutableListOf<RunEntity>()

                                list.add(
                                    RunEntity(
                                        avgSpeedInKMH = document.get("avgSpeedInKMH") as String,
                                        calories = document.get("calories") as Long,
                                        distanceInMeters = document.get("distanceInMeters") as Long,
                                        timeInMillis = document.get("timeInMillis") as Long,
                                        timestamp = document.get("timestamp") as Long,
                                        toDeleteFlag = false
                                    )
                                )

                                cloudListRuns = list
                            }

                            getAllRunsFromDB()
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

    // 2
    private fun getAllRunsFromDB() {
        compositeDisposable.add(
            databaseInteractor.getAllRuns()
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe({ list ->
                    dbListRuns = list
                    getMissingRunsFromCloudToDb()
                }, {
                    _errorLiveData.postValue("Error in getListFromDB")
                    _progressLiveData.postValue(false)
                })
        )
    }

    // 3
    private fun getMissingRunsFromCloudToDb() {
        compositeDisposable.add(
            firebaseInteractor.getMissingRunsFromCloudToDb(dbListRuns)
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe({ missingRunsFromCloud ->
                    uploadMissingRunsFromCloudToDb(missingRunsFromCloud)
                }, {
                    _errorLiveData.postValue("Error in getListFromDB")
                    _progressLiveData.postValue(false)
                })
        )
    }

    // 3.1
    private fun uploadMissingRunsFromCloudToDb(missingRunsFromCloud: List<RunEntity>) {
        compositeDisposable.add(
            databaseInteractor.addList(missingRunsFromCloud)
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe({
                    setDeleteFlagsInCloud()
                }, {
                    _errorLiveData.postValue("Error in clearExistingListInDB")
                    _progressLiveData.postValue(false)
                })
        )
    }

    // 4
    private fun setDeleteFlagsInCloud() {
        compositeDisposable.add(
            firebaseInteractor.setDeleteFlagsInCloud(dbListRuns.filter { it.toDeleteFlag })
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe({
                    removeMarkedToDeleteFromDb()
                }, {
                    _errorLiveData.postValue("Error in getListFromDB")
                    _progressLiveData.postValue(false)
                })
        )
    }
    // 4.1
    private fun removeMarkedToDeleteFromDb() {
        compositeDisposable.add(
            databaseInteractor.removeMarkedToDelete()
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe({
                    getMarkedToDeleteFromCloud()
                }, {
                    _errorLiveData.postValue("Error in getListFromDB")
                    _progressLiveData.postValue(false)
                })
        )
    }

    // 5
    private fun getMarkedToDeleteFromCloud() {
        compositeDisposable.add(
            firebaseInteractor.getMarkedToDeleteFromCloud()
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe({ markedToDeleteFromCloud ->
                    removeFromDb(markedToDeleteFromCloud)
                }, {
                    _errorLiveData.postValue("Error in getListFromDB")
                    _progressLiveData.postValue(false)
                })
        )
    }
    // 5.1
    private fun removeFromDb(markedToDeleteFromCloud: List<RunEntity>) {
        compositeDisposable.add(
            databaseInteractor.deleteRuns(markedToDeleteFromCloud)
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe({
                    uploadMissingRunsFromDbToCloud()
                }, {
                    _errorLiveData.postValue("Error in getListFromDB")
                    _progressLiveData.postValue(false)
                })
        )
    }

    private fun uploadMissingRunsFromDbToCloud() {
        compositeDisposable.add(
            firebaseInteractor.uploadMissingFromDbToCloud()
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe({
                    _progressLiveData.postValue(true)
                }, {
                    _errorLiveData.postValue("Error in getListFromDB")
                    _progressLiveData.postValue(false)
                })
        )
    }

//    private fun notContainsTimeStamp(unitedList: List<RunEntity>, timestamp: Long): Boolean {
//        unitedList.forEach { run ->
//            if (run.timestamp == timestamp) return false
//        }
//
//        return true
//    }

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
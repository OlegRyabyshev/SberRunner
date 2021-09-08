package xyz.fcr.sberrunner.presentation.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import xyz.fcr.sberrunner.data.model.RunEntity
import xyz.fcr.sberrunner.domain.db.IDatabaseInteractor
import xyz.fcr.sberrunner.domain.firebase.IFirebaseInteractor
import xyz.fcr.sberrunner.presentation.viewmodels.SingleLiveEvent
import xyz.fcr.sberrunner.utils.schedulers.ISchedulersProvider
import xyz.fcr.sberrunner.utils.toBitmap
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
        compositeDisposable.add(
            databaseInteractor.getAllRuns()
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe({ value ->
                    _listOfRunsLiveData.postValue(value)
                }, { error ->
                    _errorLiveData.postValue(error.message)
                })
        )
    }

    // 1
    fun initSync() {
        compositeDisposable.add(
            databaseInteractor.getAllRuns()
                .doOnSubscribe { _progressLiveData.postValue(true) }
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe({ list ->
                    dbListRuns = list
                    getAllRunsFromCloud()
                }, {
                    _errorLiveData.postValue("Error in initSync")
                    finishSync()
                })
        )
    }

    /**
     * Метод синхронизации данных БД с облачным БД FireStore
     */
    // 2
    private fun getAllRunsFromCloud() {
        compositeDisposable.add(firebaseInteractor.getAllRunsFromCloud()
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribe { task ->
                task.addOnCompleteListener {
                    when {
                        task.isSuccessful -> {
                            val list = mutableListOf<RunEntity>()

                            if (!task.result.isEmpty) {
                                task.result.forEach { document ->
                                    list.add(
                                        RunEntity(
                                            avgSpeedInKMH = document.get("avgSpeedInKMH") as String,
                                            calories = document.get("calories") as Long,
                                            distanceInMeters = document.get("distanceInMeters") as Long,
                                            timeInMillis = document.get("timeInMillis") as Long,
                                            timestamp = document.get("timestamp") as Long,
                                            toDeleteFlag = document.get("toDeleteFlag") as Boolean
                                        )
                                    )
                                }
                            }

                            cloudListRuns = list
                            val missingList = getMissingRunsFromCloudToDb()

                            if (missingList.isEmpty()) {
                                switchToDeleteFlagsInCloud()
                            } else {
                                loadImagesLoList(missingList)
                            }
                        }

                        else -> {
                            _errorLiveData.postValue("Error in getAllRunsFromCloud")
                            finishSync()
                        }
                    }
                }
            }
        )
    }

    private fun loadImagesLoList(missingList: List<RunEntity>) {
        var counter = 0

        missingList.forEach { run ->
            compositeDisposable.add(
                firebaseInteractor.downloadImageFromStorage(run)
                    .subscribeOn(schedulersProvider.io())
                    .observeOn(schedulersProvider.ui())
                    .subscribe({ task ->
                        task.addOnCompleteListener {
                            when {
                                task.isSuccessful -> {
                                    counter++

                                    run.mapImage = task.result.toBitmap()

                                    if (counter == missingList.size) {
                                        uploadMissingRunsFromCloudToDb(missingList)
                                    }
                                }
                                else -> {
                                    _errorLiveData.postValue("Error in loadImagesLoList")
                                    finishSync()
                                }
                            }
                        }
                    }, {
                        _errorLiveData.postValue("Error in loadImagesLoList")
                        finishSync()
                    })
            )
        }
    }

    // 3.1
    private fun uploadMissingRunsFromCloudToDb(missingRunsFromCloud: List<RunEntity>) {
        compositeDisposable.add(
            databaseInteractor.addList(missingRunsFromCloud.filter { !it.toDeleteFlag })
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe({
                    switchToDeleteFlagsInCloud()
                }, {
                    _errorLiveData.postValue("Error in uploadMissingRunsFromCloudToDb")
                    finishSync()
                })
        )
    }

    // 4
    private fun switchToDeleteFlagsInCloud() {
        val listToSwitch: List<RunEntity> = getListToSwitch(dbListRuns.filter { it.toDeleteFlag }, cloudListRuns)

        if (listToSwitch.isNotEmpty()) {
            compositeDisposable.add(
                firebaseInteractor.switchToDeleteFlagsInCloud(listToSwitch)
                    .subscribeOn(schedulersProvider.io())
                    .observeOn(schedulersProvider.ui())
                    .subscribe({
                        removeMarkedToDeleteFromDb()
                    }, {
                        _errorLiveData.postValue("Error in switchToDeleteFlagsInCloud")
                        finishSync()
                    })
            )
        } else {
            removeMarkedToDeleteFromDb()
        }
    }

    // 4.1 Удаляет все entity в DB, помеченные на удаление
    private fun removeMarkedToDeleteFromDb() {
        compositeDisposable.add(
            databaseInteractor.removeMarkedToDelete()
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe({
                    val listToRemove = cloudListRuns.filter { it.toDeleteFlag }

                    if (listToRemove.isNotEmpty()) {
                        removeFromDb(listToRemove)
                    } else {
                        getUpdatedListFromDb()
                    }
                }, {
                    _errorLiveData.postValue("Error in removeMarkedToDeleteFromDb")
                    finishSync()
                })
        )
    }

    // 5.1 Удаляет все entity в DB, помеченные на удаление из firestore
    private fun removeFromDb(markedToDeleteFromCloud: List<RunEntity>) {
        compositeDisposable.add(
            databaseInteractor.removeRuns(markedToDeleteFromCloud)
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe({
                    getUpdatedListFromDb()
                }, {
                    _errorLiveData.postValue("Error in removeFromDb")
                    finishSync()
                })
        )
    }

    // 6
    private fun getUpdatedListFromDb() {
        compositeDisposable.add(
            databaseInteractor.getAllRuns()
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe({ list ->
                    dbListRuns = list
                    val missingList = getMissingRunsFromDbToCloud()

                    if (missingList.isNotEmpty()) {
                        loadPicturesFromDbToStorage(missingList)
                    } else {
                        finishSync()
                    }
                }, {
                    _errorLiveData.postValue("Error in getUpdatedListFromDb")
                    finishSync()
                })
        )
    }

    private fun loadPicturesFromDbToStorage(missingList: List<RunEntity>) {
        var counter = 0

        missingList.forEach { run ->
            compositeDisposable.add(
                firebaseInteractor.uploadImageToStorage(run)
                    .subscribeOn(schedulersProvider.io())
                    .observeOn(schedulersProvider.ui())
                    .subscribe({ task ->
                        task.addOnCompleteListener {
                            when {
                                it.isSuccessful -> {
                                    counter++
                                    if (counter == missingList.size) {
                                        uploadMissingRunsFromDbToCloud(missingList)
                                    }
                                }

                                else -> {
                                    _errorLiveData.postValue("Error in loadPicturesFromDbToStorage")
                                    _progressLiveData.postValue(false)
                                }
                            }
                        }
                    }, {
                        _errorLiveData.postValue("Error in loadPicturesFromDbToStorage")
                        _progressLiveData.postValue(false)
                    })
            )
        }
    }

    // 6.1
    private fun uploadMissingRunsFromDbToCloud(missingList: List<RunEntity>) {
        compositeDisposable.add(
            firebaseInteractor.uploadMissingFromDbToCloud(missingList)
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe({
                    _progressLiveData.postValue(false)
                    updateListOfRuns()
                }, {
                    _errorLiveData.postValue("Error in uploadMissingRunsFromDbToCloud")
                    _progressLiveData.postValue(false)
                })
        )
    }

    private fun finishSync() {
        _progressLiveData.postValue(false)
        updateListOfRuns()
    }

    private fun containsTimeStamp(unitedList: List<RunEntity>, timestamp: Long): Boolean {
        unitedList.forEach { run ->
            if (run.timestamp == timestamp) return true
        }

        return false
    }

    /**
     * Метод удааления забегов из БД
     *
     * @param runID [RunEntity] - один забег
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
            .subscribe({
                updateListOfRuns()
            }, {})


        )
    }

    private fun getMissingRunsFromCloudToDb(): List<RunEntity> {
        val missingRunsFromCloud: MutableList<RunEntity> = mutableListOf()

        cloudListRuns.forEach {
            if (!it.toDeleteFlag && !containsTimeStamp(dbListRuns, it.timestamp)) {
                missingRunsFromCloud.add(it)
            }
        }

        return missingRunsFromCloud
    }

    private fun getMissingRunsFromDbToCloud(): List<RunEntity> {
        val missingRunsFromDb: MutableList<RunEntity> = mutableListOf()

        dbListRuns.forEach {
            if (!containsTimeStamp(cloudListRuns, it.timestamp)) {
                missingRunsFromDb.add(it)
            }
        }

        return missingRunsFromDb
    }

    private fun getListToSwitch(dbSwitchedRuns: List<RunEntity>, cloudListRuns: List<RunEntity>): List<RunEntity> {
        val listToSwitch: MutableList<RunEntity> = mutableListOf()

        dbSwitchedRuns.forEach {
            if (containsTimeStamp(cloudListRuns, it.timestamp)) {
                listToSwitch.add(it)
            }
        }

        return listToSwitch
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

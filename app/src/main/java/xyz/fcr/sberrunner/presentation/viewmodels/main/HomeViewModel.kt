package xyz.fcr.sberrunner.presentation.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import xyz.fcr.sberrunner.domain.interactor.db.IDatabaseInteractor
import xyz.fcr.sberrunner.domain.interactor.firebase.IFirebaseInteractor
import xyz.fcr.sberrunner.presentation.model.Run
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
    private val _listOfRunsLiveData = MutableLiveData<List<Run>>()

    private var compositeDisposable = CompositeDisposable()

    private var cloudListRuns: List<Run> = emptyList()
    private var dbListRuns: List<Run> = emptyList()

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

    /**
     * Метод инициализации процесса синхронизации.
     * Получение локального списка забегов из БД для последующего сравнения.
     */
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
     * Получение списка забегов из Firestore.
     */
    private fun getAllRunsFromCloud() {
        compositeDisposable.add(firebaseInteractor.getAllRunsFromCloud()
            .subscribeOn(schedulersProvider.io())
            .observeOn(schedulersProvider.ui())
            .subscribe { task ->
                task.addOnCompleteListener {
                    when {
                        task.isSuccessful -> {
                            val list = mutableListOf<Run>()

                            if (!task.result.isEmpty) {
                                task.result.forEach { document ->
                                    list.add(
                                        Run(
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
                            _errorLiveData.postValue("No access to internet")
                            finishSync()
                        }
                    }
                }
            }
        )
    }

    /**
     * Загрузка изображений из локальной БД на Firebase Storage
     * @param missingList [List] - список забегов
     */
    private fun loadImagesLoList(missingList: List<Run>) {
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

    /**
     * Загрузка недостоющих забегов из Firestore в локальную БД
     * @param missingRunsFromCloud [List] - список забегов
     */
    private fun uploadMissingRunsFromCloudToDb(missingRunsFromCloud: List<Run>) {
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

    /**
     * Переключение флагов на удаление в облаке
     * Это позволит при последующей синхронизации других устройств удалить эти забеги и там
     */
    private fun switchToDeleteFlagsInCloud() {
        val listToSwitch: List<Run> = getListToSwitch(dbListRuns.filter { it.toDeleteFlag }, cloudListRuns)

        if (listToSwitch.isNotEmpty()) {
            compositeDisposable.add(
                firebaseInteractor.switchToDeleteFlagsInCloud(listToSwitch)
                    .subscribeOn(schedulersProvider.io())
                    .observeOn(schedulersProvider.ui())
                    .subscribe({ task ->
                        task.addOnCompleteListener {
                            when {
                                task.isSuccessful -> {
                                    removeMarkedToDeleteFromDb()
                                }
                                else -> {
                                    _errorLiveData.postValue("Error in switchToDeleteFlagsInCloud")
                                    finishSync()
                                }
                            }
                        }
                    }, {
                        _errorLiveData.postValue("Error in switchToDeleteFlagsInCloud")
                        finishSync()
                    })
            )
        } else {
            removeMarkedToDeleteFromDb()
        }
    }

    /**
     * Удаление всех забегов из локальной БД, отмеченных флагом на удаление
     */
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

    /**
     * Удаление всех забегов из локальной БД, отмеченных на сервере флагом на удаление
     *
     * @param markedToDeleteFromCloud [List] - список забегов
     */
    private fun removeFromDb(markedToDeleteFromCloud: List<Run>) {
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

    /**
     * Получение обновленного списка из локальной БД
     */
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

    /**
     * Отправка изображений недостоющих забегов в Firebase Storage.
     *
     * @param missingList [List] - список забегов
     */
    private fun loadPicturesFromDbToStorage(missingList: List<Run>) {
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

    /**
     * Отправка изображений недостоющих забегов в Firebase Storage.
     *
     * @param missingList [List] - список забегов
     */
    private fun uploadMissingRunsFromDbToCloud(missingList: List<Run>) {
        compositeDisposable.add(
            firebaseInteractor.uploadMissingFromDbToCloud(missingList)
                .subscribeOn(schedulersProvider.io())
                .observeOn(schedulersProvider.ui())
                .subscribe({ task ->
                    task.addOnCompleteListener {
                        when {
                            task.isSuccessful -> {
                                finishSync()
                            }
                            else -> {
                                _errorLiveData.postValue("Error in uploadMissingRunsFromDbToCloud")
                                _progressLiveData.postValue(false)
                            }
                        }
                    }
                }, {
                    _errorLiveData.postValue("Error in uploadMissingRunsFromDbToCloud")
                    _progressLiveData.postValue(false)
                })
        )
    }

    /**
     * Завершение синхронизации
     */
    private fun finishSync() {
        _progressLiveData.postValue(false)
        updateListOfRuns()
    }

    /**
     * Проверка содержит ли список забег с выбранной временной отметкой
     *
     * @param list [List] - список забегов
     * @param timestamp [Long] - временная отметка
     *
     * @return [Boolean] - отметка содержится (true)/ не содержится (false)
     */
    private fun containsTimeStamp(list: List<Run>, timestamp: Long): Boolean {
        list.forEach { run ->
            if (run.timestamp == timestamp) return true
        }

        return false
    }

    /**
     * Метод переключения флага на удаление в БД
     *
     * @param timestamp [Long] - временная отмета забега
     * @param toDelete [Boolean] - флаг на удаление
     */
    fun setFlag(timestamp: Long, toDelete: Boolean) {
        compositeDisposable.add(
            databaseInteractor.switchToDeleteFlag(timestamp, toDelete)
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

    /**
     * Получение списка забегов, имеющихся в Firestore, но отсутствующих в локальной БД
     *
     * @return [List] - список забегов
     */
    private fun getMissingRunsFromCloudToDb(): List<Run> {
        val missingRunsFromCloud: MutableList<Run> = mutableListOf()

        cloudListRuns.forEach {
            if (!it.toDeleteFlag && !containsTimeStamp(dbListRuns, it.timestamp)) {
                missingRunsFromCloud.add(it)
            }
        }

        return missingRunsFromCloud
    }

    /**
     * Получение списка забегов, имеющихся в локальной БД, но отсутствующих в Firestore
     *
     * @return [List] - список забегов
     */
    private fun getMissingRunsFromDbToCloud(): List<Run> {
        val missingRunsFromDb: MutableList<Run> = mutableListOf()

        dbListRuns.forEach {
            if (!containsTimeStamp(cloudListRuns, it.timestamp)) {
                missingRunsFromDb.add(it)
            }
        }

        return missingRunsFromDb
    }

    /**
     * Получение списка забегов на переключение флагов на удаление
     *
     * @param dbSwitchedRuns [List] - список забегов из локальной БД
     * @param cloudListRuns [List] - список забегов из Firestore
     *
     * @return [List] - список забегов
     */
    private fun getListToSwitch(dbSwitchedRuns: List<Run>, cloudListRuns: List<Run>): List<Run> {
        val listToSwitch: MutableList<Run> = mutableListOf()

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
    val listOfRunsLiveData: LiveData<List<Run>>
        get() = _listOfRunsLiveData
}

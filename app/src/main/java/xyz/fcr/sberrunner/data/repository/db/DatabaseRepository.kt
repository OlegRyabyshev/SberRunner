package xyz.fcr.sberrunner.data.repository.db

import androidx.lifecycle.LiveData
import io.reactivex.rxjava3.core.Single
import xyz.fcr.sberrunner.data.model.Run
import xyz.fcr.sberrunner.data.room.RunDao
import xyz.fcr.sberrunner.utils.ISchedulersProvider
import javax.inject.Inject

/**
 * Имплементация интерфейса [IDatabaseRepository], служит для связи room dao <-> view model
 *
 * @param runDao [RunDao] - data access objects для получения доступа к базе данных бега
 * @param schedulersProvider [ISchedulersProvider] - провайдер объектов Scheduler
 */
class DatabaseRepository @Inject constructor(
    private val runDao: RunDao,
    private val schedulersProvider: ISchedulersProvider
) : IDatabaseRepository {

    /**
     * Метод добавления объкта бега в БД
     *
     * @param run [Run] - объект бега на добавление
     */
    override fun addRun(run: Run) {
        Single.fromCallable { runDao.addRun(run) }
            .subscribeOn(schedulersProvider.io())
            .subscribe()
    }

    /**
     * Метод удаления объкта бега из БД
     *
     * @param run [Run] - объект бега на удаление
     */
    override fun deleteRun(run: Run) {
        Single.fromCallable { runDao.deleteRun(run) }
            .subscribeOn(schedulersProvider.io())
            .subscribe()
    }

    /**
     * Метод добавления объкта бега в БД
     *
     * @return - LiveData лист из забегов
     */
    override fun getAllRuns(): LiveData<List<Run>> {
        return runDao.getAllRuns()
    }
}
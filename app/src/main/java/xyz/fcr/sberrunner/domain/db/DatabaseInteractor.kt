package xyz.fcr.sberrunner.domain.db

import androidx.lifecycle.LiveData
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import xyz.fcr.sberrunner.data.model.RunEntity
import xyz.fcr.sberrunner.data.room.RunDao
import javax.inject.Inject

/**
 * Имплементация интерфейса [IDatabaseInteractor], служит для связи room dao <-> view model
 *
 * @param runDao [RunDao] - data access objects для получения доступа к базе данных бега
 */
class DatabaseInteractor @Inject constructor(
    private val runDao: RunDao
) : IDatabaseInteractor {

    /**
     * Метод добавления объкта бега в БД
     *
     * @param run [RunEntity] - объект бега на добавление
     */
    override fun addRun(run: RunEntity): Completable {
        return Completable.fromCallable { runDao.addRun(run) }
    }

    /**
     * Метод удаления объекта бега из БД
     *
     * @param run [RunEntity] - объект бега на удаление
     */
    override fun deleteRun(run: RunEntity): Completable {
        return Completable.fromCallable { runDao.deleteRun(run) }
    }

    /**
     * Метод получения объектов бега в из БД
     *
     * @return [Single<List<Run>>] - объекты бега
     */
    override fun getAllRuns(): Single<List<RunEntity>> {
        return Single.fromCallable { runDao.getAllRuns() }
    }

    /**
     * Метод получения объкта бега из БД по ID
     *
     * @return - LiveData объект забега
     */
    override fun getRun(runId: Int): LiveData<RunEntity> {
        return runDao.getRun(runId)
    }

    override fun clearRuns(): Single<Unit> {
        return Single.fromCallable { runDao.clearRuns() }
    }

    override fun addList(unitedList: List<RunEntity>): Single<Unit> {
        return Single.fromCallable {
            unitedList.forEach {
                addRun(it)
            }
        }
    }

    override fun switchToDeleteFlag(runID: Int, toDelete: Boolean): Single<Unit> {
        return Single.fromCallable { runDao.switchToDeleteFlag(runID, toDelete) }
    }
}
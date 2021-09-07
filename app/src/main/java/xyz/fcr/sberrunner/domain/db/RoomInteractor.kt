package xyz.fcr.sberrunner.domain.db

import androidx.lifecycle.LiveData
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import xyz.fcr.sberrunner.data.model.RunEntity
import xyz.fcr.sberrunner.data.room.RunDao
import javax.inject.Inject

/**
 * Имплементация интерфейса [IDatabaseInteractor], служит для связи Room <-> ViewModel
 *
 * @param runDao [RunDao] - data access objects для получения доступа к базе данных бега
 */
class RoomInteractor @Inject constructor(
    private val runDao: RunDao
) : IDatabaseInteractor {

    /**
     * Метод добавления объкта бега в БД
     *
     * @param run [RunEntity] - объект бега на добавление
     *
     * @return [Completable] - результат выполнения добавления
     */
    override fun addRun(run: RunEntity): Completable {
        return Completable.fromCallable { runDao.addRun(run) }
    }

    /**
     * Метод удаления объекта бега из БД
     *
     * @param run [RunEntity] - объект бега на удаление
     *
     * @return [Completable] - результат выполнения удаления
     */
    override fun deleteRun(run: RunEntity): Completable {
        return Completable.fromCallable { runDao.deleteRun(run) }
    }

    /**
     * Метод получения объектов бега из БД
     *
     * @return [Single] - результат получения списка забегов
     */
    override fun getAllRuns(): Single<List<RunEntity>> {
        return Single.fromCallable { runDao.getAllRuns() }
    }

    /**
     * Метод получения объкта бега из БД по ID
     *
     * @param runId [Int] - ID забега
     *
     * @return [LiveData] - observable объект забега
     */
    override fun getRun(runId: Int): LiveData<RunEntity> {
        return runDao.getRun(runId)
    }

    /**
     * Очистка всех объктов бега из БД
     *
     * @return [Completable] - результат выполнения удаления
     */
    override fun clearRuns(): Completable {
        return Completable.fromCallable { runDao.clearRuns() }
    }

    /**
     * Метод добавления объктов бега в БД
     *
     * @param list [List] - список забегов на добавление
     *
     * @return [Completable] - результат выполнения добавления
     */
    override fun addList(list: List<RunEntity>): Completable {
        return Completable.fromCallable {
            list.forEach {
                runDao.addRun(it)
            }
        }
    }

    /**
     * Метод переключения флага на удаление в БД
     *
     * @param runID [Int] - ID забега
     * @param toDelete [Boolean] - флаг на удаление
     *
     * @return [Completable] - результат выполнения переключения
     */
    override fun switchToDeleteFlag(runID: Int, toDelete: Boolean): Completable {
        return Completable.fromCallable { runDao.switchToDeleteFlag(runID, toDelete) }
    }

    /**
     * Метод удаления всех забегов с флагом на удаление
     *
     * @return [Completable] - результат выполнения удаления
     */
    override fun removeMarkedToDelete(): Completable {
        return Completable.fromCallable { runDao.removerMarkedToDelete() }
    }

    /**
     * Метод удаления всех забегов из БД, помеченных на удаление в Firestore
     *
     * @param markedToDeleteFromCloud [List] - список из забегов на удаление
     *
     * @return [Completable] - результат выполнения удаления
     */
    override fun removeRuns(markedToDeleteFromCloud: List<RunEntity>): Completable {
        val timeStampList : List<Long> = markedToDeleteFromCloud.map { it.timestamp }

        return Completable.fromCallable { runDao.removeRuns(timeStampList) }
    }
}
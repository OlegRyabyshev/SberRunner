package xyz.fcr.sberrunner.domain.interactor.db

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import xyz.fcr.sberrunner.data.room.RunDao
import xyz.fcr.sberrunner.domain.converter.RunConverter
import xyz.fcr.sberrunner.presentation.model.Run

/**
 * Имплементация интерфейса [IDatabaseInteractor], служит для связи Room <-> ViewModel
 *
 * @param runDao [RunDao] - data access objects для получения доступа к базе данных бега
 */
class RoomInteractor(
    private val runDao: RunDao,
    private val converter: RunConverter
) : IDatabaseInteractor {

    /**
     * Метод добавления объкта бега в БД
     *
     * @param run [Run] - объект бега на добавление
     *
     * @return [Completable] - результат выполнения добавления
     */
    override fun addRun(run: Run): Completable {
        return Completable.fromCallable { runDao.addRun(converter.toRunEntity(run)) }
    }

    /**
     * Метод удаления объекта бега из БД
     *
     * @param run [Run] - объект бега на удаление
     *
     * @return [Completable] - результат выполнения удаления
     */
    override fun deleteRun(run: Run): Completable {
        return Completable.fromCallable { runDao.deleteRun(converter.toRunEntity(run)) }
    }

    /**
     * Метод получения объектов бега из БД
     *
     * @return [Single] - результат получения списка забегов
     */
    override fun getAllRuns(): Single<List<Run>> {
        return Single.fromCallable { converter.toRunList(runDao.getAllRuns()) }
    }

    /**
     * Метод получения объкта бега из БД по ID
     *
     * @param timestamp [Long] - временная отметка забега
     *
     * @return [Single] - результ запроса забега
     */
    override fun getRun(timestamp: Long): Single<Run> {
        return Single.fromCallable {
            converter.toRun(
                runDao.getRun(timestamp)
            )
        }
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
    override fun addList(list: List<Run>): Completable {
        return Completable.fromCallable {
            list.forEach {
                runDao.addRun(converter.toRunEntity(it))
            }
        }
    }

    /**
     * Метод переключения флага на удаление в БД
     *
     * @param timestamp [Long] - временная отметка забега
     * @param toDelete [Boolean] - флаг на удаление
     *
     * @return [Completable] - результат выполнения переключения
     */
    override fun switchToDeleteFlag(timestamp: Long, toDelete: Boolean): Completable {
        return Completable.fromCallable { runDao.switchToDeleteFlag(timestamp, toDelete) }
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
    override fun removeRuns(markedToDeleteFromCloud: List<Run>): Completable {
        val timeStampList: List<Long> = markedToDeleteFromCloud.map { it.timestamp }

        return Completable.fromCallable { runDao.removeRuns(timeStampList) }
    }
}
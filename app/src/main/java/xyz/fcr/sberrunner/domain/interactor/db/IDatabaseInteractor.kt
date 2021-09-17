package xyz.fcr.sberrunner.domain.interactor.db

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import xyz.fcr.sberrunner.domain.model.Run

/**
 * Интерфейс доступка к базе данных
 */
interface IDatabaseInteractor {

    /**
     * Метод добавления объкта бега в БД
     *
     * @param run [Run] - объект бега на добавление
     *
     * @return [Completable] - результат выполнения добавления
     */
    fun addRun(run: Run): Completable

    /**
     * Метод удаления объекта бега из БД
     *
     * @param run [Run] - объект бега на удаление
     *
     * @return [Completable] - результат выполнения удаления
     */
    fun deleteRun(run: Run): Completable

    /**
     * Метод получения объектов бега из БД
     *
     * @return [Single] - результат получения списка забегов
     */
    fun getAllRuns(): Single<List<Run>>

    /**
     * Метод получения объкта бега из БД по ID
     *
     * @param timestamp [Long] - временная отметка забега
     *
     * @return [Single] - результ запроса забега
     */
    fun getRun(timestamp: Long): Single<Run>

    /**
     * Очистка всех объктов бега из БД
     *
     * @return [Completable] - результат выполнения удаления
     */
    fun clearRuns(): Completable

    /**
     * Метод добавления объктов бега в БД
     *
     * @param list [List] - список забегов на добавление
     *
     * @return [Completable] - результат выполнения добавления
     */
    fun addList(list: List<Run>): Completable

    /**
     * Метод переключения флага на удаление в БД
     *
     * @param timestamp [Long] - временная отметка забега
     * @param toDelete [Boolean] - флаг на удаление
     *
     * @return [Completable] - результат выполнения переключения
     */
    fun switchToDeleteFlag(timestamp: Long, toDelete: Boolean): Completable

    /**
     * Метод удаления всех забегов с флагом на удаление
     *
     * @return [Completable] - результат выполнения удаления
     */
    fun removeMarkedToDelete(): Completable

    /**
     * Метод удаления всех забегов из БД, помеченных на удаление в Firestore
     *
     * @param markedToDeleteFromCloud [List] - список из забегов на удаление
     *
     * @return [Completable] - результат выполнения удаления
     */
    fun removeRuns(markedToDeleteFromCloud: List<Run>): Completable
}
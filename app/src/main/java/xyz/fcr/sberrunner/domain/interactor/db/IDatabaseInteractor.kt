package xyz.fcr.sberrunner.domain.interactor.db

import androidx.lifecycle.LiveData
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import xyz.fcr.sberrunner.data.model.RunEntity

/**
 * Интерфейс доступка к базе данных
 */
interface IDatabaseInteractor {

    /**
     * Метод добавления объкта бега в БД
     *
     * @param run [RunEntity] - объект бега на добавление
     *
     * @return [Completable] - результат выполнения добавления
     */
    fun addRun(run: RunEntity): Completable

    /**
     * Метод удаления объекта бега из БД
     *
     * @param run [RunEntity] - объект бега на удаление
     *
     * @return [Completable] - результат выполнения удаления
     */
    fun deleteRun(run: RunEntity): Completable

    /**
     * Метод получения объектов бега из БД
     *
     * @return [Single] - результат получения списка забегов
     */
    fun getAllRuns(): Single<List<RunEntity>>

    /**
     * Метод получения объкта бега из БД по ID
     *
     * @param runId [Int] - ID забега
     *
     * @return [LiveData] - observable объект забега
     */
    fun getRun(runId: Int): LiveData<RunEntity>

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
    fun addList(list: List<RunEntity>): Completable

    /**
     * Метод переключения флага на удаление в БД
     *
     * @param runID [Int] - ID забега
     * @param toDelete [Boolean] - флаг на удаление
     *
     * @return [Completable] - результат выполнения переключения
     */
    fun switchToDeleteFlag(runID: Int, toDelete: Boolean): Completable

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
    fun removeRuns(markedToDeleteFromCloud: List<RunEntity>): Completable
}
package xyz.fcr.sberrunner.domain.db

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
     */
    fun addRun(run: RunEntity): Completable

    /**
     * Метод удаления объкта бега из БД
     *
     * @param run [RunEntity] - объект бега на удаление
     */
    fun deleteRun(run: RunEntity): Completable

    /**
     * Метод получения объектов бега из БД
     *
     * @return [Single<List<Run>>] - объекты бега
     */
    fun getAllRuns() : Single<List<RunEntity>>

    /**
     * Метод получения объкта бега из БД по ID
     *
     * @return - LiveData объект забега
     */
    fun getRun(runId: Int) : LiveData<RunEntity>

    fun clearRuns(): Single<Unit>

    fun addList(unitedList: List<RunEntity>): Single<Unit>

    fun switchToDeleteFlag(runID: Int, toDelete: Boolean): Single<Unit>
}
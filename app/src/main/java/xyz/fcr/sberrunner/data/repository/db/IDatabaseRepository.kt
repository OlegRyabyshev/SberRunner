package xyz.fcr.sberrunner.data.repository.db

import androidx.lifecycle.LiveData
import xyz.fcr.sberrunner.data.model.Run

/**
 * Интерфейс доступка к базе данных
 */
interface IDatabaseRepository {

    /**
     * Метод добавления объкта бега в БД
     *
     * @param run [Run] - объект бега на добавление
     */
    fun addRun(run: Run)

    /**
     * Метод удаления объкта бега из БД
     *
     * @param run [Run] - объект бега на удаление
     */
    fun deleteRun(run: Run)

    /**
     * Метод добавления объкта бега в БД
     *
     * @return - LiveData лист из забегов
     */
    fun getAllRuns() : LiveData<List<Run>>
}
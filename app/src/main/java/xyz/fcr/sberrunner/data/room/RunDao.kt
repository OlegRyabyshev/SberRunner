package xyz.fcr.sberrunner.data.room

import androidx.lifecycle.LiveData
import androidx.room.*
import xyz.fcr.sberrunner.data.model.Run
import xyz.fcr.sberrunner.utils.Constants.DB_NAME

/**
 * Интерфейс взаимодействия базой данных бега
 */
@Dao
interface RunDao {

    /**
     * Добавление результатов одного забега
     *
     * @param run [Run] - забег пользователя
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRun(run: Run)

    /**
     * Удаление выбранного забега
     *
     * @param run [Run] - забег пользователя
     */
    @Delete
    fun deleteRun(run: Run)

    /**
     * Удаление всех забегов из базы данных
     */
    @Query("DELETE FROM $DB_NAME")
    fun clearRuns()

    /**
     * Получение всех забегов из базы данных
     *
     * @return [List<Run>] - список забегов пользователя
     */
    @Query("SELECT * FROM $DB_NAME ORDER BY timestamp DESC")
    fun getAllRuns(): List<Run>

    /**
     * Получение забега из базы данных
     *
     * @param runId [Int] - ID забега
     * @return [List<Run>] - список забегов пользователя
     */
    @Query("SELECT * FROM $DB_NAME WHERE id = :runId")
    fun getRun(runId: Int): LiveData<Run>
}
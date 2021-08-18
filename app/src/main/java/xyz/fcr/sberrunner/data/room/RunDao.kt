package xyz.fcr.sberrunner.data.room

import androidx.room.*

@Dao
interface RunDao {
    @Query("SELECT * FROM sber_runner_table ORDER BY timestamp DESC")
    fun getAllRuns(): List<RunEntity>

    @Query("SELECT SUM(timeInMillis) FROM sber_runner_table")
    fun getTotalTimeInMillis(): Long

    @Query("SELECT SUM(distanceInMeters) FROM sber_runner_table")
    fun getTotalDistance(): Int

    @Query("SELECT AVG(avgSpeedInKMH) FROM sber_runner_table")
    fun getTotalAvgSpeed(): Float

    @Query("SELECT SUM(calories) FROM sber_runner_table")
    fun getTotalCaloriesBurned(): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRun(run: RunEntity)

    @Delete
    fun deleteRun(run: RunEntity)
}
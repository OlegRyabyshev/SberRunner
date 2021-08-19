package xyz.fcr.sberrunner.data.room

import androidx.room.*
import xyz.fcr.sberrunner.utils.Constants.DB_NAME

@Dao
interface RunDao {
    @Query("SELECT * FROM $DB_NAME ORDER BY timestamp DESC")
    fun getAllRuns(): List<RunEntity>

    @Query("SELECT SUM(timeInMillis) FROM $DB_NAME")
    fun getTotalTimeInMillis(): Long

    @Query("SELECT SUM(distanceInMeters) FROM $DB_NAME")
    fun getTotalDistance(): Int

    @Query("SELECT AVG(avgSpeedInKMH) FROM $DB_NAME")
    fun getTotalAvgSpeed(): Float

    @Query("SELECT SUM(calories) FROM $DB_NAME")
    fun getTotalCaloriesBurned(): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRun(run: RunEntity)

    @Delete
    fun deleteRun(run: RunEntity)
}
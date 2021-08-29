package xyz.fcr.sberrunner.data.room

import androidx.lifecycle.LiveData
import androidx.room.*
import xyz.fcr.sberrunner.data.model.Run
import xyz.fcr.sberrunner.utils.Constants.DB_NAME

@Dao
interface RunDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRun(run: Run)

    @Delete
    fun deleteRun(run: Run)

    @Query("SELECT * FROM $DB_NAME ORDER BY timestamp DESC")
    fun getAllRuns(): LiveData<List<Run>>

//    @Query("SELECT SUM(timeInMillis) FROM $DB_NAME")
//    fun getTotalTimeInMillis(): Long
//
//    @Query("SELECT SUM(distanceInMeters) FROM $DB_NAME")
//    fun getTotalDistance(): Int
//
//    @Query("SELECT AVG(avgSpeedInKMH) FROM $DB_NAME")
//    fun getTotalAvgSpeed(): Float
//
//    @Query("SELECT SUM(calories) FROM $DB_NAME")
//    fun getTotalCaloriesBurned(): Int
}
package xyz.fcr.sberrunner.data.repository.db

import androidx.lifecycle.LiveData
import xyz.fcr.sberrunner.data.model.Run

interface IDatabaseRepository {
    fun addRun(run: Run)
    fun deleteRun(run: Run)

    fun getAllRuns() : LiveData<List<Run>>
//    fun getTotalDistance() : Int
//    fun getTotalTimeInMillis() : Long
//    fun getTotalAvgSpeed() : Float
//    fun getTotalCaloriesBurned() : Int
}
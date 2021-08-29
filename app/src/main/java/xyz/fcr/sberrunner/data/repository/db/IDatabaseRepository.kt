package xyz.fcr.sberrunner.data.repository.db

import xyz.fcr.sberrunner.data.model.Run

interface IDatabaseRepository {
    fun insertRun(run: Run)
    fun deleteRun(run: Run)

    fun getAllRuns() : List<Run>
    fun getTotalDistance() : Int
    fun getTotalTimeInMillis() : Long
    fun getTotalAvgSpeed() : Float
    fun getTotalCaloriesBurned() : Int
}
package xyz.fcr.sberrunner.domain

import xyz.fcr.sberrunner.data.room.RunDao
import xyz.fcr.sberrunner.data.model.Run
import javax.inject.Inject

class RunInteractor @Inject constructor (private val runDao: RunDao) {

    suspend fun insertRun(run: Run) = runDao.addRun(run)
    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)

    fun getAllRuns() = runDao.getAllRuns()
    fun getTotalDistance() = runDao.getTotalDistance()
    fun getTotalAvgSpeed() = runDao.getTotalAvgSpeed()
    fun getTotalTimeInMillis() = runDao.getTotalTimeInMillis()
    fun getTotalCaloriesBurned() = runDao.getTotalCaloriesBurned()

}
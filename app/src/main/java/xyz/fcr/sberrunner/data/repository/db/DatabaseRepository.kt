package xyz.fcr.sberrunner.data.repository.db

import androidx.lifecycle.LiveData
import io.reactivex.rxjava3.core.Single
import xyz.fcr.sberrunner.data.model.Run
import xyz.fcr.sberrunner.data.room.RunDao
import xyz.fcr.sberrunner.utils.ISchedulersProvider
import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    private val runDao: RunDao,
    private val schedulersProvider: ISchedulersProvider
) : IDatabaseRepository {

    override fun addRun(run: Run) {
        Single.fromCallable { runDao.addRun(run) }
            .subscribeOn(schedulersProvider.io())
            .subscribe()
    }

    override fun deleteRun(run: Run) {
        Single.fromCallable { runDao.deleteRun(run) }
            .subscribeOn(schedulersProvider.io())
            .subscribe()
    }

    override fun getAllRuns(): LiveData<List<Run>> {
        return runDao.getAllRuns()
    }

//    override fun getTotalDistance() = runDao.getTotalDistance()
//    override fun getTotalTimeInMillis() = runDao.getTotalTimeInMillis()
//    override fun getTotalAvgSpeed() = runDao.getTotalAvgSpeed()
//    override fun getTotalCaloriesBurned() = runDao.getTotalCaloriesBurned()
}
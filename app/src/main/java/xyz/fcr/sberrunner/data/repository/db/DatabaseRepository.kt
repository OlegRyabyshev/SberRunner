package xyz.fcr.sberrunner.data.repository.db

import io.reactivex.rxjava3.core.Completable
import xyz.fcr.sberrunner.data.model.Run
import xyz.fcr.sberrunner.data.room.RunDao
import xyz.fcr.sberrunner.utils.ISchedulersProvider
import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    private val runDao: RunDao,
    private val schedulersProvider: ISchedulersProvider
) : IDatabaseRepository {

    override fun insertRun(run: Run) {
        Completable.fromCallable { runDao.addRun(run) }
            .subscribeOn(schedulersProvider.io())
            .subscribe()
    }

    override fun deleteRun(run: Run) {
        Completable.fromCallable { runDao.deleteRun(run) }
            .subscribeOn(schedulersProvider.io())
            .subscribe()
    }

    override fun getAllRuns() = runDao.getAllRuns()
    override fun getTotalDistance() = runDao.getTotalDistance()
    override fun getTotalTimeInMillis() = runDao.getTotalTimeInMillis()
    override fun getTotalAvgSpeed() = runDao.getTotalAvgSpeed()
    override fun getTotalCaloriesBurned() = runDao.getTotalCaloriesBurned()
}
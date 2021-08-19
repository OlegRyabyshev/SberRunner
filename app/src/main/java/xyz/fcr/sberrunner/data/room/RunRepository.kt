package xyz.fcr.sberrunner.data.room

class RunRepository (private val runDao: RunDao) {

    fun getListOfRuns(): List<RunEntity> = runDao.getAllRuns()

    fun createNewListItem(runEntity: RunEntity) {
        runDao.insertRun(runEntity)
    }

    fun deleteListItem(runEntity: RunEntity) {
        runDao.deleteRun(runEntity)
    }
}
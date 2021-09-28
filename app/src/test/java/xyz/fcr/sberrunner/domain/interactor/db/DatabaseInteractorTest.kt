package xyz.fcr.sberrunner.domain.interactor.db

import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import xyz.fcr.sberrunner.data.room.RunDao
import xyz.fcr.sberrunner.data.converter.RunConverter
import xyz.fcr.sberrunner.domain.model.Run

class DatabaseInteractorTest {

    private val runDao: RunDao = mockk(relaxed = true)
    private val converter: RunConverter = RunConverter()

    private val databaseInteractor = DatabaseInteractor(runDao, converter)

    @Test
    fun `addRun should not invoke function until subscription`() {
        val completable = databaseInteractor.addRun(mockRun)

        verify(exactly = 0) {
            runDao.addRun(converter.toRunEntity(mockRun))
        }

        val disposable = completable.subscribe({}, {})

        verify(exactly = 1) {
            runDao.addRun(converter.toRunEntity(mockRun))
        }

        disposable.dispose()
    }

    @Test
    fun `deleteRun should not invoke function until subscription`() {
        val completable = databaseInteractor.deleteRun(mockRun)

        verify(exactly = 0) {
            runDao.deleteRun(converter.toRunEntity(mockRun))
        }

        val disposable = completable.subscribe({}, {})

        verify(exactly = 1) {
            runDao.deleteRun(converter.toRunEntity(mockRun))
        }

        disposable.dispose()
    }

    @Test
    fun `getAllRuns should not invoke function until subscription`() {
        val single = databaseInteractor.getAllRuns()

        verify(exactly = 0) {
            converter.toRunList(runDao.getAllRuns())
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            converter.toRunList(runDao.getAllRuns())
        }

        disposable.dispose()
    }

    @Test
    fun `getRun should not invoke function until subscription`() {
        val single = databaseInteractor.getRun(mockRun.timestamp)

        verify(exactly = 0) {
            converter.toRun(runDao.getRun(mockRun.timestamp))
        }

        val disposable = single.subscribe({}, {})

        verify(exactly = 1) {
            converter.toRun(runDao.getRun(mockRun.timestamp))
        }

        disposable.dispose()
    }

    @Test
    fun `clearRuns should not invoke function until subscription`() {
        val completable = databaseInteractor.clearRuns()

        verify(exactly = 0) {
            runDao.clearRuns()
        }

        val disposable = completable.subscribe({}, {})

        verify(exactly = 1) {
            runDao.clearRuns()
        }

        disposable.dispose()
    }

    @Test
    fun `addList should not invoke function until subscription`() {
        val completable = databaseInteractor.addList(listOfMockedRuns)

        verify(exactly = 0) {
            listOfMockedRuns.forEach {
                runDao.addRun(converter.toRunEntity(it))
            }
        }

        val disposable = completable.subscribe({}, {})

        verify(exactly = 1) {
            listOfMockedRuns.forEach {
                runDao.addRun(converter.toRunEntity(it))
            }
        }

        disposable.dispose()
    }

    @Test
    fun `switchToDeleteFlag should not invoke function until subscription`() {
        val completable = databaseInteractor.switchToDeleteFlag(mockRun.timestamp, TO_DELETE)

        verify(exactly = 0) {
            runDao.switchToDeleteFlag(mockRun.timestamp, TO_DELETE)
        }

        val disposable = completable.subscribe({}, {})

        verify(exactly = 1) {
            runDao.switchToDeleteFlag(mockRun.timestamp, TO_DELETE)
        }

        disposable.dispose()
    }

    @Test
    fun `removeMarkedToDelete should not invoke function until subscription`() {
        val completable = databaseInteractor.removeMarkedToDelete()

        verify(exactly = 0) {
            runDao.removerMarkedToDelete()
        }

        val disposable = completable.subscribe({}, {})

        verify(exactly = 1) {
            runDao.removerMarkedToDelete()
        }

        disposable.dispose()
    }

    @Test
    fun `removeRuns should not invoke function until subscription`() {
        val completable = databaseInteractor.removeRuns(listOfMockedRuns)

        verify(exactly = 0) {
            runDao.removeRuns(any())
        }

        val disposable = completable.subscribe({}, {})

        verify(exactly = 1) {
            runDao.removeRuns(any())
        }

        disposable.dispose()
    }

    private companion object {
        private const val TO_DELETE = true

        private val mockRun = Run(
            timestamp = 100L
        )

        private val listOfMockedRuns = listOf(
            Run(timestamp = 10L),
            Run(timestamp = 20L),
            Run(timestamp = 30L)
        )
    }
}
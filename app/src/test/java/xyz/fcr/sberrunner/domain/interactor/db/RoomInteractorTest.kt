package xyz.fcr.sberrunner.domain.interactor.db

import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import xyz.fcr.sberrunner.data.room.RunDao
import xyz.fcr.sberrunner.domain.converter.RunConverter
import xyz.fcr.sberrunner.presentation.model.Run

class RoomInteractorTest {

    private val runDao: RunDao = mockk(relaxed = true)
    private val converter: RunConverter = RunConverter()

    private val roomInteractor = RoomInteractor(runDao, converter)

    @Test
    fun `addRun should not invoke function until subscription`() {
        val completable = roomInteractor.addRun(mockRun)

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
        val completable = roomInteractor.deleteRun(mockRun)

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
        val single = roomInteractor.getAllRuns()

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
        val single = roomInteractor.getRun(mockRun.timestamp)

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
        val completable = roomInteractor.clearRuns()

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
        val completable = roomInteractor.addList(listOfMockedRuns)

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
        val completable = roomInteractor.switchToDeleteFlag(mockRun.timestamp, TO_DELETE)

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
        val completable = roomInteractor.removeMarkedToDelete()

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
        val completable = roomInteractor.removeRuns(listOfMockedRuns)

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
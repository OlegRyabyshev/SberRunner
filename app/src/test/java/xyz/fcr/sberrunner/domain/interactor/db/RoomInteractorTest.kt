package xyz.fcr.sberrunner.domain.interactor.db

import io.mockk.*
import io.reactivex.rxjava3.core.Completable
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import xyz.fcr.sberrunner.data.room.RunDao
import xyz.fcr.sberrunner.domain.converter.RunConverter
import xyz.fcr.sberrunner.presentation.model.Run
import java.util.*

class RoomInteractorTest {

    private val runDao: RunDao = mockk()
    private val runConverter: RunConverter = RunConverter()

    private val roomInteractor = RoomInteractor(runDao, runConverter)

    @Test
    fun addRun() = runBlocking {
        every { runDao.addRun(runConverter.toRunEntity(mockRun)) } returns Unit

        roomInteractor.addRun(mockRun)

        verify {
            runDao.addRun(runConverter.toRunEntity(mockRun))
        }
    }

    @Test
    fun deleteRun() {
    }

    @Test
    fun getAllRuns() {
    }

    @Test
    fun getRun() {
    }

    @Test
    fun clearRuns() {
    }

    @Test
    fun addList() {
    }

    @Test
    fun switchToDeleteFlag() {
    }

    @Test
    fun removeMarkedToDelete() {
    }

    @Test
    fun removeRuns() {
    }

    private companion object{
        private val mockRun = Run()
    }
}
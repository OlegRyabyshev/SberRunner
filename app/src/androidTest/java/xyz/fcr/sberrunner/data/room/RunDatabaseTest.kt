package xyz.fcr.sberrunner.data.room

import androidx.room.Room
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import xyz.fcr.sberrunner.data.model.RunEntity
import xyz.fcr.sberrunner.presentation.App
import java.io.IOException

class RunDatabaseTest {
    private lateinit var runDao: RunDao
    private lateinit var db: RunDatabase

    @Before
    fun setUp() {
        val context = App.appComponent.context()

        db = Room.inMemoryDatabaseBuilder(
            context,
            RunDatabase::class.java
        ).build()

        runDao = db.getRunDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testAddAndGetRun() = runBlocking {
        runDao.addRun(mockRun)

        val run = runDao.getRun(TIMESTAMP)
        assertTrue(run.timestamp == TIMESTAMP)
    }

    @Test
    fun testDeleteRun() = runBlocking {
        runDao.addRun(mockRun)
        runDao.deleteRun(mockRun)

        val runs = runDao.getAllRuns()

        assert(!runs.contains(mockRun))
    }


    @Test
    fun testClearRuns() = runBlocking {
        listOfMockRuns.forEach {
            runDao.addRun(it)
        }

        runDao.clearRuns()

        val runs = runDao.getAllRuns()

        assertTrue(runs.isEmpty())
    }

    @Test
    fun testSwitchToDeleteFlag() = runBlocking {
        runDao.addRun(mockRun)

        runDao.switchToDeleteFlag(TIMESTAMP, TO_DELETE)

        val run = runDao.getRun(TIMESTAMP)

        assertTrue(run.toDeleteFlag)
    }

    @Test
    fun testSwitchToRestoreFlag() = runBlocking {
        runDao.addRun(mockRun.apply { toDeleteFlag = true })

        runDao.switchToDeleteFlag(TIMESTAMP, TO_RESTORE)

        val run = runDao.getRun(TIMESTAMP)

        assertFalse(run.toDeleteFlag)
    }

    @Test
    fun testRemoveToDeleteFlag() = runBlocking {
        listOfMockRunsToDelete.forEach {
            runDao.addRun(it)
        }

        runDao.removerMarkedToDelete()

        val runs = runDao.getAllRuns()

        assertTrue(runs.isEmpty())
    }

    @Test
    fun testRemoveRuns() = runBlocking {
        listOfMockRuns.forEach {
            runDao.addRun(it)
        }

        runDao.removeRuns(listOfMockRuns.map { it.timestamp })

        val runs = runDao.getAllRuns()

        assertTrue(runs.isEmpty())
    }

    private companion object {
        private val mockRun = RunEntity().apply {
            timestamp = TIMESTAMP
            id = 1
        }

        private val listOfMockRuns = listOf(
            RunEntity().apply { id = 1 },
            RunEntity().apply { id = 2 },
            RunEntity().apply { id = 3 }
        )

        private val listOfMockRunsToDelete = listOf(
            RunEntity().apply {
                id = 1
                toDeleteFlag = true
            },
            RunEntity().apply {
                id = 1
                toDeleteFlag = true
            },
            RunEntity().apply {
                id = 1
                toDeleteFlag = true
            }
        )

        private const val TIMESTAMP = 100L

        private const val TO_DELETE = true
        private const val TO_RESTORE = false
    }
}
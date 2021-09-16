package xyz.fcr.sberrunner.domain.converter

import android.graphics.BitmapFactory
import org.junit.Assert.*
import org.junit.Test
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.data.model.RunEntity
import xyz.fcr.sberrunner.presentation.App
import xyz.fcr.sberrunner.presentation.model.Run

class RunConverterTest {

    private val runConverter = RunConverter()

    @Test
    fun convertToRun() {
        val run = runConverter.toRun(mockRunEntity)

        assertTrue(isEqually(run, mockRunEntity))
        assertEquals(mockRun, run)
    }

    @Test
    fun convertToRunEntity() {
        val runEntity = runConverter.toRunEntity(mockRun)

        assertTrue(isEqually(mockRun, runEntity))
        assertEquals(mockRunEntity, runEntity)
    }

    @Test
    fun convertToListRun() {
        val runList = runConverter.toRunList(listOfRunsEntity)

        for (i in runList.indices) {
            assertTrue(isEqually(runList[i], listOfRunsEntity[i]))
            assertEquals(mockRun, runList[i])
        }
    }

    @Test
    fun convertToListRunEntity() {
        val runEntityList = runConverter.toRunEntityList(listOfRuns)

        for (i in runEntityList.indices) {
            assertTrue(isEqually(listOfRuns[i], runEntityList[i]))
            assertEquals(mockRunEntity, runEntityList[i])
        }
    }

    private fun isEqually(run: Run, runEntity: RunEntity): Boolean {
        return run.avgSpeedInKMH == runEntity.avgSpeedInKMH &&
                run.calories == runEntity.calories &&
                run.distanceInMeters == runEntity.distanceInMeters &&
                run.timeInMillis == runEntity.timeInMillis &&
                run.timestamp == runEntity.timestamp &&
                run.toDeleteFlag == runEntity.toDeleteFlag &&
                run.mapImage == runEntity.mapImage
    }

    private companion object {
        private val bitmap = BitmapFactory.decodeResource(
            App.appComponent.context().resources,
            R.drawable.ic_map
        )

        private val mockRun = Run(
            avgSpeedInKMH = "16.0",
            calories = 32L,
            distanceInMeters = 64L,
            timeInMillis = 128L,
            timestamp = 256L,
            toDeleteFlag = false,
            mapImage = bitmap
        )

        private val mockRunEntity = RunEntity(
            avgSpeedInKMH = "16.0",
            calories = 32L,
            distanceInMeters = 64L,
            timeInMillis = 128L,
            timestamp = 256L,
            toDeleteFlag = false,
            mapImage = bitmap
        )

        private val listOfRuns = listOf(
            mockRun,
            mockRun,
            mockRun
        )

        private val listOfRunsEntity = listOf(
            mockRunEntity,
            mockRunEntity,
            mockRunEntity
        )
    }
}
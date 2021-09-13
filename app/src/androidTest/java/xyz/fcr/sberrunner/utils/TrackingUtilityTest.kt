package xyz.fcr.sberrunner.utils

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class TrackingUtilityTest {

    @Test
    fun validateEmptyTime() {
        val actual = TrackingUtility.getFormattedStopWatchTime(INPUT_1)

        assertEquals(OUTPUT_1, actual)
    }

    @Test
    fun validateFirstTwoDigits() {
        val actual = TrackingUtility.getFormattedStopWatchTime(INPUT_2)

        assertEquals(OUTPUT_2, actual)
    }

    @Test
    fun validateSecondTwoDigits() {
        val actual = TrackingUtility.getFormattedStopWatchTime(INPUT_3)

        assertEquals(OUTPUT_3, actual)
    }

    @Test
    fun validateThirdTwoDigits() {
        val actual = TrackingUtility.getFormattedStopWatchTime(INPUT_4)

        assertEquals(OUTPUT_4, actual)
    }

    @Test
    fun checkWrongDigits() {
        val actual = TrackingUtility.getFormattedStopWatchTime(INPUT_WRONG)

        assertNotEquals(OUTPUT_WRONG, actual)
    }

    @Test
    fun checkDistanceForTwoPoints() {
        var distance = 0f
        for (i in 0..DISTANCE_INPUT_1.size - 2) {
            val pos1 = DISTANCE_INPUT_1[i]
            val pos2 = DISTANCE_INPUT_1[i + 1]
            val result = FloatArray(1)

            Location.distanceBetween(
                pos1.latitude,
                pos1.longitude,
                pos2.latitude,
                pos2.longitude,
                result
            )

            distance += result[0]
        }

        assertEquals(DISTANCE_OUTPUT_1, distance)
    }

    @Test
    fun checkDistanceForThreePoints() {
        var distance = 0f
        for (i in 0..DISTANCE_INPUT_2.size - 2) {
            val pos1 = DISTANCE_INPUT_2[i]
            val pos2 = DISTANCE_INPUT_2[i + 1]
            val result = FloatArray(1)

            Location.distanceBetween(
                pos1.latitude,
                pos1.longitude,
                pos2.latitude,
                pos2.longitude,
                result
            )

            distance += result[0]
        }

        assertEquals(DISTANCE_OUTPUT_2, distance.toInt())
    }


    private companion object {
        private const val INPUT_1 = 0L
        private const val OUTPUT_1 = "00:00:00"

        private const val INPUT_2 = 30_000L
        private const val OUTPUT_2 = "00:00:30"

        private const val INPUT_3 = 1_800_000L
        private const val OUTPUT_3 = "00:30:00"

        private const val INPUT_4 = 43_200_000L
        private const val OUTPUT_4 = "12:00:00"

        private const val INPUT_WRONG = 1_000_000L
        private const val OUTPUT_WRONG = "00:01:00"

        private val DISTANCE_INPUT_1: List<LatLng> = listOf(
            LatLng(50.0, 50.0),
            LatLng(50.0, 50.0)
        )

        private val DISTANCE_INPUT_2: List<LatLng> = listOf(
            LatLng(50.0, 50.0),
            LatLng(-50.0, -50.0),
            LatLng(50.0, 50.0)
        )

        private const val DISTANCE_OUTPUT_1 = 0f
        private const val DISTANCE_OUTPUT_2 = 29_139_518 //meters
    }
}
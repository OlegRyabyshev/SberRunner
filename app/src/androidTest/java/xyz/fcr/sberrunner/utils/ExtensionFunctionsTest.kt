package xyz.fcr.sberrunner.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.presentation.App

class ExtensionFunctionsTest {

    @Test
    fun checkAddingDistanceMetric() {
        val expected = TEST_STRING + KM_ADDICTION
        val actual = TEST_STRING.addDistanceUnits(IS_METRIC)

        assertEquals(expected, actual)
    }

    @Test
    fun checkAddingDistanceNonMetric() {
        val expected = TEST_STRING + MILES_ADDICTION
        val actual = TEST_STRING.addDistanceUnits(NON_METRIC)

        assertEquals(expected, actual)
    }

    @Test
    fun checkAddingSpeedMetric() {
        val expected = TEST_STRING + KMH_ADDICTION
        val actual = TEST_STRING.addSpeedUnits(IS_METRIC)

        assertEquals(expected, actual)
    }

    @Test
    fun checkAddingSpeedNonMetric() {
        val expected = TEST_STRING + MPH_ADDICTION
        val actual = TEST_STRING.addSpeedUnits(NON_METRIC)

        assertEquals(expected, actual)
    }

    @Test
    fun checkCalories() {
        val expected = TEST_STRING + KCAL_ADDICTION
        val actual = TEST_STRING.addCalories()

        assertEquals(expected, actual)
    }

    @Test
    fun checkGetAverageMetric() {
        val actual = LONG_INPUT.getAverage(IS_METRIC, COUNT).toInt()

        assertEquals(AVERAGE_RESULT_METRIC, actual)
    }

    @Test
    fun checkGetAverageNonMetric() {
        val actual = LONG_INPUT.getAverage(NON_METRIC, COUNT).toInt()

        assertEquals(AVERAGE_RESULT_NON_METRIC, actual)
    }

    @Test
    fun checkConvertMetersToKilometers() {
        val actual = INPUT_METERS.convertMetersToKilometres()

        assertEquals(OUTPUT_KM, actual)
    }

    @Test
    fun checkConvertMetersToMiles() {
        val actual = INPUT_METERS.convertMetersToMiles()

        assertEquals(OUTPUT_MILE, actual)
    }

    @Test
    fun checkConvertKMHToMPH() {
        val actual = INPUT_KMH_SPEED.convertKMHtoMPH()

        assertEquals(OUTPUT_MPH_SPEED, actual)
    }

    private companion object {
        private const val IS_METRIC = true
        private const val NON_METRIC = false

        private const val TEST_STRING = "text"

        private val KM_ADDICTION = App.appComponent.context().resources.getString(R.string.km_addition)
        private val MILES_ADDICTION = App.appComponent.context().resources.getString(R.string.miles_addition)

        private val KMH_ADDICTION = App.appComponent.context().resources.getString(R.string.km_h_addition)
        private val MPH_ADDICTION = App.appComponent.context().resources.getString(R.string.mph_addition)

        private val KCAL_ADDICTION = App.appComponent.context().resources.getString(R.string.kcal_addition)

        private const val COUNT = 10

        private const val LONG_INPUT = 100_000L

        private const val AVERAGE_RESULT_METRIC = 10
        private const val AVERAGE_RESULT_NON_METRIC = 6

        private const val INPUT_METERS = 1000L

        private const val OUTPUT_KM = 1f
        private const val OUTPUT_MILE = 0.62f

        private const val INPUT_KMH_SPEED = 100f
        private const val OUTPUT_MPH_SPEED = 62.14f
    }
}
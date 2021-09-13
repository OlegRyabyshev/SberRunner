package xyz.fcr.sberrunner.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.presentation.App

class ExtensionFunctionsTest {

    @Test
    fun checkAddingDistance() {
        assertThat(TEST_STRING + KM_ADDICTION)
            .isEqualTo(TEST_STRING.addDistanceUnits(IS_METRIC))

        assertThat(TEST_STRING + MILES_ADDICTION)
            .isEqualTo(TEST_STRING.addDistanceUnits(NOT_METRIC))
    }

    @Test
    fun checkAddingSpeed() {
        assertThat(TEST_STRING + KMH_ADDICTION)
            .isEqualTo(TEST_STRING.addSpeedUnits(IS_METRIC))

        assertThat(TEST_STRING + MPH_ADDICTION)
            .isEqualTo(TEST_STRING.addSpeedUnits(NOT_METRIC))
    }

    @Test
    fun checkCalories() {
        assertThat(TEST_STRING + KCAL_ADDICTION)
            .isEqualTo(TEST_STRING.addCalories())
    }

    @Test
    fun checkGetAverage() {
        assertThat(AVERAGE_RESULT_METRIC)
            .isEqualTo(LONG_INPUT.getAverage(IS_METRIC, COUNT).toInt())

        assertThat(AVERAGE_RESULT_NON_METRIC)
            .isEqualTo(LONG_INPUT.getAverage(NOT_METRIC, COUNT).toInt())
    }

    @Test
    fun checkConvertMetersToKilometers() {
        assertThat(OUTPUT_KM)
            .isEqualTo(INPUT_METERS.convertMetersToKilometres())
    }

    @Test
    fun checkConvertMetersToMiles() {
        assertThat(OUTPUT_MILE)
            .isEqualTo(INPUT_METERS.convertMetersToMiles())
    }

    @Test
    fun checkConvertKMHToMPH() {
        assertThat(OUTPUT_MPH_SPEED)
            .isEqualTo(INPUT_KMH_SPEED.convertKMHtoMPH())
    }

    private companion object {
        private const val IS_METRIC = true
        private const val NOT_METRIC = false

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
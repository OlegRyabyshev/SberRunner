package xyz.fcr.sberrunner.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TrackingUtilityTest {

    @Test
    fun validateEmptyTime() {
        assertThat(OUTPUT_1).isEqualTo(TrackingUtility.getFormattedStopWatchTime(INPUT_1))
    }

    @Test
    fun validateFirstTwoDigits() {
        assertThat(OUTPUT_2).isEqualTo(TrackingUtility.getFormattedStopWatchTime(INPUT_2))
    }

    @Test
    fun validateSecondTwoDigits() {
        assertThat(OUTPUT_3).isEqualTo(TrackingUtility.getFormattedStopWatchTime(INPUT_3))
    }

    @Test
    fun validateThirdTwoDigits() {
        assertThat(OUTPUT_4).isEqualTo(TrackingUtility.getFormattedStopWatchTime(INPUT_4))
    }

    @Test
    fun checkWrongDigits() {
        assertThat(OUTPUT_WRONG).isNotEqualTo(TrackingUtility.getFormattedStopWatchTime(INPUT_WRONG))
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
    }
}
package xyz.fcr.sberrunner.presentation.service.notification

import android.media.MediaPlayer
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith
import xyz.fcr.sberrunner.data.repository.shared.SharedPreferenceWrapper

@RunWith(AndroidJUnit4::class)
class AudioNotificatorTest {
    private val mediaPlayer: MediaPlayer = mockk(relaxed = true)
    private val sharedPreferenceWrapper: SharedPreferenceWrapper = mockk(relaxed = true)

    private val audioNotificator = AudioNotificator(mediaPlayer, sharedPreferenceWrapper)

    @Test
    fun playCrossingBorderDistance() {
        every { sharedPreferenceWrapper.isMetric() } returns true

        audioNotificator.checkIfVoiceNotificationNeeded(OLD_DISTANCE, NEW_DISTANCE)

        verify(exactly = 1) {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(any(), any())
            mediaPlayer.start()
        }
    }

    @Test
    fun playNotCrossingBorderDistance() {
        every { sharedPreferenceWrapper.isMetric() } returns true

        audioNotificator.checkIfVoiceNotificationNeeded(OLD_DISTANCE_WRONG, NEW_DISTANCE_WRONG)

        verify(exactly = 0) {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(any(), any())
            mediaPlayer.start()
        }
    }

    private companion object {
        private const val OLD_DISTANCE = 0.9f // Пересекаем отметку в 1км
        private const val NEW_DISTANCE = 1.1f

        private const val OLD_DISTANCE_WRONG = 0.7f // Не пересекаем пограничные отметки
        private const val NEW_DISTANCE_WRONG = 0.8f
    }
}
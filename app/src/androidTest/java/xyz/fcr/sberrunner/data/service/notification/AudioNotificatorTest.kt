package xyz.fcr.sberrunner.data.service.notification

import android.media.MediaPlayer
import io.mockk.mockk
import org.junit.Test
import xyz.fcr.sberrunner.data.repository.shared.SharedPreferenceWrapper

class AudioNotificatorTest {
    private val mediaPlayer : MediaPlayer = mockk()
    private val sharedPreferenceWrapper: SharedPreferenceWrapper = mockk()

    private val audioNotificator = AudioNotificator(mediaPlayer, sharedPreferenceWrapper)

    @Test
    fun playCrossingBorderDistance() {
        audioNotificator.checkIfVoiceNotificationNeeded(OLD_DISTANCE, NEW_DISTANCE)
    }

    private companion object {
        private const val OLD_DISTANCE = 0.9f
        private const val NEW_DISTANCE = 1.1f


    }

}
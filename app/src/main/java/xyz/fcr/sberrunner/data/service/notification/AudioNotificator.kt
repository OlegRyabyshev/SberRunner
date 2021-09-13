package xyz.fcr.sberrunner.data.service.notification

import android.media.MediaPlayer
import android.net.Uri
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.data.repository.shared.ISharedPreferenceWrapper
import xyz.fcr.sberrunner.presentation.App
import xyz.fcr.sberrunner.utils.Constants.UNIT_RATIO

/**
 * Имплементация интерфейса [IAudioNotificator], служит звуковых нотификаций во время бега
 *
 * @param mediaPlayer [MediaPlayer] - проигрыватель звуков
 * @param sharedPreferenceWrapper [ISharedPreferenceWrapper] - интерфейс взаимодействия с SharedPreference
 */
class AudioNotificator(
    private val mediaPlayer: MediaPlayer,
    private val sharedPreferenceWrapper: ISharedPreferenceWrapper
) : IAudioNotificator {

    /**
     * Воспроизведение звуков
     *
     * @param action [String] - выбранный звук для воспроизведения
     */
    override fun play(action: String) {
        updateVolume()

        var record: Int? = null

        when (action) {
            VOICE_START -> record = R.raw.voice_start
            VOICE_COMPLETE -> record = R.raw.voice_complete
            VOICE_PAUSE -> record = R.raw.voice_pause
            VOICE_RESUME -> record = R.raw.voice_resume

            VOICE_500 -> record = R.raw.voice_500
            VOICE_1000 -> record = R.raw.voice_1000
            VOICE_2000 -> record = R.raw.voice_2000
            VOICE_3000 -> record = R.raw.voice_3000
            VOICE_4000 -> record = R.raw.voice_4000
            VOICE_5000 -> record = R.raw.voice_5000
            VOICE_7000 -> record = R.raw.voice_7000
            VOICE_9000 -> record = R.raw.voice_9000
        }

        if (record != null) {
            val uri = Uri.parse("android.resource://xyz.fcr.sberrunner/$record")

            try {
                mediaPlayer.reset()
                mediaPlayer.setDataSource(App.appComponent.context(), uri)
                mediaPlayer.prepare()
                mediaPlayer.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Функция нотификации пользователя при пересечении пограничных значений в дистанициях
     *
     * @param oldDistance [Float] - прошлая обновленная дистанция
     * @param newDistance [Float] - текущаая обновленная дистанция
     */
    override fun checkIfVoiceNotificationNeeded(oldDistance: Float, newDistance: Float) {
        var unitRatio = 1f

        if (!sharedPreferenceWrapper.isMetric()) unitRatio = UNIT_RATIO

        when {
            oldDistance * unitRatio < DISTANCE500 && newDistance * unitRatio >= DISTANCE500 -> play(VOICE_500)
            oldDistance * unitRatio < DISTANCE1000 && newDistance * unitRatio >= DISTANCE1000 -> play(VOICE_1000)
            oldDistance * unitRatio < DISTANCE2000 && newDistance * unitRatio >= DISTANCE2000 -> play(VOICE_2000)
            oldDistance * unitRatio < DISTANCE3000 && newDistance * unitRatio >= DISTANCE3000 -> play(VOICE_3000)
            oldDistance * unitRatio < DISTANCE4000 && newDistance * unitRatio >= DISTANCE4000 -> play(VOICE_4000)
            oldDistance * unitRatio < DISTANCE5000 && newDistance * unitRatio >= DISTANCE5000 -> play(VOICE_5000)
            oldDistance * unitRatio < DISTANCE7000 && newDistance * unitRatio >= DISTANCE7000 -> play(VOICE_7000)
            oldDistance * unitRatio < DISTANCE9000 && newDistance * unitRatio >= DISTANCE9000 -> play(VOICE_9000)
        }
    }

    /**
     * Обновление громкости нотификатора по значению из SharedPreference
     */
    private fun updateVolume() {
        if (sharedPreferenceWrapper.getVoiceNotificationStatus()) {
            mediaPlayer.setVolume(UNMUTED, UNMUTED)
        } else {
            mediaPlayer.setVolume(MUTED, MUTED)
        }
    }

    companion object {
        const val VOICE_START = "VOICE_START"
        const val VOICE_COMPLETE = "VOICE_COMPLETE"
        const val VOICE_PAUSE = "VOICE_PAUSE"
        const val VOICE_RESUME = "VOICE_RESUME"

        const val VOICE_500 = "VOICE_500"
        const val VOICE_1000 = "VOICE_1000"
        const val VOICE_2000 = "VOICE_2000"
        const val VOICE_3000 = "VOICE_3000"
        const val VOICE_4000 = "VOICE_4000"
        const val VOICE_5000 = "VOICE_5000"
        const val VOICE_7000 = "VOICE_7000"
        const val VOICE_9000 = "VOICE_9000"

        const val DISTANCE500 = 0.5f
        const val DISTANCE1000 = 1f
        const val DISTANCE2000 = 2f
        const val DISTANCE3000 = 3f
        const val DISTANCE4000 = 4f
        const val DISTANCE5000 = 5f
        const val DISTANCE7000 = 7f
        const val DISTANCE9000 = 9f

        const val MUTED = 0f
        const val UNMUTED = 1f
    }
}
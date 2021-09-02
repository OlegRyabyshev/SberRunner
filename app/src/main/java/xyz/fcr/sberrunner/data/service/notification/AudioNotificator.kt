package xyz.fcr.sberrunner.data.service.notification

import android.media.MediaPlayer
import android.net.Uri
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.data.repository.shared.ISharedPreferenceWrapper
import xyz.fcr.sberrunner.presentation.App
import xyz.fcr.sberrunner.utils.Constants.UNIT_RATIO
import javax.inject.Inject


class AudioNotificator @Inject constructor(
    private val mediaPlayer: MediaPlayer,
    private val sharedPreferenceWrapper: ISharedPreferenceWrapper
) : IAudioNotificator {

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
            VOICE_3000 -> record = R.raw.voice_3000
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

    override fun checkIfVoiceNotificationNeeded(oldDistance: Float, newDistance: Float) {
        var unitRatio = 1f

        if (!sharedPreferenceWrapper.isMetric()) unitRatio = UNIT_RATIO

        when {
            oldDistance * unitRatio < 0.5f && newDistance * unitRatio >= 0.5f -> play(VOICE_500)
            oldDistance * unitRatio < 1f && newDistance * unitRatio >= 1f -> play(VOICE_1000)
            oldDistance * unitRatio < 3f && newDistance * unitRatio >= 3f -> play(VOICE_3000)
            oldDistance * unitRatio < 5f && newDistance * unitRatio >= 5f -> play(VOICE_5000)
            oldDistance * unitRatio < 7f && newDistance * unitRatio >= 7f -> play(VOICE_7000)
            oldDistance * unitRatio < 9f && newDistance * unitRatio >= 9f -> play(VOICE_9000)
        }
    }

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
        const val VOICE_3000 = "VOICE_3000"
        const val VOICE_5000 = "VOICE_5000"
        const val VOICE_7000 = "VOICE_7000"
        const val VOICE_9000 = "VOICE_9000"

        const val MUTED = 0f
        const val UNMUTED = 1f
    }
}
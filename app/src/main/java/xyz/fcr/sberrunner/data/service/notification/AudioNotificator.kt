package xyz.fcr.sberrunner.data.service.notification

import android.media.MediaPlayer
import android.net.Uri
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.presentation.App
import javax.inject.Inject


class AudioNotificator @Inject constructor(private val mediaPlayer: MediaPlayer) {

    fun voiceStartOfTheRun(action: String) {
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

    fun mute() {
        mediaPlayer.setVolume(MUTED, MUTED)
    }

    fun unmute() {
        mediaPlayer.setVolume(UNMUTED, UNMUTED)
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
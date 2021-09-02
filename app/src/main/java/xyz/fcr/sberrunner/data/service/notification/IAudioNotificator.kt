package xyz.fcr.sberrunner.data.service.notification

interface IAudioNotificator {
    fun play(action: String)
    fun checkIfVoiceNotificationNeeded(oldDistance: Float, newDistance: Float)
}
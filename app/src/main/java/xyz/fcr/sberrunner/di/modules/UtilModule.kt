package xyz.fcr.sberrunner.di.modules

import android.media.MediaPlayer
import dagger.Module
import dagger.Provides
import xyz.fcr.sberrunner.data.repository.shared.ISharedPreferenceWrapper
import xyz.fcr.sberrunner.presentation.service.notification.AudioNotificator
import xyz.fcr.sberrunner.presentation.service.notification.IAudioNotificator
import xyz.fcr.sberrunner.data.util.BitmapConverter
import xyz.fcr.sberrunner.domain.converter.RunConverter
import xyz.fcr.sberrunner.utils.schedulers.ISchedulersProvider
import xyz.fcr.sberrunner.utils.schedulers.SchedulersProvider
import javax.inject.Singleton

/**
 * Модуль Util, предоставляющий различные зависимости
 */
@Module
object UtilModule {

    /**
     * Предоставление ISchedulersProvider
     *
     * @return [ISchedulersProvider] - интерфейс выполенения задач на разных потоках
     */
    @Singleton
    @Provides
    fun provideSchedulersProvider(): ISchedulersProvider {
        return SchedulersProvider()
    }

    /**
     * Предоставление объекта AudioNotificator
     *
     * @param mediaPlayer [MediaPlayer] - объект воспроизведения звуков в сервисе
     * @param sharedPreferences [ISharedPreferenceWrapper] - дефолтный объект SharedPreference
     *
     * @return [AudioNotificator] - объект воспроизведения уведомлений бега
     */
    @Singleton
    @Provides
    fun provideAudioNotificator(
        mediaPlayer: MediaPlayer,
        sharedPreferences: ISharedPreferenceWrapper
    ): IAudioNotificator {
        return AudioNotificator(mediaPlayer, sharedPreferences)
    }

    /**
     * Предоставление объекта MediaPlayer
     *
     * @return [MediaPlayer] - объект воспроизведения звуков в сервисе
     */
    @Singleton
    @Provides
    fun provideMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }

    /**
     * Предоставление объекта конвертации забегов между слоями логики
     *
     * @return [RunConverter] - конвертер забегов
     */
    @Singleton
    @Provides
    fun provideRunConverter(): RunConverter {
        return RunConverter()
    }

    /**
     * Предоставление объекта конвертора Bitmap <-> ByteArray
     *
     * @return [RunConverter] - конвертер забегов
     */
    @Singleton
    @Provides
    fun provideBitmapConverter(): BitmapConverter{
        return BitmapConverter()
    }
}
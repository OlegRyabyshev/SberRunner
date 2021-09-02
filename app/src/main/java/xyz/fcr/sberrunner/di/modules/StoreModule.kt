package xyz.fcr.sberrunner.di.modules

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import xyz.fcr.sberrunner.data.repository.shared.ISharedPreferenceWrapper
import xyz.fcr.sberrunner.data.repository.shared.SharedPreferenceWrapper
import xyz.fcr.sberrunner.data.service.notification.AudioNotificator
import javax.inject.Singleton

@Module
class StoreModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(context: Context) : SharedPreferences{
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Singleton
    @Provides
    fun provideISharedPreferenceWrapper(sharedPreferences: SharedPreferences) : ISharedPreferenceWrapper{
        return SharedPreferenceWrapper(sharedPreferences)
    }

    @Singleton
    @Provides
    fun provideAudioNotificator(mediaPlayer: MediaPlayer): AudioNotificator{
        return AudioNotificator(mediaPlayer)
    }

    @Singleton
    @Provides
    fun provideMediaPlayer(): MediaPlayer{
        return MediaPlayer()
    }
}
package xyz.fcr.sberrunner.di.modules

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import xyz.fcr.sberrunner.data.repository.shared.ISharedPreferenceWrapper
import xyz.fcr.sberrunner.data.repository.shared.SharedPreferenceWrapper
import javax.inject.Singleton

/**
 * Модуль приложения, предоставляющий зависимости SharedPreference
 */
@Module
class SharedPreferenceModule {

    /**
     * Предоставление SharedPreferences
     *
     * @param context [Context] - application контекст приложения
     *
     * @return [SharedPreferences] - дефолтный объект SharedPreference
     */
    @Singleton
    @Provides
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    /**
     * Предоставление интерфейса взаимодействия с SharedPreference
     *
     * @param sharedPreferences [SharedPreferences] - дефолтный объект SharedPreference
     *
     * @return [ISharedPreferenceWrapper] - интерфейс взаимодействия с SharedPreference
     */
    @Singleton
    @Provides
    fun provideISharedPreferenceWrapper(sharedPreferences: SharedPreferences): ISharedPreferenceWrapper {
        return SharedPreferenceWrapper(sharedPreferences)
    }
}
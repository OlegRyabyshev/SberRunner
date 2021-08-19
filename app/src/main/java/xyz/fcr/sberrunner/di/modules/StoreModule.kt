package xyz.fcr.sberrunner.di.modules

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import xyz.fcr.sberrunner.utils.SchedulersProvider
import xyz.fcr.sberrunner.utils.SchedulersProviderInterface
import javax.inject.Singleton

@Module
class StoreModule {

    @Provides
    fun provideSharedPreferences(context: Context) : SharedPreferences{
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Provides
    fun provideSchedulersProvider(): SchedulersProviderInterface {
        return SchedulersProvider()
    }
}
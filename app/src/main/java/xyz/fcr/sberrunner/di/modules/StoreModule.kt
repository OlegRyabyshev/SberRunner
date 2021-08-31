package xyz.fcr.sberrunner.di.modules

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import xyz.fcr.sberrunner.data.repository.shared.ISharedPreferenceWrapper
import xyz.fcr.sberrunner.data.repository.shared.SharedPreferenceWrapper
import xyz.fcr.sberrunner.utils.Constants.METRIC_UNIT
import xyz.fcr.sberrunner.utils.Constants.NAME_KEY
import xyz.fcr.sberrunner.utils.Constants.UNITS_KEY
import xyz.fcr.sberrunner.utils.Constants.WEIGHT_KEY
import xyz.fcr.sberrunner.utils.SchedulersProvider
import xyz.fcr.sberrunner.utils.ISchedulersProvider
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
}
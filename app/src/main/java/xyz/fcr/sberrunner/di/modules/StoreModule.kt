package xyz.fcr.sberrunner.di.modules

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import dagger.Module
import dagger.Provides

@Module
class StoreModule {

    @Provides
    fun provideSharedPreferences(context: Context) : SharedPreferences{
        return context.getSharedPreferences("PREFS", AppCompatActivity.MODE_PRIVATE)
    }

}
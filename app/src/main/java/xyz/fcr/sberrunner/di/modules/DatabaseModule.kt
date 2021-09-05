package xyz.fcr.sberrunner.di.modules

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import xyz.fcr.sberrunner.data.room.RunDao
import xyz.fcr.sberrunner.data.room.RunDatabase
import xyz.fcr.sberrunner.domain.db.DatabaseInteractor
import xyz.fcr.sberrunner.domain.db.IDatabaseInteractor
import xyz.fcr.sberrunner.utils.Constants.DB_NAME
import xyz.fcr.sberrunner.utils.ISchedulersProvider
import xyz.fcr.sberrunner.utils.SchedulersProvider
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabaseRepository(runDao: RunDao): IDatabaseInteractor {
        return DatabaseInteractor(runDao)
    }

    @Singleton
    @Provides
    fun provideAppDb(app: Application): RunDatabase {
        return Room.databaseBuilder(app, RunDatabase::class.java, DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideRunDao(db: RunDatabase): RunDao {
        return db.getRunDao()
    }

    @Singleton
    @Provides
    fun provideSchedulersProvider(): ISchedulersProvider {
        return SchedulersProvider()
    }
}
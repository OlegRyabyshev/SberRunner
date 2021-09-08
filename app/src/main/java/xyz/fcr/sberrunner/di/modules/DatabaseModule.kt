package xyz.fcr.sberrunner.di.modules

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import xyz.fcr.sberrunner.data.room.RunDao
import xyz.fcr.sberrunner.data.room.RunDatabase
import xyz.fcr.sberrunner.domain.interactor.db.IDatabaseInteractor
import xyz.fcr.sberrunner.domain.interactor.db.RoomInteractor
import xyz.fcr.sberrunner.utils.Constants.DB_NAME
import javax.inject.Singleton

/**
 * Модуль приложения, предоставляющий зависимости базы данных
 */
@Module
object DatabaseModule {

    /**
     * Предоставление интерактора базы данных для взаимодйствия с ней
     *
     * @param runDao [RunDao] - data access object базы данных
     *
     * @return [IDatabaseInteractor] - интерфейс взаимодействия
     */
    @Singleton
    @Provides
    fun provideDatabaseRepository(runDao: RunDao): IDatabaseInteractor {
        return RoomInteractor(runDao)
    }

    /**
     * Предоставление базы данных
     *
     * @param app [Application] - базовый класс глобального состояния приложения
     *
     * @return [RunDatabase] - база данных
     */
    @Singleton
    @Provides
    fun provideAppDb(app: Application): RunDatabase {
        return Room.databaseBuilder(app, RunDatabase::class.java, DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    /**
     * Предоставление data access object базы данных для взаимодйствия с ней
     *
     * @param database [RunDatabase] - база данных
     *
     * @return [RunDao] - data access object базы данных
     */
    @Singleton
    @Provides
    fun provideRunDao(database: RunDatabase): RunDao {
        return database.getRunDao()
    }
}
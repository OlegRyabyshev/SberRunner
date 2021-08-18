package xyz.fcr.sberrunner.di.modules;

import android.app.Application;

import androidx.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import xyz.fcr.sberrunner.data.room.RunDao;
import xyz.fcr.sberrunner.data.room.RunDatabase;

//@Module
//public class RoomModule {
//
//    private final RunDatabase database;
//
//    public RoomModule(Application application) {
//        this.database = Room.databaseBuilder(
//                application,
//                RunDatabase.class,
//                "sber_runner_table.db"
//        ).build();
//    }
//
//    @Provides
//    @Singleton
//    RunRepository provideListItemRepository(RunDao listItemDao){
//        return new RunRepository(listItemDao);
//    }

//    @Provides
//    @Singleton
//    RunDao provideRunDao(RunDao database){
//        return database.listItemDao();
//    }
//
//    @Provides
//    @Singleton
//    RunDatabase provideRunDatabase(Application application){
//        return database;
//    }
//}
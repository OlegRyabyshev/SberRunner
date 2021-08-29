package xyz.fcr.sberrunner.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import xyz.fcr.sberrunner.data.model.Run
import xyz.fcr.sberrunner.data.util.Converters

@Database(
    entities = [Run::class],
    version = 1,
    exportSchema = false)
@TypeConverters(Converters::class)
abstract class RunDatabase : RoomDatabase() {

    abstract fun getRunDao(): RunDao
}
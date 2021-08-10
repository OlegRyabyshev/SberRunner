package xyz.fcr.sberrunner.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RunEntity::class], version = 1, exportSchema = false)
abstract class RunDatabase : RoomDatabase() {
    abstract fun cityDao(): RunDao
}
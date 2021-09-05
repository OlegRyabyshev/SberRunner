package xyz.fcr.sberrunner.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import xyz.fcr.sberrunner.data.model.RunEntity
import xyz.fcr.sberrunner.data.util.BitmapConverter

/**
 * Абстрактный класс базы данных забегов
 */
@Database(
    entities = [RunEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(BitmapConverter::class)
abstract class RunDatabase : RoomDatabase() {
    abstract fun getRunDao(): RunDao
}
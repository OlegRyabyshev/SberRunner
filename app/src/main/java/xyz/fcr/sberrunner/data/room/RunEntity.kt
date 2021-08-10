package xyz.fcr.sberrunner.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_run_list")
data class RunEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val lat: Double,
    val lon: Double,
)


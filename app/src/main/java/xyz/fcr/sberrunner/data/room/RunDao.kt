package xyz.fcr.sberrunner.data.room

import androidx.room.*

@Dao
interface RunDao {
    @Query("SELECT * FROM table_run_list")
    fun getListOfCities() : List<RunEntity>

    @Update
    fun updateCity(entity: RunEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCity(entity: RunEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addListCity(listCity: List<RunEntity>)

    @Delete
    fun deleteCity(entity: RunEntity)
}
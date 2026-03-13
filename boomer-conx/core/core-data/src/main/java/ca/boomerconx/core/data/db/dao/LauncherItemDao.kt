package ca.boomerconx.core.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ca.boomerconx.core.data.db.entity.LauncherItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LauncherItemDao {
    @Query("SELECT * FROM launcher_items ORDER BY page, position_y, position_x")
    fun getAll(): Flow<List<LauncherItemEntity>>

    @Query("SELECT * FROM launcher_items WHERE page = :page ORDER BY position_y, position_x")
    fun getByPage(page: Int): Flow<List<LauncherItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: LauncherItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<LauncherItemEntity>)

    @Update
    suspend fun update(item: LauncherItemEntity)

    @Delete
    suspend fun delete(item: LauncherItemEntity)

    @Query("DELETE FROM launcher_items")
    suspend fun deleteAll()
}

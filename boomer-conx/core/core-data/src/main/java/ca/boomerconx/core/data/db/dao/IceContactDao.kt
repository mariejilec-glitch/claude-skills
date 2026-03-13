package ca.boomerconx.core.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ca.boomerconx.core.data.db.entity.IceContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IceContactDao {
    @Query("SELECT * FROM ice_contacts ORDER BY priority ASC")
    fun getAll(): Flow<List<IceContactEntity>>

    @Query("SELECT * FROM ice_contacts WHERE id = :id")
    suspend fun getById(id: String): IceContactEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: IceContactEntity)

    @Update
    suspend fun update(contact: IceContactEntity)

    @Delete
    suspend fun delete(contact: IceContactEntity)

    @Query("SELECT COUNT(*) FROM ice_contacts")
    fun getCount(): Flow<Int>
}

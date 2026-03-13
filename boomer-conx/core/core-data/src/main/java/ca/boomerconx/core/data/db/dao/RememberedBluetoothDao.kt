package ca.boomerconx.core.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ca.boomerconx.core.data.db.entity.RememberedBluetoothEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RememberedBluetoothDao {
    @Query("SELECT * FROM remembered_bluetooth ORDER BY device_name ASC")
    fun getAll(): Flow<List<RememberedBluetoothEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(device: RememberedBluetoothEntity)

    @Delete
    suspend fun delete(device: RememberedBluetoothEntity)
}

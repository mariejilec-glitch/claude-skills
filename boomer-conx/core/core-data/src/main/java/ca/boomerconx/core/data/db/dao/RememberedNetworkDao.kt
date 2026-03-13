package ca.boomerconx.core.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ca.boomerconx.core.data.db.entity.RememberedNetworkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RememberedNetworkDao {
    @Query("SELECT * FROM remembered_networks ORDER BY ssid ASC")
    fun getAll(): Flow<List<RememberedNetworkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(network: RememberedNetworkEntity)

    @Delete
    suspend fun delete(network: RememberedNetworkEntity)
}

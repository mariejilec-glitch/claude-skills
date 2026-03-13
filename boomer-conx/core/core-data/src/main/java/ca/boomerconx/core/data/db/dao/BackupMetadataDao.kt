package ca.boomerconx.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ca.boomerconx.core.data.db.entity.BackupMetadataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BackupMetadataDao {
    @Query("SELECT * FROM backup_metadata ORDER BY timestamp DESC")
    fun getAll(): Flow<List<BackupMetadataEntity>>

    @Query("SELECT * FROM backup_metadata WHERE type = :type ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestByType(type: String): BackupMetadataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metadata: BackupMetadataEntity)

    @Query("SELECT MAX(timestamp) FROM backup_metadata")
    fun getLastBackupTimestamp(): Flow<Long?>
}

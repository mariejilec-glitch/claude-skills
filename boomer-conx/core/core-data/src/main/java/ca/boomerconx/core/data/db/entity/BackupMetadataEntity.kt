package ca.boomerconx.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class BackupType { CONTACTS, PHOTOS, VIDEOS, FILES, APPS, LAUNCHER_LAYOUT }

@Entity(tableName = "backup_metadata")
data class BackupMetadataEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val type: String,
    val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "item_count") val itemCount: Int = 0,
    @ColumnInfo(name = "size_bytes") val sizeBytes: Long = 0,
    @ColumnInfo(name = "cloud_synced") val cloudSynced: Boolean = false,
    @ColumnInfo(name = "supabase_storage_path") val supabaseStoragePath: String? = null
)

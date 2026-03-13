package ca.boomerconx.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "launcher_items")
data class LauncherItemEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "package_name") val packageName: String,
    val label: String,
    val page: Int = 0,
    @ColumnInfo(name = "position_x") val positionX: Int = 0,
    @ColumnInfo(name = "position_y") val positionY: Int = 0,
    @ColumnInfo(name = "is_folder") val isFolder: Boolean = false,
    @ColumnInfo(name = "folder_name") val folderName: String? = null
)

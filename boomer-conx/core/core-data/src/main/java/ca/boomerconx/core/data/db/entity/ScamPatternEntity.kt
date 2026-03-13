package ca.boomerconx.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ScamType { SMS, URL, CALLER, EMAIL }

@Entity(tableName = "scam_patterns")
data class ScamPatternEntity(
    @PrimaryKey val id: String,
    val pattern: String,
    val type: String,
    val severity: Int = 5,
    @ColumnInfo(name = "description_fr") val descriptionFr: String,
    @ColumnInfo(name = "description_en") val descriptionEn: String? = null,
    @ColumnInfo(name = "last_updated") val lastUpdated: Long = System.currentTimeMillis()
)

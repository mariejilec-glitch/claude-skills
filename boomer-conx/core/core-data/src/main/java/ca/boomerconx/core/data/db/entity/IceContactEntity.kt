package ca.boomerconx.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "ice_contacts")
data class IceContactEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val phone: String,
    val relationship: String,
    val priority: Int,
    @ColumnInfo(name = "medical_notes") val medicalNotes: String? = null,
    @ColumnInfo(name = "blood_type") val bloodType: String? = null,
    @ColumnInfo(name = "medication_list") val medicationList: String? = null,
    @ColumnInfo(name = "health_card_number") val healthCardNumber: String? = null
)

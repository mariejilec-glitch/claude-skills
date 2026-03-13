package ca.boomerconx.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "remembered_bluetooth")
data class RememberedBluetoothEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "device_name") val deviceName: String,
    @ColumnInfo(name = "device_type") val deviceType: String,
    @ColumnInfo(name = "pairing_instructions") val pairingInstructions: String? = null
)

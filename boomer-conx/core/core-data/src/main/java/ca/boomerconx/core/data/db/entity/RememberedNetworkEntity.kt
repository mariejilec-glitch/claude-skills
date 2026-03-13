package ca.boomerconx.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "remembered_networks")
data class RememberedNetworkEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val ssid: String,
    @ColumnInfo(name = "security_type") val securityType: String,
    val notes: String? = null,
    @ColumnInfo(name = "encrypted_password") val encryptedPassword: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RememberedNetworkEntity) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

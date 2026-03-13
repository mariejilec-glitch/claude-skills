package ca.boomerconx.core.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "credentials")
data class CredentialEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "site_name") val siteName: String,
    val url: String? = null,
    val username: String,
    @ColumnInfo(name = "encrypted_password") val encryptedPassword: ByteArray,
    val notes: String? = null,
    val category: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CredentialEntity) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

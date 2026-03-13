package ca.boomerconx.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ca.boomerconx.core.data.db.dao.BackupMetadataDao
import ca.boomerconx.core.data.db.dao.CredentialDao
import ca.boomerconx.core.data.db.dao.IceContactDao
import ca.boomerconx.core.data.db.dao.LauncherItemDao
import ca.boomerconx.core.data.db.dao.RememberedBluetoothDao
import ca.boomerconx.core.data.db.dao.RememberedNetworkDao
import ca.boomerconx.core.data.db.dao.ScamPatternDao
import ca.boomerconx.core.data.db.entity.BackupMetadataEntity
import ca.boomerconx.core.data.db.entity.CredentialEntity
import ca.boomerconx.core.data.db.entity.IceContactEntity
import ca.boomerconx.core.data.db.entity.LauncherItemEntity
import ca.boomerconx.core.data.db.entity.RememberedBluetoothEntity
import ca.boomerconx.core.data.db.entity.RememberedNetworkEntity
import ca.boomerconx.core.data.db.entity.ScamPatternEntity

@Database(
    entities = [
        CredentialEntity::class,
        IceContactEntity::class,
        BackupMetadataEntity::class,
        LauncherItemEntity::class,
        ScamPatternEntity::class,
        RememberedNetworkEntity::class,
        RememberedBluetoothEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class BoomerDatabase : RoomDatabase() {
    abstract fun credentialDao(): CredentialDao
    abstract fun iceContactDao(): IceContactDao
    abstract fun backupMetadataDao(): BackupMetadataDao
    abstract fun launcherItemDao(): LauncherItemDao
    abstract fun scamPatternDao(): ScamPatternDao
    abstract fun rememberedNetworkDao(): RememberedNetworkDao
    abstract fun rememberedBluetoothDao(): RememberedBluetoothDao

    companion object {
        const val DATABASE_NAME = "boomerconx_db"
    }
}

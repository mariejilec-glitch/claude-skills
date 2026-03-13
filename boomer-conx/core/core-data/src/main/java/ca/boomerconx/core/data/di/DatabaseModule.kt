package ca.boomerconx.core.data.di

import android.content.Context
import androidx.room.Room
import ca.boomerconx.core.data.db.BoomerDatabase
import ca.boomerconx.core.data.db.dao.BackupMetadataDao
import ca.boomerconx.core.data.db.dao.CredentialDao
import ca.boomerconx.core.data.db.dao.IceContactDao
import ca.boomerconx.core.data.db.dao.LauncherItemDao
import ca.boomerconx.core.data.db.dao.RememberedBluetoothDao
import ca.boomerconx.core.data.db.dao.RememberedNetworkDao
import ca.boomerconx.core.data.db.dao.ScamPatternDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BoomerDatabase {
        return Room.databaseBuilder(
            context,
            BoomerDatabase::class.java,
            BoomerDatabase.DATABASE_NAME
        ).build()
    }

    @Provides fun provideCredentialDao(db: BoomerDatabase): CredentialDao = db.credentialDao()
    @Provides fun provideIceContactDao(db: BoomerDatabase): IceContactDao = db.iceContactDao()
    @Provides fun provideBackupMetadataDao(db: BoomerDatabase): BackupMetadataDao = db.backupMetadataDao()
    @Provides fun provideLauncherItemDao(db: BoomerDatabase): LauncherItemDao = db.launcherItemDao()
    @Provides fun provideScamPatternDao(db: BoomerDatabase): ScamPatternDao = db.scamPatternDao()
    @Provides fun provideRememberedNetworkDao(db: BoomerDatabase): RememberedNetworkDao = db.rememberedNetworkDao()
    @Provides fun provideRememberedBluetoothDao(db: BoomerDatabase): RememberedBluetoothDao = db.rememberedBluetoothDao()
}

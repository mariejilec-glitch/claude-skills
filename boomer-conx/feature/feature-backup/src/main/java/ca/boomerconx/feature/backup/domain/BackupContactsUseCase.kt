package ca.boomerconx.feature.backup.domain

import ca.boomerconx.core.data.db.dao.BackupMetadataDao
import ca.boomerconx.core.data.db.entity.BackupMetadataEntity
import ca.boomerconx.core.data.db.entity.BackupType
import ca.boomerconx.core.data.supabase.SupabaseSync
import ca.boomerconx.core.security.CryptoManager
import ca.boomerconx.feature.backup.data.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BackupContactsUseCase @Inject constructor(
    private val contactRepository: ContactRepository,
    private val cryptoManager: CryptoManager,
    private val supabaseSync: SupabaseSync,
    private val backupMetadataDao: BackupMetadataDao
) {
    suspend operator fun invoke(): Result<BackupMetadataEntity> = withContext(Dispatchers.IO) {
        try {
            val contacts = contactRepository.readAllContacts()
            val vcfBytes = contactRepository.serializeToVcf(contacts)
            val encrypted = cryptoManager.encrypt(vcfBytes)

            val storagePath = try {
                supabaseSync.uploadBackup("contacts", encrypted)
            } catch (_: Exception) {
                null // Offline mode - local only
            }

            val metadata = BackupMetadataEntity(
                type = BackupType.CONTACTS.name,
                itemCount = contacts.size,
                sizeBytes = encrypted.size.toLong(),
                cloudSynced = storagePath != null,
                supabaseStoragePath = storagePath
            )
            backupMetadataDao.insert(metadata)
            Result.success(metadata)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

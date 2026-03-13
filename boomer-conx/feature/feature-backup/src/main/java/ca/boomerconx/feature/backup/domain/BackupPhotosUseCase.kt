package ca.boomerconx.feature.backup.domain

import ca.boomerconx.core.data.db.dao.BackupMetadataDao
import ca.boomerconx.core.data.db.entity.BackupMetadataEntity
import ca.boomerconx.core.data.db.entity.BackupType
import ca.boomerconx.feature.backup.data.MediaRepository
import javax.inject.Inject

class BackupPhotosUseCase @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val backupMetadataDao: BackupMetadataDao
) {
    suspend operator fun invoke(): Result<BackupMetadataEntity> {
        return try {
            val photos = mediaRepository.getPhotoPaths()
            val metadata = BackupMetadataEntity(
                type = BackupType.PHOTOS.name,
                itemCount = photos.size,
                sizeBytes = 0,
                cloudSynced = false
            )
            backupMetadataDao.insert(metadata)
            Result.success(metadata)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

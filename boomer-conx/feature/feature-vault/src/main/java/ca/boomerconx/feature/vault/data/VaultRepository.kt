package ca.boomerconx.feature.vault.data

import ca.boomerconx.core.data.db.dao.CredentialDao
import ca.boomerconx.core.data.db.entity.CredentialEntity
import ca.boomerconx.core.security.CryptoManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VaultRepository @Inject constructor(
    private val credentialDao: CredentialDao,
    private val cryptoManager: CryptoManager
) {
    fun getAllCredentials(): Flow<List<CredentialEntity>> = credentialDao.getAll()

    fun searchCredentials(query: String): Flow<List<CredentialEntity>> = credentialDao.search(query)

    fun getByCategory(category: String): Flow<List<CredentialEntity>> =
        credentialDao.getByCategory(category)

    suspend fun getById(id: String): CredentialEntity? = credentialDao.getById(id)

    suspend fun findByDomain(domain: String): List<CredentialEntity> =
        credentialDao.findByDomain(domain)

    suspend fun saveCredential(
        siteName: String,
        url: String?,
        username: String,
        password: String,
        notes: String?,
        category: String?
    ) {
        val encrypted = cryptoManager.encryptString(password)
        val entity = CredentialEntity(
            siteName = siteName,
            url = url,
            username = username,
            encryptedPassword = encrypted,
            notes = notes,
            category = category
        )
        credentialDao.insert(entity)
    }

    suspend fun updateCredential(
        existing: CredentialEntity,
        password: String? = null
    ): CredentialEntity {
        val updated = if (password != null) {
            existing.copy(
                encryptedPassword = cryptoManager.encryptString(password),
                updatedAt = System.currentTimeMillis()
            )
        } else {
            existing.copy(updatedAt = System.currentTimeMillis())
        }
        credentialDao.update(updated)
        return updated
    }

    suspend fun deleteCredential(credential: CredentialEntity) {
        credentialDao.delete(credential)
    }

    fun decryptPassword(credential: CredentialEntity): String {
        return cryptoManager.decryptToString(credential.encryptedPassword)
    }

    fun getCount(): Flow<Int> = credentialDao.getCount()
}

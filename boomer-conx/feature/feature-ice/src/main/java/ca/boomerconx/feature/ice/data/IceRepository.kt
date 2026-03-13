package ca.boomerconx.feature.ice.data

import ca.boomerconx.core.data.db.dao.IceContactDao
import ca.boomerconx.core.data.db.entity.IceContactEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IceRepository @Inject constructor(
    private val iceContactDao: IceContactDao
) {
    fun getAllContacts(): Flow<List<IceContactEntity>> = iceContactDao.getAll()

    suspend fun getById(id: String): IceContactEntity? = iceContactDao.getById(id)

    suspend fun saveContact(contact: IceContactEntity) = iceContactDao.insert(contact)

    suspend fun updateContact(contact: IceContactEntity) = iceContactDao.update(contact)

    suspend fun deleteContact(contact: IceContactEntity) = iceContactDao.delete(contact)

    fun getContactCount(): Flow<Int> = iceContactDao.getCount()
}

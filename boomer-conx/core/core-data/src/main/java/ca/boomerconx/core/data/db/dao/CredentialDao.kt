package ca.boomerconx.core.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ca.boomerconx.core.data.db.entity.CredentialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CredentialDao {
    @Query("SELECT * FROM credentials ORDER BY site_name ASC")
    fun getAll(): Flow<List<CredentialEntity>>

    @Query("SELECT * FROM credentials WHERE id = :id")
    suspend fun getById(id: String): CredentialEntity?

    @Query("SELECT * FROM credentials WHERE category = :category ORDER BY site_name ASC")
    fun getByCategory(category: String): Flow<List<CredentialEntity>>

    @Query("SELECT * FROM credentials WHERE site_name LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<CredentialEntity>>

    @Query("SELECT * FROM credentials WHERE url LIKE '%' || :domain || '%'")
    suspend fun findByDomain(domain: String): List<CredentialEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(credential: CredentialEntity)

    @Update
    suspend fun update(credential: CredentialEntity)

    @Delete
    suspend fun delete(credential: CredentialEntity)

    @Query("SELECT COUNT(*) FROM credentials")
    fun getCount(): Flow<Int>
}

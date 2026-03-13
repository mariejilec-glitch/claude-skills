package ca.boomerconx.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ca.boomerconx.core.data.db.entity.ScamPatternEntity

@Dao
interface ScamPatternDao {
    @Query("SELECT * FROM scam_patterns WHERE type = :type")
    suspend fun getByType(type: String): List<ScamPatternEntity>

    @Query("SELECT * FROM scam_patterns")
    suspend fun getAll(): List<ScamPatternEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(patterns: List<ScamPatternEntity>)

    @Query("DELETE FROM scam_patterns")
    suspend fun deleteAll()
}

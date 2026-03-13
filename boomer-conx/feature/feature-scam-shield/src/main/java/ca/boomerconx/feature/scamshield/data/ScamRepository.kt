package ca.boomerconx.feature.scamshield.data

import ca.boomerconx.core.data.db.dao.ScamPatternDao
import ca.boomerconx.core.data.db.entity.ScamPatternEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScamRepository @Inject constructor(
    private val scamPatternDao: ScamPatternDao
) {
    suspend fun getPatternsByType(type: String): List<ScamPatternEntity> =
        scamPatternDao.getByType(type)

    suspend fun getAllPatterns(): List<ScamPatternEntity> = scamPatternDao.getAll()

    suspend fun updatePatterns(patterns: List<ScamPatternEntity>) {
        scamPatternDao.deleteAll()
        scamPatternDao.insertAll(patterns)
    }

    fun analyzeSms(message: String, patterns: List<ScamPatternEntity>): ScamAnalysisResult {
        val normalizedMessage = message.lowercase()
        val matchedPatterns = patterns.filter { pattern ->
            try {
                Regex(pattern.pattern, RegexOption.IGNORE_CASE).containsMatchIn(normalizedMessage)
            } catch (_: Exception) {
                normalizedMessage.contains(pattern.pattern.lowercase())
            }
        }

        return if (matchedPatterns.isEmpty()) {
            ScamAnalysisResult(isScam = false, severity = 0, reasons = emptyList())
        } else {
            val maxSeverity = matchedPatterns.maxOf { it.severity }
            ScamAnalysisResult(
                isScam = maxSeverity >= 5,
                severity = maxSeverity,
                reasons = matchedPatterns.map { it.descriptionFr }
            )
        }
    }
}

data class ScamAnalysisResult(
    val isScam: Boolean,
    val severity: Int,
    val reasons: List<String>
)

package ca.boomerconx.feature.scamshield.domain

import ca.boomerconx.core.data.db.entity.ScamType
import ca.boomerconx.feature.scamshield.data.ScamRepository
import javax.inject.Inject

data class UrlCheckResult(
    val safe: Boolean,
    val reason: String?,
    val severity: Int
)

class CheckUrlUseCase @Inject constructor(
    private val scamRepository: ScamRepository
) {
    suspend operator fun invoke(url: String): UrlCheckResult {
        val patterns = scamRepository.getPatternsByType(ScamType.URL.name)
        val normalizedUrl = url.lowercase()

        val match = patterns.firstOrNull { pattern ->
            try {
                Regex(pattern.pattern, RegexOption.IGNORE_CASE).containsMatchIn(normalizedUrl)
            } catch (_: Exception) {
                normalizedUrl.contains(pattern.pattern.lowercase())
            }
        }

        return if (match != null) {
            UrlCheckResult(safe = false, reason = match.descriptionFr, severity = match.severity)
        } else {
            UrlCheckResult(safe = true, reason = null, severity = 0)
        }
    }
}

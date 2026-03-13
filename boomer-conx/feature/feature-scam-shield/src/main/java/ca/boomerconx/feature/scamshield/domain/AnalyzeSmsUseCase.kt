package ca.boomerconx.feature.scamshield.domain

import ca.boomerconx.core.data.db.entity.ScamType
import ca.boomerconx.feature.scamshield.data.ScamAnalysisResult
import ca.boomerconx.feature.scamshield.data.ScamRepository
import javax.inject.Inject

class AnalyzeSmsUseCase @Inject constructor(
    private val scamRepository: ScamRepository
) {
    suspend operator fun invoke(message: String): ScamAnalysisResult {
        val patterns = scamRepository.getPatternsByType(ScamType.SMS.name)
        return scamRepository.analyzeSms(message, patterns)
    }
}

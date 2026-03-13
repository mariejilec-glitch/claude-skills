package ca.boomerconx.feature.accessibility.data

import ca.boomerconx.core.data.prefs.UserPreferences
import ca.boomerconx.core.ui.theme.AccessibilityState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccessibilityPreferences @Inject constructor(
    private val userPreferences: UserPreferences
) {
    val accessibilityState: Flow<AccessibilityState> = combine(
        userPreferences.fontScale,
        userPreferences.highContrast,
        userPreferences.reducedMotion
    ) { fontScale, highContrast, reducedMotion ->
        AccessibilityState(
            fontScale = fontScale,
            highContrast = highContrast,
            reducedMotion = reducedMotion
        )
    }

    suspend fun setFontScale(scale: Float) = userPreferences.setFontScale(scale)
    suspend fun setHighContrast(enabled: Boolean) = userPreferences.setHighContrast(enabled)
    suspend fun setReducedMotion(enabled: Boolean) = userPreferences.setReducedMotion(enabled)
}

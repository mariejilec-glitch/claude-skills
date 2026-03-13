package ca.boomerconx.core.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

data class AccessibilityState(
    val fontScale: Float = 1.0f,
    val highContrast: Boolean = false,
    val reducedMotion: Boolean = false
) {
    fun scaledFontSize(baseSize: TextUnit): TextUnit =
        (baseSize.value * fontScale).sp
}

val LocalAccessibilityState = compositionLocalOf { AccessibilityState() }

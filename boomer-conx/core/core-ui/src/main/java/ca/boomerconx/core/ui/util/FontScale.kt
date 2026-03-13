package ca.boomerconx.core.ui.util

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

object FontScale {
    const val SMALL = 0.85f
    const val NORMAL = 1.0f
    const val LARGE = 1.3f
    const val EXTRA_LARGE = 1.6f
    const val MAXIMUM = 2.0f

    val SCALE_OPTIONS = listOf(SMALL, NORMAL, LARGE, EXTRA_LARGE, MAXIMUM)

    fun scale(baseSize: TextUnit, scaleFactor: Float): TextUnit =
        (baseSize.value * scaleFactor).sp

    fun labelForScale(scale: Float): String = when (scale) {
        SMALL -> "Petit"
        NORMAL -> "Normal"
        LARGE -> "Grand"
        EXTRA_LARGE -> "Très grand"
        MAXIMUM -> "Maximum"
        else -> "Personnalisé"
    }
}

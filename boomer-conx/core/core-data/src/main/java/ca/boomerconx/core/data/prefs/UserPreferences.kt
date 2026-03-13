package ca.boomerconx.core.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val onboardedKey = booleanPreferencesKey("is_onboarded")
    private val languageKey = stringPreferencesKey("language")
    private val fontScaleKey = floatPreferencesKey("font_scale")
    private val highContrastKey = booleanPreferencesKey("high_contrast")
    private val reducedMotionKey = booleanPreferencesKey("reduced_motion")
    private val iceLockScreenKey = booleanPreferencesKey("ice_lock_screen")
    private val smsProtectionKey = booleanPreferencesKey("sms_protection")
    private val callProtectionKey = booleanPreferencesKey("call_protection")

    val isOnboarded: Flow<Boolean> = context.dataStore.data.map { it[onboardedKey] ?: false }
    val language: Flow<String> = context.dataStore.data.map { it[languageKey] ?: "fr-CA" }
    val fontScale: Flow<Float> = context.dataStore.data.map { it[fontScaleKey] ?: 1.0f }
    val highContrast: Flow<Boolean> = context.dataStore.data.map { it[highContrastKey] ?: false }
    val reducedMotion: Flow<Boolean> = context.dataStore.data.map { it[reducedMotionKey] ?: false }
    val iceLockScreen: Flow<Boolean> = context.dataStore.data.map { it[iceLockScreenKey] ?: true }
    val smsProtection: Flow<Boolean> = context.dataStore.data.map { it[smsProtectionKey] ?: true }
    val callProtection: Flow<Boolean> = context.dataStore.data.map { it[callProtectionKey] ?: true }

    suspend fun setOnboarded(value: Boolean) {
        context.dataStore.edit { it[onboardedKey] = value }
    }

    suspend fun setLanguage(value: String) {
        context.dataStore.edit { it[languageKey] = value }
    }

    suspend fun setFontScale(value: Float) {
        context.dataStore.edit { it[fontScaleKey] = value }
    }

    suspend fun setHighContrast(value: Boolean) {
        context.dataStore.edit { it[highContrastKey] = value }
    }

    suspend fun setReducedMotion(value: Boolean) {
        context.dataStore.edit { it[reducedMotionKey] = value }
    }

    suspend fun setIceLockScreen(value: Boolean) {
        context.dataStore.edit { it[iceLockScreenKey] = value }
    }

    suspend fun setSmsProtection(value: Boolean) {
        context.dataStore.edit { it[smsProtectionKey] = value }
    }

    suspend fun setCallProtection(value: Boolean) {
        context.dataStore.edit { it[callProtectionKey] = value }
    }
}

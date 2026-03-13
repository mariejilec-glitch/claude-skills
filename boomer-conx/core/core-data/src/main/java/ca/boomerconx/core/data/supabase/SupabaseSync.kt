package ca.boomerconx.core.data.supabase

import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.storage.storage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseSync @Inject constructor(
    private val supabaseClient: BoomerSupabaseClient
) {
    private val client get() = supabaseClient.client

    suspend fun uploadBackup(type: String, data: ByteArray): String {
        val userId = client.auth.currentUserOrNull()?.id
            ?: throw IllegalStateException("User not authenticated")
        val path = "$userId/$type/${System.currentTimeMillis()}.enc"
        client.storage.from("backups").upload(path, data)
        return path
    }

    suspend fun downloadBackup(path: String): ByteArray {
        return client.storage.from("backups").downloadAuthenticated(path)
    }

    suspend fun isAuthenticated(): Boolean {
        return client.auth.currentUserOrNull() != null
    }

    suspend fun signIn(email: String, password: String) {
        client.auth.signInWith(io.github.jan.supabase.gotrue.providers.builtin.Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signUp(email: String, password: String) {
        client.auth.signUpWith(io.github.jan.supabase.gotrue.providers.builtin.Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signOut() {
        client.auth.signOut()
    }
}

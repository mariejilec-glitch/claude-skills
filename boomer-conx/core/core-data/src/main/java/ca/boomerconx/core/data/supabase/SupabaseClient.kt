package ca.boomerconx.core.data.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoomerSupabaseClient @Inject constructor() {

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
    }

    companion object {
        // These should be loaded from BuildConfig in production
        private const val SUPABASE_URL = "https://your-project.supabase.co"
        private const val SUPABASE_ANON_KEY = "your-anon-key"
    }
}

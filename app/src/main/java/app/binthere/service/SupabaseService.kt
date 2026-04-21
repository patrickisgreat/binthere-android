package app.binthere.service

import io.github.jan.supabase.SupabaseClient
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thin wrapper around [SupabaseClient]. Keeps the rest of the app decoupled
 * from the SDK so future backend swaps touch one file.
 */
@Singleton
class SupabaseService
    @Inject
    constructor(
        val client: SupabaseClient,
    )

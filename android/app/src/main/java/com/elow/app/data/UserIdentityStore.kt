package com.elow.app.data

import android.content.Context
import java.util.UUID

class UserIdentityStore(context: Context) {
    private val preferences = context.getSharedPreferences("elow_identity", Context.MODE_PRIVATE)

    fun userId(): String {
        val existing = preferences.getString(KEY_USER_ID, null)
        if (!existing.isNullOrBlank()) return existing

        val created = "local-${UUID.randomUUID()}"
        preferences.edit().putString(KEY_USER_ID, created).apply()
        return created
    }

    private companion object {
        const val KEY_USER_ID = "user_id"
    }
}


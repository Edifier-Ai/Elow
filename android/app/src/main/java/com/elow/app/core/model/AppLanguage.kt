package com.elow.app.core.model

enum class AppLanguage(val code: String) {
    ENGLISH("en"),
    CHINESE("zh");

    companion object {
        fun fromCode(code: String?): AppLanguage =
            entries.firstOrNull { it.code == code } ?: ENGLISH
    }
}

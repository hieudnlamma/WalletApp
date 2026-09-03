package com.lmt.global.base.model

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.annotation.DrawableRes
import com.lmt.global.base.R
import java.util.Locale

enum class Language(
    val id: Int,
    val country: String,
    val isoCountry: String,
    @DrawableRes
    val iconFlag: Int
) {
    ENGLISH(1, "English", "en", R.drawable.ic_english),
    CZECH(2, "Czech", "cs", R.drawable.ic_czech_republic),
    GERMAN(3, "German", "de", R.drawable.ic_german),
    SPANISH(4, "Spanish", "es", R.drawable.ic_spanish),
    FILIPINO(5, "Filipino", "fil", R.drawable.ic_filipino),
    FRENCH(6, "French", "fr", R.drawable.ic_france),
    HINDI(7, "Hindi", "hi", R.drawable.ic_hindi),
    CROATIAN(8, "Croatian", "hr", R.drawable.ic_croatia),
    INDONESIAN(9, "Indonesian", "in", R.drawable.ic_indonesian),
    ITALIAN(10, "Italian", "it", R.drawable.ic_italian),
    JAPANESE(11, "Japanese", "ja", R.drawable.ic_japanese),
    KOREAN(12, "Korean", "ko", R.drawable.ic_korean),
    MALAY(13, "Malay", "ms", R.drawable.ic_malay),
    DUTCH(14, "Dutch", "nl", R.drawable.ic_dutch),
    POLISH(15, "Polish", "pl", R.drawable.ic_polish),
    PORTUGUESE(16, "Portuguese", "pt", R.drawable.ic_portugal),
    RUSSIAN(17, "Russian", "ru", R.drawable.ic_russian),
    SERBIAN(18, "Serbian", "sr", R.drawable.ic_serbian),
    SWEDISH(19, "Swedish", "sv", R.drawable.ic_swedish),
    TURKISH(20, "Turkish", "tr", R.drawable.ic_turkish),
    VIETNAMESE(21, "Vietnamese", "vi", R.drawable.ic_vietnamese);

    companion object {
        fun getAllLanguages(): List<Language> {
            val languages = Language.entries
            val currentLanguage = Resources.getSystem().configuration.locales[0].language
            if (languages.any { it.isoCountry == currentLanguage }) {
                val (matching, others) = languages.partition { it.isoCountry == currentLanguage }
                return matching + others
            }
            return languages
        }
    }

    @Suppress("DEPRECATION")
    fun applyLanguage(context: Context) {
        val newLocale = Locale(isoCountry)
        Locale.setDefault(newLocale)
        val configuration = Configuration()
        configuration.setLocale(newLocale)
        context.resources.updateConfiguration(
            configuration, context.resources.displayMetrics
        )
    }
}

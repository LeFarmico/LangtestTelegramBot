package repository

import entity.Language

interface LanguageRepository {

    suspend fun getAvailableLanguages(): List<Language>

    suspend fun getLanguageById(languageId: Long): Language?
}

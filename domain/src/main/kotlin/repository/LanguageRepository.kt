package repository

import entity.Language
import state.DataState

interface LanguageRepository {

    suspend fun getAvailableLanguages(): DataState<List<Language>>

    suspend fun getLanguageById(languageId: Long): DataState<Language>
}

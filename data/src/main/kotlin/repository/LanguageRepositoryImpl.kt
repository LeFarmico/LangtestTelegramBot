package repository

import dataSource.WordsDataSource
import entity.Language

class LanguageRepositoryImpl(
    private val wordsDataSource: WordsDataSource
) : LanguageRepository {

    override suspend fun getAvailableLanguages(): List<Language> {
        return wordsDataSource.getLanguages()
    }

    override suspend fun getLanguageById(languageId: Long): Language? {
        return wordsDataSource.getLanguage(languageId)
    }
}

package repository

import entity.Language

interface LanguageRepository {

    fun getAvailableLanguages(): List<Language>
}
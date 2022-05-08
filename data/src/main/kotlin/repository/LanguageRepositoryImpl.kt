package repository

import entity.Language
import http.LangTestApi
import state.DataState

class LanguageRepositoryImpl(
    private val langTestApi: LangTestApi
) : LanguageRepository {

    override suspend fun getAvailableLanguages(): DataState<List<Language>> {
        return try {
            val callback = langTestApi.getAvailableLanguages()
            val response = callback.execute()
            if (response.body() == null) {
                DataState.Empty
            } else {
                DataState.Success(response.body()!!)
            }
        } catch (e: Exception) {
            DataState.Failure(e)
        }
    }

    override suspend fun getLanguageById(languageId: Long): DataState<Language> {
        return try {
            val callback = langTestApi.getLanguageById(languageId)
            val response = callback.execute()
            if (response.body() == null) {
                DataState.Empty
            } else {
                DataState.Success(response.body()!!)
            }
        } catch (e: Exception) {
            DataState.Failure(e)
        }
    }
}

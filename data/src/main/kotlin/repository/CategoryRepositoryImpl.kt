package repository

import entity.Category
import http.LangTestApi
import state.DataState

class CategoryRepositoryImpl(
    private val langTestApi: LangTestApi
) : CategoryRepository {

    override suspend fun getCategoriesByLanguage(languageId: Long): DataState<List<Category>> {
        return try {
            val response = langTestApi.getAvailableCategories(languageId).execute()
            if (response.body() == null) {
                DataState.Empty
            } else {
                DataState.Success(response.body()!!)
            }
        } catch (e: Exception) {
            DataState.Failure(e)
        }
    }

    override suspend fun getCategory(categoryId: Long): DataState<Category> {
        return try {
            val response = langTestApi.getCategoryById(categoryId).execute()
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

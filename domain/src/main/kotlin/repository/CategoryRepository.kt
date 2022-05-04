package repository

import entity.Category
import state.DataState

interface CategoryRepository {
    suspend fun getCategoriesByLanguage(languageId: Long): DataState<List<Category>>

    suspend fun getCategory(categoryId: Long): DataState<Category>
}

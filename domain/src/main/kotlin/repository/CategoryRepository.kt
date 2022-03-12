package repository

import entity.Category

interface CategoryRepository {
    suspend fun getCategoriesByLanguage(languageId: Long): List<Category>

    suspend fun getCategory(categoryId: Long): Category?
}

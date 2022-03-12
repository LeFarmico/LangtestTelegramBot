package repository

import dataSource.WordsDataSource
import entity.Category

class CategoryRepositoryImpl(
    private val wordsDataSource: WordsDataSource
) : CategoryRepository {

    override suspend fun getCategoriesByLanguage(languageId: Long): List<Category> {
        return wordsDataSource.getCategories(languageId)
    }

    override suspend fun getCategory(categoryId: Long): Category? {
        return wordsDataSource.getCategory(categoryId)
    }
}

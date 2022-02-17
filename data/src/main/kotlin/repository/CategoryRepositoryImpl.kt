package repository

import entity.Category

class CategoryRepositoryImpl : CategoryRepository {

    override suspend fun getCategories(): List<Category> {
        return listOf(
        Category(1, "default"),
        Category(2, "shop"),
        Category(3, "travel")
        )
    }

}
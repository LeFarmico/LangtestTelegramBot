package repository

import entity.Category

interface CategoryRepository {
    suspend fun getCategories(): List<Category>
}

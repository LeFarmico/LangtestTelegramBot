package entity

data class User(val id: Long = 0, val chatId: Long, val categoryId: Long = 1, val breakTimeInMillis: Long = 3600000)

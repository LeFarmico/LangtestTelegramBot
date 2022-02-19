package inject

import dataSource.UserDataSource
import dataSource.WordsDataSource
import repository.UserRepository
import repository.UserRepositoryImpl
import repository.WordsRepository
import repository.WordsRepositoryImpl

object DataInjector {

    private val userDataSource = UserDataSource()
    private val wordsDataSource = WordsDataSource()

    val userRepo: UserRepository = UserRepositoryImpl(userDataSource)
    val wordsRepo: WordsRepository = WordsRepositoryImpl(wordsDataSource, userDataSource)
}

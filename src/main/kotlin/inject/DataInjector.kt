package inject

import dataSource.QuizWordsDataSource
import dataSource.UserDataSource
import dataSource.WordsDataSource
import repository.*

object DataInjector {

    private val userDataSource = UserDataSource()
    private val wordsDataSource = WordsDataSource()
    private val quizWordsDataSource = QuizWordsDataSource()

    val userRepo: UserRepository = UserRepositoryImpl(userDataSource)
    val wordsRepo: WordsRepository = WordsRepositoryImpl(wordsDataSource)
    val categoryRepository: CategoryRepository = CategoryRepositoryImpl(wordsDataSource)
    val languageRepository: LanguageRepository = LanguageRepositoryImpl(wordsDataSource)
    val quizRepository: QuizRepository = QuizRepositoryImpl(
        userDataSource,
        quizWordsDataSource,
        wordsDataSource
    )
}

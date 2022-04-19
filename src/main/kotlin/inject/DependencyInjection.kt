package inject

import dataSource.QuizWordsDataSource
import dataSource.UserDataSource
import dataSource.WordsDataSource
import org.koin.dsl.module
import repository.*

object DependencyInjection {
    val module = module {
        single<UserRepository> { UserRepositoryImpl(get()) }
        single<CategoryRepository> { CategoryRepositoryImpl(get()) }
        single<LanguageRepository> { LanguageRepositoryImpl(get()) }
        single<QuizRepository> { QuizRepositoryImpl(get(), get(), get()) }

        single { UserDataSource() }
        single { WordsDataSource() }
        single { QuizWordsDataSource() }
    }

    private val userDataSource = UserDataSource()
    private val wordsDataSource = WordsDataSource()
    private val quizWordsDataSource = QuizWordsDataSource()

//    val wordsRepo: WordsRepository = WordsRepositoryImpl(wordsDataSource)
    val userRepo: UserRepository = UserRepositoryImpl(userDataSource)
    val categoryRepository: CategoryRepository = CategoryRepositoryImpl(wordsDataSource)
    val languageRepository: LanguageRepository = LanguageRepositoryImpl(wordsDataSource)
    val quizRepository: QuizRepository = QuizRepositoryImpl(
        userDataSource,
        quizWordsDataSource,
        wordsDataSource
    )
}

package di

import org.koin.dsl.module
import repository.*

object AppDi {
    val module = module {
        single<UserRepository> { UserRepositoryImpl(get()) }
        single<CategoryRepository> { CategoryRepositoryImpl(get()) }
        single<LanguageRepository> { LanguageRepositoryImpl(get()) }
        single<QuizRepository> { QuizRepositoryImpl(get()) }

//        single { UserDataSource() }
//        single { WordsDataSource() }
    }
}

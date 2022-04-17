package interactor

interface ILangDataInteractor {

    fun getLanguages()

    fun getCategoriesByLanguageId(languageId: Long)
}

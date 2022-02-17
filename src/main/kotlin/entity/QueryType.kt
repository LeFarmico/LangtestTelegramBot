package entity

sealed class QueryType {
    
    companion object {
        fun getQueryType(queryText: String): QueryType {
            return when (queryText.trim().lowercase()) {
                "langtestright" -> LangTestQuery.Right
                "langtestwrong" -> LangTestQuery.Wrong
                "langtestfinish" -> LangTestQuery.Finish
                else -> None
            }
        }
    }
    object None : QueryType()

    sealed class LangTestQuery {
        object Right : QueryType()
        object Wrong : QueryType()
        object Finish : QueryType()
    }
}

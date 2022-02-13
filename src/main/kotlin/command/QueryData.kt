package command

data class QueryData(val textQuery: String)

sealed class QueryType {
    
    companion object {
        fun getQueryType(queryText: String): QueryType {
            return when (queryText.trim().lowercase()) {
                "right" -> Right
                "wrong" -> Wrong
                "finish" -> Finish
                else -> None
            }
        }
    }
    
    object Right : QueryType()
    object Wrong : QueryType()
    object Finish : QueryType()
    object None : QueryType()
}

package entity

import ability.langTestAbility.LangTestAbility

sealed class AbilityActionType {
    
    companion object {
        fun getQueryType(queryText: String): AbilityActionType {
            return when (queryText.trim().lowercase()) {
                LangTestAbility.RIGHT_ANSWER -> LangTestQuery.Right
                LangTestAbility.WRONG_ANSWER -> LangTestQuery.Wrong
                LangTestAbility.FINISH -> LangTestQuery.Finish
                else -> None
            }
        }
    }
    object None : AbilityActionType()

    sealed class LangTestQuery {
        object Right : AbilityActionType()
        object Wrong : AbilityActionType()
        object Finish : AbilityActionType()
    }
}

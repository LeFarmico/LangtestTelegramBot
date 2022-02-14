package ability

abstract class AbstractAbility(private val chatId: Long) {

    protected var abilityState = AbilityState.DEFAULT

    open fun start() {
        abilityState = AbilityState.STARTED
    }

    fun getState(): AbilityState = abilityState

    abstract fun action(actionData: Any? = null)
    
    open fun finish() {
        abilityState = AbilityState.FINISHED
    }
}

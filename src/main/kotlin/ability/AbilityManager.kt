package ability

import ability.langTestAbility.AbilityCommand
import org.slf4j.LoggerFactory

class AbilityManager : IAbilityManager {

    private val lock = Any()
    private val log = LoggerFactory.getLogger(javaClass.simpleName)
    private val abilityMap: MutableMap<Class<out IAbility<out AbilityCommand>>, IAbility<out AbilityCommand>> = mutableMapOf()

    override fun addAbility(
        abilityClass: Class<out IAbility<out AbilityCommand>>,
        ability: IAbility<out AbilityCommand>
    ) {
        synchronized(lock) {
            if (abilityMap[abilityClass] == null) {
                log.info("[INFO] ${ability.javaClass.simpleName} added to Ability Manager")
                abilityMap[abilityClass] = ability
            } else {
                log.warn("[WARN] ${ability.javaClass.simpleName} already exist.")
            }
        }
    }

    override fun removeAbility(abilityClass: Class<out IAbility<out AbilityCommand>>) {
        try {
            synchronized(lock) {
                abilityMap.remove(abilityClass)
            }
            log.info("[INFO] ${abilityClass.javaClass.simpleName} has removed for.")
        } catch (e: NullPointerException) {
            log.error("[ERROR] ${abilityClass.javaClass.simpleName} was not found", e)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : IAbility<out AbilityCommand>> getAbility(abilityClass: Class<out T>): T? {
        return try {
            synchronized(lock) {
                abilityMap[abilityClass] as T
            }
        } catch (e: TypeCastException) {
            log.error("${abilityClass.simpleName} must implements IAbility interface.", e)
            null
        }
    }
}

package dataSource

object BreakTimeDataSource {

    val breakTimeList = mapOf<Long, String>(
        30L * 60000 to "30 минут",
        60L * 60000 to "1 час",
        120L * 60000 to "2 часа",
        240L * 60000 to "4 часа",
    )
}

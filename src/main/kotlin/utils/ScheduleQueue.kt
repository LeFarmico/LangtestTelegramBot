package utils

import entity.MessageType
import java.time.ZoneOffset
import java.util.concurrent.PriorityBlockingQueue

class ScheduleQueue<E : MessageType> : PriorityBlockingQueue<E>() {

    override fun add(element: E): Boolean {

        return super.add(element)
    }

    override fun comparator(): Comparator<in E> = Comparator<E> { o1, o2 ->
        if (o1.notifyTime!!.toEpochSecond(ZoneOffset.UTC) > o2.notifyTime!!.toEpochSecond(ZoneOffset.UTC)) {
            1
        } else {
            0
        }
    }

}

package cc.mewcraft.worldreset.manager

import cc.mewcraft.worldreset.schedule.Schedule

/**
 * This class manages all the [Schedule].
 */
interface ScheduleManager {
    val schedules: Sequence<Schedule>
        get() = throw NotImplementedError()

    fun load()
    fun start()
    fun get(name: String): Schedule
    fun add(schedule: Schedule)
}
package cc.mewcraft.worldreset.manager

import cc.mewcraft.worldreset.schedule.Schedule

/**
 * This class keeps all the schedules.
 */
interface Schedules {
    val schedules: Sequence<Schedule>
        get() = throw NotImplementedError()

    fun load()
    fun start()
    fun get(name: String): Schedule
    fun add(schedule: Schedule)
}
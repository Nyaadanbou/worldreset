package cc.mewcraft.worldreset.manager

import cc.mewcraft.worldreset.WorldResetSettings
import cc.mewcraft.worldreset.logger
import cc.mewcraft.worldreset.plugin
import cc.mewcraft.worldreset.schedule.Schedule
import cc.mewcraft.worldreset.util.mini
import com.cronutils.CronScheduler
import com.cronutils.ExecutionStatus
import com.github.shynixn.mccoroutine.bukkit.launch
import me.lucko.helper.terminable.Terminable

class LocalSchedules(
    private val settings: WorldResetSettings,
) : Schedules, Terminable {
    private lateinit var scheduler: CronScheduler
    private lateinit var scheduleMap: Map<String, Schedule>

    override val schedules: Sequence<Schedule>
        get() = scheduleMap.values.asSequence()

    override fun load() {
        logger.info("<aqua>Loading schedules for scheduler.".mini())
        scheduleMap = settings.schedules.associateBy { it.name }
        logger.info("<aqua>Loaded ${scheduleMap.size} schedules from file.".mini())
    }

    override fun start() {
        logger.info("<aqua>Starting scheduler.".mini())

        scheduler = CronScheduler()
        scheduleMap.values.forEach { add(it) } // Add it to the scheduler
        logger.info("<aqua>Added ${scheduleMap.size} schedules to scheduler.".mini())

        scheduler.startPollingTask()
        logger.info("<aqua>Scheduler has started! Any errors are reported above.".mini())
    }

    override fun get(name: String): Schedule {
        return scheduleMap[name] ?: throw NullPointerException("No such schedule: $name")
    }

    override fun add(schedule: Schedule) {
        scheduler.scheduleCronJob(schedule.name, schedule.cron) {
            logger.info("<gold>Cron `${schedule.cron.asString()}` is triggered. Executing schedule: `${schedule.name}`".mini())
            logger.info("<gold>Starting the schedule as coroutine.".mini())

            plugin.launch {
                schedule.execute()
            }

            logger.info("<gold>Coroutine has started.".mini())

            ExecutionStatus.SUCCESS
        }
    }

    override fun close() {
        scheduler.stopPollingTask()
    }
}
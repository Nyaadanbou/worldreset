package cc.mewcraft.worldreset.manager

import cc.mewcraft.cronutils.CronScheduler
import cc.mewcraft.cronutils.ExecutionStatus
import cc.mewcraft.worldreset.WorldResetSettings
import cc.mewcraft.worldreset.logger
import cc.mewcraft.worldreset.schedule.Schedule
import cc.mewcraft.worldreset.util.mini
import me.lucko.helper.terminable.Terminable

class LocalScheduleManager(
    private val settings: WorldResetSettings,
) : ScheduleManager, Terminable {
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

        scheduler.start()
        logger.info("<aqua>Scheduler has started! Any errors are reported above.".mini())
    }

    override fun get(name: String): Schedule {
        return scheduleMap[name] ?: throw NullPointerException("No such schedule: $name")
    }

    override fun add(schedule: Schedule) {
        scheduler.schedule(schedule.name, schedule.cron) {
            logger.info("<gold>Cron `${schedule.cron.asString()}` is triggered. Executing schedule: `${schedule.name}`".mini())
            schedule.execute()
            logger.info("<gold>Execution of schedule `${schedule.name}` is completed.".mini())
            ExecutionStatus.SUCCESS
        }
    }

    override fun close() {
        scheduler.shutdown()
    }
}
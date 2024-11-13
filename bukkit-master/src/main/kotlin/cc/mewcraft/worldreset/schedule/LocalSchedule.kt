package cc.mewcraft.worldreset.schedule

import cc.mewcraft.worldreset.data.CronData
import com.cronutils.model.Cron
import com.cronutils.model.time.ExecutionTime
import java.time.Duration
import java.time.ZonedDateTime
import kotlin.jvm.optionals.getOrNull

abstract class LocalSchedule(
    override val name: String,
    private val cronData: CronData,
) : Schedule {
    override val cron: Cron
        get() = cronData.cron

    override fun nextExecution(): Duration? {
        return ExecutionTime.forCron(cronData.cron).timeToNextExecution(ZonedDateTime.now()).getOrNull()
    }
}

package cc.mewcraft.worldreset.schedule

import cc.mewcraft.worldreset.util.throwAtSlave
import com.cronutils.model.Cron
import java.time.Duration

/**
 * An empty schedule that does nothing.
 *
 * Used as a placeholder when the real schedule is not yet loaded.
 */
object EmptySchedule : Schedule {
    override val name: String
        get() = "empty"
    override val cron: Cron
        get() = throwAtSlave()
    override val timeUntilNextExecution: Duration
        get() = Duration.ZERO

    override suspend fun execute(): Unit =
        throwAtSlave()
}
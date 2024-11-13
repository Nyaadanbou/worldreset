package cc.mewcraft.worldreset.schedule

import cc.mewcraft.worldreset.util.throwAtSlave
import java.time.Duration

class RemoteSchedule(
    override val name: String,
    override val timeUntilNextExecution: Duration?,
) : Schedule {
    override val cron
        get() = throwAtSlave()

    override suspend fun execute(): Unit =
        throwAtSlave()
}
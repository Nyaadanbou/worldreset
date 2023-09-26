package cc.mewcraft.worldreset.schedule

import cc.mewcraft.worldreset.data.CommandData
import cc.mewcraft.worldreset.data.CronData

class CommandSchedule(
    name: String,
    cron: CronData,
    private val commandData: CommandData,
) : LocalSchedule(
    name,
    cron
) {
    override suspend fun execute() {
        commandData.dispatchAll()
    }
}

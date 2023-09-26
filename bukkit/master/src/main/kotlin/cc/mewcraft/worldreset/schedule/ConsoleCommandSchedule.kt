package cc.mewcraft.worldreset.schedule

import cc.mewcraft.worldreset.data.CommandData
import cc.mewcraft.worldreset.data.CronData
import org.bukkit.plugin.Plugin

class ConsoleCommandSchedule(
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

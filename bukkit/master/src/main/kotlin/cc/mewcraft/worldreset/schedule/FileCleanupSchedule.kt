package cc.mewcraft.worldreset.schedule

import cc.mewcraft.worldreset.data.CronData
import cc.mewcraft.worldreset.data.PathData
import org.bukkit.plugin.Plugin

class FileCleanupSchedule(
    name: String,
    cron: CronData,
    private val pathData: PathData,
) : LocalSchedule(
    name,
    cron
) {
    override suspend fun execute() {
        pathData.deleteFiles()
    }
}

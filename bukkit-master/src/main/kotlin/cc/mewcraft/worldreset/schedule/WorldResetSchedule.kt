package cc.mewcraft.worldreset.schedule

import cc.mewcraft.worldreset.data.CommandData
import cc.mewcraft.worldreset.data.CronData
import cc.mewcraft.worldreset.data.WorldData
import cc.mewcraft.worldreset.logger
import kotlinx.coroutines.delay

class WorldResetSchedule(
    name: String,
    cron: CronData,
    val worldData: WorldData,
    private val preCommandData: CommandData,
    private val postCommandData: CommandData,
) : LocalSchedule(
    name,
    cron
) {
    override suspend fun execute() {
        // Run pre commands
        logger.info("Starting running pre commands.")
        preCommandData.dispatchAll()
        delay(1000)

        // Reset the world
        if (!worldData.regen()) {
            logger.error("Something severe occurred during the world reset process.")
            logger.error("The schedule was cancelled in the middle.")
            logger.error("See more details in the logs above.")
            return
        }

        // Run post commands
        delay(1000)
        logger.info("Starting running post commands.")
        postCommandData.dispatchAll()
    }
}

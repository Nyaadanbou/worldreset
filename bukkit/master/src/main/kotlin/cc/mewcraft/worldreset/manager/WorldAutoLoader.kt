package cc.mewcraft.worldreset.manager

import cc.mewcraft.worldreset.logger
import cc.mewcraft.worldreset.schedule.WorldResetSchedule
import org.bukkit.WorldCreator

private const val PREFIX = "[WorldAutoLoader]"

class WorldAutoLoader(
    /**
     * An instance of [Schedules].
     *
     * The [Schedules.load] must be called before passing to this constructor.
     */
    private val schedules: Schedules,
) {
    /**
     * Loads all custom worlds specified in the schedules.
     */
    fun load() {
        val worldDataSequence = schedules
            .schedules
            .filterIsInstance<WorldResetSchedule>()
            .map { it.worldData }

        logger.info("$PREFIX Loading custom worlds specified in the schedules.")
        logger.info("$PREFIX Server might lag for a while. Please bear with it!")

        for (it in worldDataSequence) {
            if (it.isMainWorld)
                logger.info("$PREFIX World is main world: `${it.name}`. Skipped.")

            if (it.isWorldDirectoryExisting) {
                logger.info("$PREFIX World directory exists: `${it.name}`. Trying to load it.")
                WorldCreator(it.name).createWorld()
                logger.info("$PREFIX World loaded: `${it.name}`.")
            } else {
                logger.info("$PREFIX World directory does not exist: `${it.name}`. Skipped.")
            }
        }
    }
}
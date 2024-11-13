package cc.mewcraft.worldreset

import cc.mewcraft.worldreset.data.BroadcastData
import cc.mewcraft.worldreset.data.CommandData
import cc.mewcraft.worldreset.data.CronData
import cc.mewcraft.worldreset.data.WorldData
import cc.mewcraft.worldreset.schedule.BroadcastSchedule
import cc.mewcraft.worldreset.schedule.CommandSchedule
import cc.mewcraft.worldreset.schedule.Schedule
import cc.mewcraft.worldreset.schedule.WorldResetSchedule
import org.bukkit.configuration.ConfigurationSection

/**
 * Contains settings of this plugin.
 *
 * All the settings will be initialized as soon as this class is instantiated.
 *
 * If you need to reload the settings, simply construct a new instance again.
 */
class WorldResetSettings {
    init {
        plugin.saveDefaultConfig()
        plugin.reloadConfig()
    }

    /* Public Methods */

    /**
     * List of schedules which are loaded from the config.
     */
    val schedules: List<Schedule> by lazy {
        buildList {
            with(plugin.config.getConfigurationSectionOrThrow("schedules")) schedules@{

                getKeys(false).toList().forEach { key ->
                    logger.info("Loading schedule: $key")

                    with(getConfigurationSectionOrThrow(key)) schedule@{
                        val cron = CronData(this@schedule.getStringOrThrow("cron"))
                        val schedule = when (val type = this@schedule.getStringOrThrow("type")) {
                            "COMMAND" -> loadCommandSchedule(key, cron, this@schedule)
                            "BROADCAST" -> loadBroadcastSchedule(key, cron, this@schedule)
                            "WORLD_RESET" -> loadWorldResetSchedule(key, cron, this@schedule)
                            else -> throw IllegalArgumentException(type)
                        }
                        this@buildList += schedule
                    }
                }

            }
        }
    }

    /* Utility Methods */

    private fun loadBroadcastSchedule(
        name: String,
        cron: CronData,
        config: ConfigurationSection,
    ): BroadcastSchedule {
        val messages = config.getStringList("messages")
        return BroadcastSchedule(
            name, cron, BroadcastData(messages)
        )
    }

    private fun loadCommandSchedule(
        name: String,
        cron: CronData,
        config: ConfigurationSection,
    ): CommandSchedule {
        val commands = config.getStringList("commands")
        return CommandSchedule(
            name, cron, CommandData(commands)
        )
    }

    private fun loadWorldResetSchedule(
        name: String,
        cron: CronData,
        config: ConfigurationSection,
    ): WorldResetSchedule {
        val world = config.getStringOrThrow("name")
        val keepSeed = config.getBoolean("keep_seed")
        val environment = config.getStringOrThrow("environment")
        val preCommands = config.getStringList("pre_commands")
        val postCommands = config.getStringList("post_commands")
        return WorldResetSchedule(
            name, cron, WorldData(world, keepSeed, environment), CommandData(preCommands), CommandData(postCommands),
        )
    }
}

/* Extension Functions */

private fun ConfigurationSection.getConfigurationSectionOrThrow(path: String) =
    this.getConfigurationSection(path) ?: throw NullPointerException(path)

private fun ConfigurationSection.getStringOrThrow(path: String) =
    this.getString(path) ?: throw NullPointerException(path)

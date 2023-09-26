package cc.mewcraft.worldreset.data

import cc.mewcraft.worldreset.logger
import cc.mewcraft.worldreset.plugin
import org.bukkit.Bukkit

class CommandData(
    commands: List<String>,
) {
    private val commands = commands.map { SingleCommandData(it) }

    fun dispatchAll() {
        commands.forEach { it.dispatch() }
    }
}

private class SingleCommandData(
    private val command: String,
) {
    init {
        // Validate command line
        if (!isRegistered(command)) {
            logger.warn("Command `$command` is not found in the CommandMap")
        }
    }

    fun dispatch() {
        if (!isRegistered(command)) {
            logger.warn("Skipping command: `$command`")
        } else {
            logger.info("Dispatching command: `$command`")
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command)
        }
    }

    private fun isRegistered(command: String) =
        command.splitToSequence(" ").first() in plugin.server.commandMap.knownCommands
}

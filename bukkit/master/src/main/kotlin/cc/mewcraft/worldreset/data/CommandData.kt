package cc.mewcraft.worldreset.data

import cc.mewcraft.worldreset.data.CommandType.DELETE_FILE
import cc.mewcraft.worldreset.data.CommandType.GAME_COMMAND
import cc.mewcraft.worldreset.logger
import cc.mewcraft.worldreset.plugin
import org.bukkit.Bukkit

class CommandData(
    /**
     * Each string in the list must follow [SingleCommandData].
     */
    commands: List<String>,
) {
    private val commands = commands.map { SingleCommandData(it) }

    fun dispatchAll() {
        commands.forEach { it.dispatch() }
    }
}

private class SingleCommandData(
    /**
     * Command string format: `[command-type] [command-data]`.
     *
     * - `[command-type]` is [CommandType], with brackets, such as `[delete-file]`.
     * - `[command-data]` is implementation-defined, without brackets.
     *
     * **Examples**
     *
     * - `[delete-file] plugins/CustomStructures/data/structures.db`
     * - `[game-command] customstructures reload`
     */
    command: String,
) {
    private val regex: Regex = Regex("\\[(?<CommandType>.+)]\\s(?<CommandData>.+)")
    private val type: CommandType
    private val data: String

    init {
        val ex: () -> Nothing = { throw IllegalArgumentException(command) }
        val result: MatchResult = regex.matchEntire(command) ?: ex()
        type = result.groups["CommandType"]?.value?.trim()?.let(CommandType::valueOf) ?: ex()
        data = result.groups["CommandData"]?.value?.trim() ?: ex()

        // Validate command line
        if (type == GAME_COMMAND && !isRegistered(data)) logger.warn("Command `$data` is not found in the CommandMap")
    }

    fun dispatch() {
        when (type) {
            GAME_COMMAND -> {
                if (!isRegistered(data)) {
                    logger.warn("Skipping command: `$data`")
                } else {
                    logger.info("Dispatching command: `$data`")
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), data)
                }
            }

            DELETE_FILE -> {
                TODO()
            }
        }
    }

    private fun isRegistered(command: String) =
        command.splitToSequence(" ").first() in plugin.server.commandMap.knownCommands
}

private enum class CommandType {
    DELETE_FILE,
    GAME_COMMAND,
}

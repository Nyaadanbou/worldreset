package cc.mewcraft.worldreset.data

import cc.mewcraft.worldreset.data.CommandType.*
import cc.mewcraft.worldreset.logger
import cc.mewcraft.worldreset.plugin
import me.lucko.helper.Helper
import kotlin.io.path.Path

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
     * - `[command-type]` is a value from [CommandType], with brackets.
     * - `[command-data]` is implementation-defined, without brackets.
     *
     * **Examples**
     *
     * - `[delete-file] plugins/CustomStructures/data`
     * - `[console-cmd] customstructures reload`
     * - `[console-cmd] teleport @a 0 0 0 ~ ~`
     * - `[reset-joined]` (no data)
     */
    command: String,
) {
    private val regex: Regex = Regex("\\[(?<CommandType>.+)]\\s(?<CommandData>.+)")
    private val type: CommandType
    private val data: String

    init {
        val result: MatchResult = regex.matchEntire(command) ?: error("Malformed command: `$command`")
        type = result.groups["CommandType"]?.value?.let { CommandType.match(it) } ?: error("Failed to extract CommandType: `$command`")
        data = result.groups["CommandData"]?.value?.trim() ?: error("Failed to extract CommandData: `$command`")
    }

    fun dispatch() {
        when (type) {
            CONSOLE_CMD -> {
                if (!isRegistered(data)) {
                    logger.warn("Skipping command: `$data`")
                } else {
                    logger.info("Dispatching command: `$data`")
                    Helper.executeCommand(data)
                }
            }

            DELETE_FILE -> {
                val file = Path(data).toFile()
                logger.info("Deleting file: ${file.path}")
                file.deleteRecursively()
                logger.info("Deleted file: ${file.path}")
            }

            RESET_JOINED -> {
                plugin.userDataManager.modifyEachUser { it.copy(hasJoined = false) }
            }
        }
    }

    fun isRegistered(command: String): Boolean {
        return command.splitToSequence(" ").first() in plugin.server.commandMap.knownCommands
    }
}

private enum class CommandType {
    DELETE_FILE,
    CONSOLE_CMD,
    RESET_JOINED,
    ;

    companion object {
        fun match(value: String): CommandType =
            valueOf(value.uppercase().replace('-', '_'))
    }
}

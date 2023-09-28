package cc.mewcraft.worldreset.data

import cc.mewcraft.worldreset.data.CommandType.CONSOLE_CMD
import cc.mewcraft.worldreset.data.CommandType.DELETE_FILE
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
     */
    command: String,
) {
    private val regex: Regex = Regex("\\[(?<CommandType>.+)]\\s(?<CommandData>.+)")
    private val type: CommandType
    private val data: String

    init {
        val result: MatchResult = regex.matchEntire(command) ?: error("Malformed command: `$command`")
        type = result.groups["CommandType"]?.value?.trim()?.let { CommandType.match(it) } ?: error("Failed to extract CommandType: `$command`")
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
                val root = requireNotNull(plugin.server.pluginsFolder.parent) { "Should never happen, unless the default `plugins` folder is moved" }
                val input = Path(data.removePrefix("/")) // Force convert to relative path
                val target = Path(root).resolve(input)
                val file = target.toFile()
                logger.info("Deleting file: ${file.path}")
                file.deleteRecursively()
                logger.info("Deleted file: ${file.path}")
            }
        }
    }

}

private enum class CommandType {
    DELETE_FILE,
    CONSOLE_CMD,
    ;

    companion object {
        fun match(value: String): CommandType =
            valueOf(value.uppercase())
    }

}

private fun isRegistered(command: String) =
    command.splitToSequence(" ").first() in plugin.server.commandMap.knownCommands

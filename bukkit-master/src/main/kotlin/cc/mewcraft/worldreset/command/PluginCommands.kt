package cc.mewcraft.worldreset.command

import cc.mewcraft.spatula.command.SimpleCommands
import cc.mewcraft.worldreset.manager.ServerLockManager
import cc.mewcraft.worldreset.plugin
import cloud.commandframework.arguments.standard.BooleanArgument

class PluginCommands(
    private val serverLockManager: ServerLockManager,
) : SimpleCommands(plugin) {
    override fun registerCommands() {
        commandRegistry().addCommand(
            commandRegistry().commandBuilder("worldreset")
                .literal("serverlock")
                .argument(BooleanArgument.builder("status"))
                .permission("worldreset.command.admin")
                .handler { ctx ->
                    val status = ctx.get<Boolean>("status")
                    serverLockManager.setLock(status)
                    ctx.sender.sendRichMessage("Current server lock: ${status.toString().uppercase()}")
                }.build()
        )
        commandRegistry().addCommand(
            commandRegistry().commandBuilder("worldreset")
                .literal("reload")
                .permission("worldreset.command.admin")
                .handler { ctx ->
                    plugin.onDisable()
                    plugin.onEnable()
                    ctx.sender.sendRichMessage("WorldReset has been reloaded!")
                }.build()
        )

        commandRegistry().registerCommands()
    }
}
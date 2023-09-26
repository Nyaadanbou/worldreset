package cc.mewcraft.worldreset.command

import cc.mewcraft.mewcore.command.SimpleCommands
import cc.mewcraft.worldreset.manager.ServerLocks
import cc.mewcraft.worldreset.plugin
import cloud.commandframework.arguments.standard.BooleanArgument
import org.bukkit.plugin.Plugin

class PluginCommands(
    private val serverLocks: ServerLocks,
) : SimpleCommands<Plugin>(plugin) {
    override fun prepareAndRegister() {
        registry.prepareCommand(
            registry.commandBuilder("worldreset")
                .literal("serverlock")
                .argument(BooleanArgument.builder("status"))
                .permission("worldreset.command.admin")
                .handler { ctx ->
                    val status = ctx.get<Boolean>("status")
                    serverLocks.setLock(status)
                    ctx.sender.sendRichMessage("Current server lock: ${status.toString().uppercase()}")
                }.build()
        )
        registry.prepareCommand(
            registry.commandBuilder("worldreset")
                .literal("reload")
                .permission("worldreset.command.admin")
                .handler { ctx ->
                    plugin.onDisable()
                    plugin.onEnable()
                    ctx.sender.sendRichMessage("WorldReset has been reloaded!")
                }.build()
        )

        registry.registerCommands()
    }
}
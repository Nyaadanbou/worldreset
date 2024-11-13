package cc.mewcraft.worldreset.command

import cc.mewcraft.worldreset.manager.ServerLockManager
import cc.mewcraft.worldreset.plugin
import org.bukkit.command.CommandSender
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.kotlin.extension.buildAndRegister
import org.incendo.cloud.paper.LegacyPaperCommandManager
import org.incendo.cloud.parser.standard.BooleanParser

class PluginCommands(
    private val serverLockManager: ServerLockManager,
) {
    companion object {
        /**
         * Whether the commands have been registered.
         */
        private var registered: Boolean = false
    }

    private lateinit var manager: LegacyPaperCommandManager<CommandSender>

    fun registerCommands() {
        if (registered) {
            return
        }

        manager = LegacyPaperCommandManager(plugin, ExecutionCoordinator.simpleCoordinator(), SenderMapper.identity())
        manager.registerBrigadier()
        manager.buildAndRegister("worldreset") {
            literal("serverlock")
            required("status", BooleanParser.booleanParser())
            permission = "worldreset.command.admin"
            handler { ctx ->
                val sender = ctx.sender()
                val status = ctx.get<Boolean>("status")
                serverLockManager.setLock(status)
                sender.sendRichMessage("Current server lock: ${status.toString().uppercase()}")
            }
        }
        manager.buildAndRegister("worldreset") {
            literal("reload")
            permission = "worldreset.command.admin"
            handler { ctx ->
                val sender = ctx.sender()
                plugin.onDisable()
                plugin.onEnable()
                sender.sendRichMessage("WorldReset has been reloaded!")
            }
        }

        registered = true
    }
}
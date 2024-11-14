package cc.mewcraft.worldreset.command

import cc.mewcraft.worldreset.data.WorldData
import cc.mewcraft.worldreset.manager.ServerLockManager
import cc.mewcraft.worldreset.plugin
import com.github.shynixn.mccoroutine.bukkit.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.bukkit.World
import org.bukkit.World.*
import org.bukkit.command.CommandSender
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.bukkit.parser.WorldParser
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler
import org.incendo.cloud.kotlin.extension.buildAndRegister
import org.incendo.cloud.paper.LegacyPaperCommandManager
import org.incendo.cloud.parser.standard.BooleanParser
import org.incendo.cloud.parser.standard.EnumParser

class PluginCommands(
    private val serverLockManager: ServerLockManager,
) {
    companion object Shared {
        private const val ROOT_COMMAND = "worldreset"

        /**
         * Whether the commands have been registered.
         */
        private var registered: Boolean = false

        /**
         * The job for resetting the world.
         */
        private var resetJob: Job? = null

        /**
         * Mutex for the reset job.
         */
        private val mutex = Mutex()
    }

    private lateinit var manager: LegacyPaperCommandManager<CommandSender>

    fun registerCommands() {
        if (registered) {
            return
        }

        manager = LegacyPaperCommandManager(plugin, ExecutionCoordinator.simpleCoordinator(), SenderMapper.identity())
        manager.registerBrigadier()
        manager.buildAndRegister(ROOT_COMMAND) {
            literal("reset_world")
            required("world", WorldParser.worldParser())
            required("environment", EnumParser.enumParser(Environment::class.java))
            optional("keep_seed", BooleanParser.booleanParser())
            permission = "worldreset.command.admin"
            suspendingHandler { ctx ->
                mutex.withLock {
                    val sender = ctx.sender()

                    if (resetJob != null) {
                        sender.sendRichMessage("<red>A world reset is already in progress!")
                        return@suspendingHandler
                    }

                    // Reassign the reset job
                    resetJob = plugin.launch {
                        val world = ctx.get<World>("world")
                        val environment = ctx.get<Environment>("environment")
                        val keepSeed = ctx.getOrDefault("keep_seed", false)

                        sender.sendRichMessage("Start resetting world: $world")

                        val worldData = WorldData(world.name, keepSeed, environment)
                        if (worldData.regen()) {
                            sender.sendRichMessage("World reset completed!")
                        } else {
                            sender.sendRichMessage("<red>World reset failed!")
                        }

                        // Set it back to null
                        resetJob = null
                    }
                }
            }
        }
        manager.buildAndRegister(ROOT_COMMAND) {
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
        manager.buildAndRegister(ROOT_COMMAND) {
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
@file:Suppress("MemberVisibilityCanBePrivate")

package cc.mewcraft.worldreset

import cc.mewcraft.worldreset.command.PluginCommands
import cc.mewcraft.worldreset.listener.PlayerListener
import cc.mewcraft.worldreset.listener.WorldListener
import cc.mewcraft.worldreset.manager.*
import cc.mewcraft.worldreset.message.MasterPluginMessenger
import cc.mewcraft.worldreset.placeholder.MiniPlaceholderExtension
import cc.mewcraft.worldreset.placeholder.PlaceholderAPIExtension
import me.lucko.helper.Helper
import me.lucko.helper.Schedulers
import me.lucko.helper.messaging.Messenger
import me.lucko.helper.plugin.ExtendedJavaPlugin
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import java.util.concurrent.TimeUnit

class WorldResetPlugin : ExtendedJavaPlugin() {
    lateinit var settings: WorldResetSettings private set
    lateinit var scheduleManager: ScheduleManager private set
    lateinit var serverLockManager: ServerLockManager private set
    lateinit var worldLockManager: WorldLockManager private set
    lateinit var worldAutoLoader: WorldAutoLoader private set

    override fun load() {
        /* Initialize managers (independent) */
        serverLockManager = LocalServerLockManager
        worldLockManager = LocalWorldLockManager
    }

    override fun enable() {
        // TODO use Koin to better manage DI
        // Right now It's totally a mess ...

        /* Initialize managers */
        settings = WorldResetSettings()
        scheduleManager = LocalScheduleManager(settings).also {
            bind(it)
            it.load() // Load schedules from files
        }
        worldAutoLoader = WorldAutoLoader(scheduleManager).also {
            bind(it)
            it.load() // Load custom worlds specified in the schedules
        }

        /* Initialize messenger */
        val messenger = Helper.serviceNullable(Messenger::class.java) ?: run {
            logger.severe("No messenger instance is provided. Is 'helper-redis' installed?")
            logger.severe("The plugin will not work without it. Shutting down ...")
            server.pluginManager.disablePlugin(this)
            return
        }
        MasterPluginMessenger(messenger, scheduleManager, serverLockManager).bindWith(this)

        /* Register listeners */
        PlayerListener(serverLockManager, worldLockManager).also { registerTerminableListener(it).bindWith(this) }
        WorldListener(serverLockManager, worldLockManager).also { registerTerminableListener(it).bindWith(this) }

        /* Register expansions */
        MiniPlaceholderExtension(scheduleManager, serverLockManager).also { bind(it).register() }
        PlaceholderAPIExtension(scheduleManager, serverLockManager).also { bind(it).register() }

        /* Register commands */
        PluginCommands(serverLockManager).registerCommands()

        // Start schedules after "Done"
        Schedulers.sync().runLater({ scheduleManager.start() }, 10, TimeUnit.SECONDS)
    }
}

val logger: ComponentLogger by lazy { plugin.componentLogger }

val plugin: WorldResetPlugin by lazy { Helper.plugins().getPlugin("WorldReset") as WorldResetPlugin }

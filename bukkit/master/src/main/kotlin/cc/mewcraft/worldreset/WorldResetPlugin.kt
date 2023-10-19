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
    lateinit var schedules: Schedules private set
    lateinit var serverLocks: ServerLocks private set
    lateinit var worldLocks: WorldLocks private set
    lateinit var worldAutoLoader: WorldAutoLoader private set

    override fun load() {
        /* Initialize managers (independent) */
        serverLocks = LocalServerLocks
        worldLocks = LocalWorldLocks
    }

    override fun enable() {
        // TODO use Koin to better manage DI
        // Right now It's totally a mess ...

        /* Initialize managers */
        settings = WorldResetSettings()
        schedules = LocalSchedules(settings).also {
            bind(it)
            it.load() // Load schedules from files
        }
        worldAutoLoader = WorldAutoLoader(schedules).also {
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
        MasterPluginMessenger(messenger, schedules, serverLocks).bindWith(this)

        /* Register listeners */
        PlayerListener(serverLocks, worldLocks).also { registerTerminableListener(it).bindWith(this) }
        WorldListener(serverLocks, worldLocks).also { registerTerminableListener(it).bindWith(this) }

        /* Register expansions */
        MiniPlaceholderExtension(schedules, serverLocks).also { bind(it).register() }
        PlaceholderAPIExtension(schedules, serverLocks).also { bind(it).register() }

        /* Register commands */
        PluginCommands(serverLocks).registerCommands()

        // Start schedules after "Done"
        Schedulers.sync().runLater({ schedules.start() }, 10, TimeUnit.SECONDS)
    }
}

val logger: ComponentLogger by lazy { plugin.componentLogger }

val plugin: WorldResetPlugin by lazy { Helper.plugins().getPlugin("WorldReset") as WorldResetPlugin }

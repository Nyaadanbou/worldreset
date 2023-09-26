package cc.mewcraft.worldreset

import cc.mewcraft.mewcore.plugin.MeowJavaPlugin
import cc.mewcraft.worldreset.manager.RemoteSchedules
import cc.mewcraft.worldreset.manager.RemoteServerLocks
import cc.mewcraft.worldreset.manager.Schedules
import cc.mewcraft.worldreset.manager.ServerLocks
import cc.mewcraft.worldreset.message.SlavePluginMessenger
import cc.mewcraft.worldreset.placeholder.MiniPlaceholderExtension
import cc.mewcraft.worldreset.placeholder.PlaceholderAPIExtension
import me.lucko.helper.Services
import me.lucko.helper.messaging.Messenger

class WorldResetPlugin : MeowJavaPlugin() {
    private lateinit var schedules: Schedules
    private lateinit var serverLocks: ServerLocks

    override fun enable() {
        /* Get the instance of messenger */
        val messenger = Services.load(Messenger::class.java)
        val slavePluginMessenger = SlavePluginMessenger(messenger).apply { bind(this) }

        /* Initialize managers */
        serverLocks = RemoteServerLocks(slavePluginMessenger)
        schedules = RemoteSchedules(slavePluginMessenger)

        /* Register expansions */
        MiniPlaceholderExtension(schedules, serverLocks).also { bind(it).register() }
        PlaceholderAPIExtension(schedules, serverLocks).also { bind(it).register() }
    }
}

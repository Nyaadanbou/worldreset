package cc.mewcraft.worldreset

import cc.mewcraft.worldreset.manager.RemoteScheduleManager
import cc.mewcraft.worldreset.manager.RemoteServerLockManager
import cc.mewcraft.worldreset.manager.ScheduleManager
import cc.mewcraft.worldreset.manager.ServerLockManager
import cc.mewcraft.worldreset.message.SlavePluginMessenger
import cc.mewcraft.worldreset.placeholder.MiniPlaceholderExtension
import cc.mewcraft.worldreset.placeholder.PlaceholderAPIExtension
import me.lucko.helper.Services
import me.lucko.helper.messaging.Messenger
import me.lucko.helper.plugin.ExtendedJavaPlugin

class WorldResetPlugin : ExtendedJavaPlugin() {
    private lateinit var scheduleManager: ScheduleManager
    private lateinit var serverLockManager: ServerLockManager

    override fun enable() {
        /* Get the instance of messenger */
        val messenger = Services.load(Messenger::class.java)
        val slavePluginMessenger = SlavePluginMessenger(messenger).apply { bind(this) }

        /* Initialize managers */
        serverLockManager = RemoteServerLockManager(slavePluginMessenger)
        scheduleManager = RemoteScheduleManager(slavePluginMessenger)

        /* Register expansions */
        MiniPlaceholderExtension(scheduleManager, serverLockManager).also { bind(it).register() }
        PlaceholderAPIExtension(scheduleManager, serverLockManager).also { bind(it).register() }
    }
}

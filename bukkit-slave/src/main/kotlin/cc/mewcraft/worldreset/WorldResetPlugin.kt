@file:Suppress("unused")

package cc.mewcraft.worldreset

import cc.mewcraft.messenger.redis.RedisProvider
import cc.mewcraft.worldreset.manager.*
import cc.mewcraft.worldreset.messaging.SlaveChannel
import cc.mewcraft.worldreset.placeholder.MiniPlaceholderExtension
import cc.mewcraft.worldreset.placeholder.PlaceholderAPIExtension
import me.lucko.helper.plugin.ExtendedJavaPlugin

class WorldResetPlugin : ExtendedJavaPlugin() {
    private lateinit var scheduleManager: ScheduleManager
    private lateinit var serverLockManager: ServerLockManager

    override fun enable() {
        /* Get the instance of messenger */
        val messenger = RedisProvider.redisProvider().getRedis()
        val slaveChannel = SlaveChannel(messenger)
        slaveChannel.bindWith(this)

        /* Initialize managers */
        serverLockManager = RemoteServerLockManager(slaveChannel)
        scheduleManager = RemoteScheduleManager(slaveChannel)

        /* Register expansions */
        if (isPluginPresent("PlaceholderAPI")) {
            PlaceholderAPIExtension(scheduleManager, serverLockManager).also { bind(it).register() }
        }
        if (isPluginPresent("MiniPlaceholders")) {
            MiniPlaceholderExtension(scheduleManager, serverLockManager).also { bind(it).register() }
        }
    }
}

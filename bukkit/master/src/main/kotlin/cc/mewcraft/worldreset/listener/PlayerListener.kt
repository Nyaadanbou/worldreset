package cc.mewcraft.worldreset.listener

import cc.mewcraft.mewcore.listener.AutoCloseableListener
import cc.mewcraft.worldreset.logger
import cc.mewcraft.worldreset.manager.ServerLocks
import cc.mewcraft.worldreset.manager.WorldLocks
import cc.mewcraft.worldreset.util.mini
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerTeleportEvent

private val OVERWORLD_KEY = NamespacedKey.minecraft("overworld")

class PlayerListener(
    private val serverLocks: ServerLocks,
    private val worldLocks: WorldLocks,
) : AutoCloseableListener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun onLogin(e: AsyncPlayerPreLoginEvent) {
        /* Kick player if server lock is enabled */
        if (serverLocks.isLocked()) e.disallow(
            /* result = */ AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
            /* message = */ "<red>World Reset in Progress".mini()
        )
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onWorldChange(e: PlayerTeleportEvent) {
        /* Cancel teleport if server lock is enabled */
        // Except the target world is the main world.

        if (serverLocks.isLocked()) {
            val toWorld = runCatching { e.to.world }.getOrNull()
            if (toWorld == null || toWorld.key() != OVERWORLD_KEY) {
                e.isCancelled = true
                logger.info("PlayerTeleportEvent was cancelled: ${e.player.name};${toWorld?.name}")
            }
        }

        // Old implementation using WorldLocks
        // val toWorld = runCatching { e.to.world }.getOrNull()
        // if (toWorld == null || worldLocks.isLocked(toWorld.name)) {
        //     e.isCancelled = true
        //     logger.info("PlayerTeleportEvent was cancelled: ${e.player.name};${toWorld?.name}")
        // }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onDamage(e: EntityDamageEvent) {
        /* Cancel player damage if the server is locked */
        if (e.entity !is Player) return
        if (serverLocks.isLocked()) {
            e.isCancelled = true
            logger.info("EntityDamageEvent was cancelled: ${e.entity.name}")
        }
    }
}
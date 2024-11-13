package cc.mewcraft.worldreset.listener

import cc.mewcraft.worldreset.logger
import cc.mewcraft.worldreset.manager.ServerLockManager
import cc.mewcraft.worldreset.manager.WorldLockManager
import cc.mewcraft.worldreset.util.mini
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerTeleportEvent

private val OVERWORLD_KEY = NamespacedKey.minecraft("overworld")

class PlayerListener(
    private val serverLockManager: ServerLockManager,
    private val worldLockManager: WorldLockManager,
) : Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun onLogin(e: AsyncPlayerPreLoginEvent) {
        /* Kick player if server lock is enabled */

        if (serverLockManager.isLocked()) e.disallow(
            /* result = */ AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
            /* message = */ "<red>世界正在重置".mini()
        )
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onWorldChange(e: PlayerTeleportEvent) {
        /* Cancel teleport if server lock is enabled, except the target world is the main world */

        if (serverLockManager.isLocked()) {
            val toWorld = runCatching { e.to.world }.getOrNull()
            if (toWorld == null || toWorld.key() != OVERWORLD_KEY) {
                e.isCancelled = true
                e.player.sendRichMessage("<#ff7e53>世界正在重置，请稍等片刻再进行传送。本次传送已取消！")
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
        if (serverLockManager.isLocked()) {
            e.isCancelled = true
            e.entity.sendRichMessage("<#ff7e53>世界正在重置，所有玩家暂时获得无敌状态。已免疫当前伤害！")
            logger.info("EntityDamageEvent was cancelled: ${e.entity.name}")
        }
    }
}
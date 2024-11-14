package cc.mewcraft.worldreset.listener

import cc.mewcraft.worldreset.logger
import cc.mewcraft.worldreset.manager.*
import cc.mewcraft.worldreset.util.mini
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.*
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.*

private val OVERWORLD_KEY = NamespacedKey.minecraft("overworld")

class PlayerListener(
    private val serverLockManager: ServerLockManager,
    private val worldLockManager: WorldLockManager,
    private val userDataManager: UserDataManager,
) : Listener {
    private val overworld: World
        get() = Bukkit.getServer().worlds[0]!!

    @EventHandler(priority = EventPriority.LOWEST)
    fun on(event: AsyncPlayerPreLoginEvent) {
        /* Kick player if server lock is enabled */

        if (serverLockManager.isLocked()) event.disallow(
            /* result = */ AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
            /* message = */ "<red>世界正在重置中, 请稍等片刻再试".mini()
        )
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun on(event: PlayerJoinEvent) {
        /* 当资源世界重置后, 玩家第一次进入资源世界时, 始终将玩家传送到重生点 */

        val player = event.player
        val userData = userDataManager.getUser(player.uniqueId)
        if (userData.hasJoined) {
            return
        }

        val spawnLocation = overworld.spawnLocation
        val teleportResult = player.teleport(spawnLocation)
        if (teleportResult) {
            userDataManager.modifyUser(player.uniqueId) { it.copy(hasJoined = true) }
        } else {
            logger.error("Failed to teleport player ${player.name} to spawn location")
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun on(event: PlayerTeleportEvent) {
        /* Cancel teleport if server lock is enabled, except the target world is the main world */

        if (serverLockManager.isLocked()) {
            val toWorld = runCatching { event.to.world }.getOrNull()
            if (toWorld == null || toWorld.key() != OVERWORLD_KEY) {
                event.isCancelled = true
                val player = event.player
                player.sendRichMessage("<#ff7e53>世界正在重置中, 请稍等片刻再试")
                logger.info("PlayerTeleportEvent was cancelled for ${player.name} to ${toWorld?.name}")
            }
        }

        // Old implementation using WorldLocks
        // val toWorld = runCatching { e.to.world }.getOrNull()
        // if (toWorld == null || worldLocks.isLocked(toWorld.name)) {
        //     e.isCancelled = true
        //     logger.info("PlayerTeleportEvent was cancelled: ${e.player.name};${toWorld?.name}")
        // }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun on(event: EntityDamageEvent) {
        /* Cancel player damage if the server is locked */

        val entity = event.entity
        if (entity !is Player) return
        if (serverLockManager.isLocked()) {
            event.isCancelled = true
            entity.sendRichMessage("<#ff7e53>世界正在重置中, 已暂时免疫当前伤害")
            logger.info("EntityDamageEvent was cancelled for ${entity.name}")
        }
    }
}
package cc.mewcraft.worldreset.listener

import cc.mewcraft.worldreset.manager.ServerLockManager
import cc.mewcraft.worldreset.manager.WorldLockManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldInitEvent

class WorldListener(
    private val serverLockManager: ServerLockManager,
    private val worldLockManager: WorldLockManager,
) : Listener {
    @EventHandler
    fun onInit(e: WorldInitEvent) {
        // TODO
    }
}
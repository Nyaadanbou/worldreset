package cc.mewcraft.worldreset.listener

import cc.mewcraft.worldreset.manager.ServerLocks
import cc.mewcraft.worldreset.manager.WorldLocks
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldInitEvent

class WorldListener(
    private val serverLocks: ServerLocks,
    private val worldLocks: WorldLocks,
) : Listener {
    @EventHandler
    fun onInit(e: WorldInitEvent) {
        // TODO
    }
}
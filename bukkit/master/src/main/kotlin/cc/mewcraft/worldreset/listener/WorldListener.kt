package cc.mewcraft.worldreset.listener

import cc.mewcraft.mewcore.listener.AutoCloseableListener
import cc.mewcraft.worldreset.manager.ServerLocks
import cc.mewcraft.worldreset.manager.WorldLocks
import org.bukkit.event.EventHandler
import org.bukkit.event.world.WorldInitEvent

class WorldListener(
    private val serverLocks: ServerLocks,
    private val worldLocks: WorldLocks,
) : AutoCloseableListener {
    @EventHandler
    fun onInit(e: WorldInitEvent) {
        // TODO
    }
}
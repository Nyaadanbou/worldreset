package cc.mewcraft.worldreset.manager

import java.util.concurrent.ConcurrentHashMap

object LocalWorldLockManager : WorldLockManager {
    private val lockMap = ConcurrentHashMap<String, Boolean>()

    override fun lock(world: String) {
        lockMap[world] = true
    }

    override fun unlock(world: String) {
        lockMap[world] = false
    }

    override fun setLock(world: String, status: Boolean) {
        lockMap[world] = status
    }

    override fun isLocked(world: String): Boolean {
        return lockMap.computeIfAbsent(world) { _ -> false }
    }
}
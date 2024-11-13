package cc.mewcraft.worldreset.manager

/**
 * This class keeps the state of world locks.
 *
 * General usage: A world lock for a world is enabled if the world is being reset.
 */
interface WorldLockManager {
    fun lock(world: String)
    fun unlock(world: String)
    fun setLock(world: String, status:Boolean)
    fun isLocked(world: String): Boolean
}
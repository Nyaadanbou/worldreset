package cc.mewcraft.worldreset.manager

/**
 * This class keeps the state of server lock.
 *
 * ### General usage
 * The server lock is **active** if there is a world being reset.
 */
interface ServerLockManager {
    /**
     * Actives the server lock.
     */
    fun lock()

    /**
     * Deactivates the server lock.
     */
    fun unlock()

    /**
     * Set the status of the server lock.
     *
     * - `true` = locked
     * - `false` = unlocked
     */
    fun setLock(status: Boolean)

    /**
     * Returns whether the server lock is activated.
     *
     * - `true` = locked
     * - `false` = unlocked
     */
    fun isLocked(): Boolean
}
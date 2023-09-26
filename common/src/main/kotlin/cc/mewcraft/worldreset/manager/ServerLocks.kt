package cc.mewcraft.worldreset.manager

/**
 * This class keeps the state of server lock.
 *
 * General usage: The server lock is enabled if there is a world being reset.
 */
interface ServerLocks {
    /**
     * Enables the server lock.
     */
    fun lock()

    /**
     * Disables the server lock.
     */
    fun unlock()

    /**
     * Set the status of server lock.
     *
     * `true` = locked
     * `false` = unlocked
     */
    fun setLock(status: Boolean)

    /**
     * Returns whether the server lock is enabled.
     *
     * `true` = locked
     * `false` = unlocked
     */
    fun isLocked(): Boolean
}
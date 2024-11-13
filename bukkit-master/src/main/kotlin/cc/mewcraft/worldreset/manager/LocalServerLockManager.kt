package cc.mewcraft.worldreset.manager

import kotlinx.atomicfu.atomic

object LocalServerLockManager : ServerLockManager {
    private val lock = atomic(false) /* true = lock is active */

    override fun lock() {
        lock.value = true
    }

    override fun unlock() {
        lock.value = false
    }

    override fun setLock(status: Boolean) {
        lock.value = status
    }

    override fun isLocked(): Boolean {
        return lock.value
    }
}
package cc.mewcraft.worldreset.manager

import cc.mewcraft.worldreset.messaging.ServerLockQueryResponse
import cc.mewcraft.worldreset.messaging.SlaveChannel
import cc.mewcraft.worldreset.util.throwAtSlave
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.lucko.helper.cache.Expiring
import java.util.concurrent.TimeUnit

class RemoteServerLockManager(
    private val slaveChannel: SlaveChannel,
) : ServerLockManager {

    private val cachedStatus: Expiring<Deferred<ServerLockQueryResponse>> = Expiring.suppliedBy({
        slaveChannel.queryServerLockAsync()
    }, 1, TimeUnit.MINUTES)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun isLocked(): Boolean {
        val promise = cachedStatus.get()
        if (promise.isCompleted) {
            val completed = promise.getCompleted()
            return completed.serverLockStatus
        } else {
            // If the result is not yet calculated,
            // we return `true` for temporary value.
            // Returning `true` should be safer than `false`.
            return true
        }
    }

    override fun lock(): Unit =
        throwAtSlave()

    override fun unlock(): Unit =
        throwAtSlave()

    override fun setLock(status: Boolean): Unit =
        throwAtSlave()
}
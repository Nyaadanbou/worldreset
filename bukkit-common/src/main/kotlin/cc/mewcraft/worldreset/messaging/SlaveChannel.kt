package cc.mewcraft.worldreset.messaging

import cc.mewcraft.messenger.messaging.Messenger
import kotlinx.coroutines.Deferred

/**
 * This messenger should be initialized by the `slave` module.
 */
class SlaveChannel(
    messenger: Messenger,
) : CommonChannel(messenger) {

    /* It's the sending side - add functions that send the requests */

    fun requestScheduleAsync(name: String): Deferred<ScheduleQueryResponse> {
        return scheduleQueryChannel.request(ScheduleQueryRequest(name))
    }

    suspend fun requestSchedule(name: String): ScheduleQueryResponse {
        return requestScheduleAsync(name).await()
    }

    fun queryServerLockAsync(): Deferred<ServerLockQueryResponse> {
        return serverLockQueryChannel.request(ServerLockQueryRequest)
    }

    suspend fun queryServerLock(): ServerLockQueryResponse {
        return queryServerLockAsync().await()
    }
}
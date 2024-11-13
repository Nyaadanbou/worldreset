package cc.mewcraft.worldreset.messaging

import cc.mewcraft.messenger.messaging.Messenger
import cc.mewcraft.worldreset.manager.ScheduleManager
import cc.mewcraft.worldreset.manager.ServerLockManager
import java.time.Duration

/**
 * This messenger should be initialized by the `master` module.
 */
class MasterChannel(
    messenger: Messenger,
    scheduleManager: ScheduleManager,
    serverLockManager: ServerLockManager,
) : CommonChannel(messenger) {

    /* It's the receiver side - add listeners that response the requests */

    init {
        scheduleQueryChannel.responseHandler { req ->
            val schedule = scheduleManager.get(req.name)
            val timeUntilNextExecution = schedule.timeUntilNextExecution ?: Duration.ZERO
            ScheduleQueryResponse(req.name, timeUntilNextExecution)
        }
        serverLockQueryChannel.responseHandler { _ ->
            val status = serverLockManager.isLocked()
            ServerLockQueryResponse(status)
        }
    }
}
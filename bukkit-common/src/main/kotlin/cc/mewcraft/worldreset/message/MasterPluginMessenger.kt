package cc.mewcraft.worldreset.message

import cc.mewcraft.worldreset.manager.ScheduleManager
import cc.mewcraft.worldreset.manager.ServerLockManager
import java.time.Duration

/**
 * This messenger should be initialized by the `master` module.
 */
class MasterPluginMessenger(
    messenger: Messenger,
    scheduleManager: ScheduleManager,
    serverLockManager: ServerLockManager,
) : BasePluginMessenger(messenger) {

    /* It's the receiver side - add listeners that response the requests */

    init {
        scheduleQueryChannel.newAgent().addListener { _, message ->
            val promise = Promise.empty<GetScheduleResponse>()
            val schedule = scheduleManager.get(message.name)
            val nextExecution = schedule.nextExecution() ?: Duration.ZERO
            promise.supply(GetScheduleResponse(message.conversationId, ScheduleData(nextExecution)))
            ConversationReply.ofPromise(promise)
        }
        serverLockQueryChannel.newAgent().addListener { _, message ->
            val promise = Promise.empty<QueryServerLockResponse>()
            val status = serverLockManager.isLocked()
            promise.supply(QueryServerLockResponse(message.conversationId, ServerLockData(status)))
            ConversationReply.ofPromise(promise)
        }
    }
}
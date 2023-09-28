package cc.mewcraft.worldreset.message

import cc.mewcraft.worldreset.manager.Schedules
import cc.mewcraft.worldreset.manager.ServerLocks
import me.lucko.helper.messaging.Messenger
import me.lucko.helper.messaging.conversation.ConversationReply
import me.lucko.helper.promise.Promise
import java.time.Duration

/**
 * This messenger should be initialized by the `master` module.
 */
class MasterPluginMessenger(
    messenger: Messenger,
    schedules: Schedules,
    serverLocks: ServerLocks,
) : BasePluginMessenger(messenger) {

    /* It's the receiver side - add listeners that response the requests */

    init {
        scheduleChannel.newAgent().addListener { _, message ->
            val promise = Promise.empty<GetScheduleResponse>()
            val schedule = schedules.get(message.name)
            val nextExecution = schedule.nextExecution() ?: Duration.ZERO
            promise.supply(GetScheduleResponse(message.conversationId, ScheduleData(nextExecution)))
            ConversationReply.ofPromise(promise)
        }
        serverLockChannel.newAgent().addListener { _, message ->
            val promise = Promise.empty<QueryServerLockResponse>()
            val status = serverLocks.isLocked()
            promise.supply(QueryServerLockResponse(message.conversationId, ServerLockData(status)))
            ConversationReply.ofPromise(promise)
        }
    }
}
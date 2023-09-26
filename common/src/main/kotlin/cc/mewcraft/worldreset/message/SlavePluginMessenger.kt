package cc.mewcraft.worldreset.message

import me.lucko.helper.messaging.Messenger
import me.lucko.helper.messaging.conversation.ConversationReplyListener
import me.lucko.helper.promise.Promise
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * This messenger should be initialized by the `slave` module.
 */
class SlavePluginMessenger(
    messenger: Messenger,
) : BasePluginMessenger(
    messenger
) {

    /* It's the sending side - add functions that send the requests */

    fun requestSchedule(name: String): Promise<ScheduleData> {
        val promise = Promise.empty<ScheduleData>()

        scheduleChannel.sendMessage(
            GetScheduleRequest(name),
            object : ConversationReplyListener<GetScheduleResponse> {
                override fun onReply(reply: GetScheduleResponse): ConversationReplyListener.RegistrationAction {
                    promise.supply(reply.scheduleData)
                    return ConversationReplyListener.RegistrationAction.STOP_LISTENING
                }

                override fun onTimeout(replies: MutableList<GetScheduleResponse>) {
                    promise.supplyException(TimeoutException("No response in 1 second"))
                }
            }, 1, TimeUnit.SECONDS
        )

        return promise
    }

    fun queryServerLock(): Promise<ServerLockData> {
        val promise = Promise.empty<ServerLockData>()

        serverLockChannel.sendMessage(
            QueryServerLockRequest(),
            object : ConversationReplyListener<QueryServerLockResponse> {
                override fun onReply(reply: QueryServerLockResponse): ConversationReplyListener.RegistrationAction {
                    promise.supply(reply.serverLockData)
                    return ConversationReplyListener.RegistrationAction.STOP_LISTENING
                }

                override fun onTimeout(replies: MutableList<QueryServerLockResponse>) {
                    promise.supplyException(TimeoutException("No response in 1 second"))
                }
            }, 1, TimeUnit.SECONDS
        )

        return promise
    }
}
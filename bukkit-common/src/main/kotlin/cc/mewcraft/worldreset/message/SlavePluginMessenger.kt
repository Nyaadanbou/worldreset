package cc.mewcraft.worldreset.message

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

        scheduleQueryChannel.sendMessage(
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

        serverLockQueryChannel.sendMessage(
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
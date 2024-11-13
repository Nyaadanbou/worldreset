package cc.mewcraft.worldreset.message

import cc.mewcraft.messenger.messaging.Messenger
import cc.mewcraft.messenger.utils.Terminable

/**
 * Base messenger.
 */
sealed class BasePluginMessenger(
    messenger: Messenger,
) : Terminable {
    protected val scheduleQueryChannel: ConversationChannel<GetScheduleRequest, GetScheduleResponse> = messenger.getConversationChannel(
        "worldreset-schedule", GetScheduleRequest::class.java, GetScheduleResponse::class.java
    )
    protected val serverLockQueryChannel: ConversationChannel<QueryServerLockRequest, QueryServerLockResponse> = messenger.getConversationChannel(
        "worldreset-server-lock", QueryServerLockRequest::class.java, QueryServerLockResponse::class.java
    )

    override fun close() {
        scheduleQueryChannel.close()
        serverLockQueryChannel.close()
    }
}
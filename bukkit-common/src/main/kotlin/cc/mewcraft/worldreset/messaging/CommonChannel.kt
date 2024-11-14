package cc.mewcraft.worldreset.messaging

import cc.mewcraft.messenger.extension.getReqRespChannel
import cc.mewcraft.messenger.messaging.Messenger
import cc.mewcraft.messenger.messaging.reqresp.ReqRespChannel
import me.lucko.helper.terminable.Terminable
import java.time.Duration

/**
 * The base channel.
 */
sealed class CommonChannel(
    messenger: Messenger,
) : Terminable {
    protected val scheduleQueryChannel: ReqRespChannel<ScheduleQueryRequest, ScheduleQueryResponse> = messenger.getReqRespChannel("worldreset-schedule-query")
    protected val serverLockQueryChannel: ReqRespChannel<ServerLockQueryRequest, ServerLockQueryResponse> = messenger.getReqRespChannel("worldreset-server-lock-query")

    override fun close() {
        scheduleQueryChannel.close()
        serverLockQueryChannel.close()
    }
}


/* Message types */


data class ScheduleQueryRequest(
    val name: String,
)

data class ScheduleQueryResponse(
    val name: String,
    val secondsUntilNextExecution: Long,
) {
    constructor(
        name: String,
        durationUntilNextExecution: Duration,
    ) : this(
        name, durationUntilNextExecution.seconds
    )

    val durationUntilNextExecution: Duration
        get() = Duration.ofSeconds(secondsUntilNextExecution)
}

data object ServerLockQueryRequest

data class ServerLockQueryResponse(
    val serverLockStatus: Boolean,
)
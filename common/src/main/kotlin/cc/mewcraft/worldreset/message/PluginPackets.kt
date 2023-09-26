@file:Suppress("UnstableApiUsage")

package cc.mewcraft.worldreset.message

import cc.mewcraft.worldreset.message.GetScheduleResponse.GetScheduleResponseCodec
import com.google.common.io.ByteStreams
import me.lucko.helper.messaging.codec.Codec
import me.lucko.helper.messaging.codec.Message
import me.lucko.helper.messaging.conversation.ConversationMessage
import java.time.Duration
import java.util.*

/* Packet fields */

data class ScheduleData(val nextExecution: Duration)
data class ServerLockData(val lockStatus: Boolean)

/* Packet classes */

class GetScheduleRequest(
    val name: String,
) : ConversationMessage {
    private val id = UUID.randomUUID()
    override fun getConversationId(): UUID {
        return id
    }
}

@Message(codec = GetScheduleResponseCodec::class)
class GetScheduleResponse(
    private val id: UUID,
    val scheduleData: ScheduleData,
) : ConversationMessage {
    override fun getConversationId(): UUID {
        return id
    }

    internal class GetScheduleResponseCodec : Codec<GetScheduleResponse> {
        override fun encode(message: GetScheduleResponse): ByteArray {
            val output = ByteStreams.newDataOutput()
            output.writeLong(message.id.mostSignificantBits)
            output.writeLong(message.id.leastSignificantBits)
            output.writeLong(message.scheduleData.nextExecution.seconds)
            return output.toByteArray()
        }

        override fun decode(buf: ByteArray): GetScheduleResponse {
            val input = ByteStreams.newDataInput(buf)
            val uuid = UUID(input.readLong(), input.readLong())
            val scheduleData = ScheduleData(Duration.ofSeconds(input.readLong()))
            return GetScheduleResponse(uuid, scheduleData)
        }
    }
}

class QueryServerLockRequest : ConversationMessage {
    private val id = UUID.randomUUID()
    override fun getConversationId(): UUID {
        return id
    }
}

class QueryServerLockResponse(
    private val id: UUID,
    val serverLockData: ServerLockData,
) : ConversationMessage {
    override fun getConversationId(): UUID {
        return id
    }
}
package cc.mewcraft.worldreset.manager

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID

@Serializable
data class UserData(
    /**
     * UUID 类型的用户标识符
     */
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    /**
     * 玩家是否加入过“本周目”
     */
    val hasJoined: Boolean,
)

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: UUID) {
        // 将 UUID 转为字符串进行存储
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): UUID {
        // 从字符串反序列化为 UUID
        return UUID.fromString(decoder.decodeString())
    }
}

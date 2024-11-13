package cc.mewcraft.worldreset.placeholder

import cc.mewcraft.worldreset.manager.ScheduleManager
import cc.mewcraft.worldreset.manager.ServerLockManager
import cc.mewcraft.worldreset.util.DurationFormatter
import io.github.miniplaceholders.api.Expansion
import me.lucko.helper.terminable.Terminable
import net.kyori.adventure.text.minimessage.tag.Tag

private val LOCKED: Tag = Tag.preProcessParsed("LOCKED")
private val UNLOCKED: Tag = Tag.preProcessParsed("UNLOCKED")
private val NEVER_REACH: Tag = Tag.preProcessParsed("NEVER REACH")

class MiniPlaceholderExtension(
    private val scheduleManager: ScheduleManager,
    private val serverLockManager: ServerLockManager,
) : Terminable {
    private val expansion: Expansion = Expansion
        .builder("worldreset")
        .audiencePlaceholder("countdown") { _, queue, _ ->
            val name = queue.pop().value()
            val schedule = scheduleManager.get(name)
            val nextExecution = schedule.timeUntilNextExecution ?: return@audiencePlaceholder NEVER_REACH
            val format = DurationFormatter.MINUTES.format(nextExecution)
            Tag.preProcessParsed(format)
        }
        .audiencePlaceholder("serverlock") { _, _, _ ->
            if (serverLockManager.isLocked()) {
                LOCKED
            } else {
                UNLOCKED
            }
        }
        .build()

    fun register() {
        expansion.register()
    }

    override fun close() {
        expansion.unregister()
    }
}
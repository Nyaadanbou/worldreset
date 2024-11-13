package cc.mewcraft.worldreset.placeholder

import cc.mewcraft.worldreset.manager.ScheduleManager
import cc.mewcraft.worldreset.manager.ServerLockManager
import cc.mewcraft.worldreset.util.DurationFormatter
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import me.lucko.helper.terminable.Terminable
import org.bukkit.OfflinePlayer

/* Constant Tags */
private const val LOCKED = "LOCKED"
private const val UNLOCKED = "UNLOCKED"
private const val NEVER_REACH = "NEVER REACH"

class PlaceholderAPIExtension(
    private val scheduleManager: ScheduleManager,
    private val serverLockManager: ServerLockManager,
) : Terminable {
    private val expansion: Expansion = Expansion()

    inner class Expansion : PlaceholderExpansion() {
        override fun getIdentifier(): String {
            return "worldreset"
        }

        override fun getAuthor(): String {
            return "Nailm"
        }

        override fun getVersion(): String {
            return "1.0.0"
        }

        override fun onRequest(player: OfflinePlayer?, params: String): String? {
            return if (params.startsWith("countdown")) {
                val args = params.substringAfter("countdown:").splitArguments()
                val size = args.size
                if (size == 1) {
                    val schedule = scheduleManager.get(args[0])
                    val nextExecution = schedule.timeUntilNextExecution ?: return NEVER_REACH
                    DurationFormatter.MINUTES.format(nextExecution)
                } else throw IllegalArgumentException(params)
            } else if (params.startsWith("serverlock")) {
                if (serverLockManager.isLocked()) {
                    LOCKED
                } else {
                    UNLOCKED
                }
            } else null
        }
    }

    fun register() {
        expansion.register()
    }

    override fun close() {
        @Suppress("SENSELESS_COMPARISON")
        if (expansion.placeholderAPI != null) {
            expansion.unregister()
        }
    }
}

private fun String.splitArguments(): List<String> =
    this.split(":")
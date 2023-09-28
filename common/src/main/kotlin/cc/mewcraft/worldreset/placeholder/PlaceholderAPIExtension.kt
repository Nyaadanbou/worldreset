package cc.mewcraft.worldreset.placeholder

import cc.mewcraft.worldreset.manager.Schedules
import cc.mewcraft.worldreset.manager.ServerLocks
import cc.mewcraft.worldreset.util.DurationFormatter
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import me.lucko.helper.terminable.Terminable
import org.bukkit.OfflinePlayer

/* Constant Tags */
private const val LOCKED = "LOCKED"
private const val UNLOCKED = "UNLOCKED"
private const val NEVER_REACH = "NEVER REACH"

class PlaceholderAPIExtension(
    private val schedules: Schedules,
    private val serverLocks: ServerLocks,
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
                    val schedule = schedules.get(args[0])
                    val nextExecution = schedule.nextExecution() ?: return NEVER_REACH
                    DurationFormatter.MINUTES.format(nextExecution)
                } else throw IllegalArgumentException(params)
            } else if (params.startsWith("serverlock")) {
                if (serverLocks.isLocked()) {
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
        expansion.unregister()
    }
}

private fun String.splitArguments(): List<String> =
    this.split(":")
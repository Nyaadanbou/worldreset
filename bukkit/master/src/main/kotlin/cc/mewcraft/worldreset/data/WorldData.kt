@file:Suppress("RedundantIf")

package cc.mewcraft.worldreset.data

import cc.mewcraft.worldreset.logger
import cc.mewcraft.worldreset.plugin
import cc.mewcraft.worldreset.util.mini
import kotlinx.coroutines.delay
import org.bukkit.World.Environment
import org.bukkit.WorldCreator
import java.io.File
import kotlin.io.path.exists
import kotlin.random.Random

class WorldData(
    /**
     * Name of the world. Case-sensitive.
     */
    private val name: String,
    /**
     * Whether to keep the old world seed.
     */
    private val keepSeed: Boolean,
    /**
     * The environment of the world.
     */
    environment: String,
) {
    private val environment = Environment.valueOf(environment)

    /**
     * Resets this world.
     */
    suspend fun regen(): Boolean {
        logger.info("<light_purple>Starting world reset process: `$name`. Server might lag for a while.".mini())

        plugin.worldLocks.lock(name)
        logger.info("<light_purple>World lock is enabled: `$name`.".mini())

        ////// Start

        // Check if the world is the main world.
        if (isMainWorld(name)) {
            logger.error("Aborting: cannot reset main world: `$name`")
            return false
        }

        val worldContainer = plugin.server.worldContainer

        // If the world folder does not exist, create a new world.
        if (!worldContainer.toPath().resolve(name).exists()) {
            logger.info("<light_purple>World does not already exist: `$name`. A new world will be created.".mini())
            logger.info("<light_purple>Attempting to create new world: `$name`.".mini())

            WorldCreator.name(name).environment(environment).createWorld()

            logger.info("<light_purple>A new world is created: `$name`. Other outputs are shown above.".mini())
            return true // Since it is a new world, the reset process is done here.
        }

        // In the other case, the world folder already exists.
        // The new world we are creating should inherit the settings of the old world, as much as possible.
        // So, we need to load the old world first to get the settings of it.
        // Abort if the old world cannot be loaded for some reason.
        val oldWorld = attempt {
            throwIfTickingWorlds()
            WorldCreator.name(name).createWorld() // Not world creating; it is actually world loading
        } ?: run {
            logger.error("Aborting: failed to load existing world: `$name`. Are the world files broken?")
            return false
        }
        // Store the old world folder.
        val oldWorldFolder = oldWorld.worldFolder

        logger.info("<light_purple>Old world is loaded: `$name` (or it is already loaded).".mini())

        // Prepare a WorldCreator of the new world.
        // We must have a WorldCreator before the old world is unloaded.
        // Otherwise, we will not be able to get the data about old world.
        val seed = if (keepSeed) oldWorld.seed else Random.nextLong()
        val worldCreator = WorldCreator(name).environment(environment).seed(seed)

        logger.info("<light_purple>World creator of new world is ready: `$name`.".mini())

        // Try unloading the old world.
        val isOldWorldUnloaded = attempt {
            throwIfTickingWorlds()
            plugin.server.unloadWorld(oldWorld, false)
        }

        // Failed to unload the old world - aborting.
        if (isOldWorldUnloaded != true) {
            logger.error("Aborting: failed to unload old world: `$name`.")
            return false
        }

        logger.info("<light_purple>Old world is unloaded: `$name`.".mini())

        // The old world is now unloaded.
        // Let's delete the files inside the old world folder.
        logger.info("<light_purple>Start deleting old world files: `$name`.".mini())
        if (!deleteWorldFolderContents(oldWorldFolder)) {
            logger.error("Aborting: failed to delete old world files. See above for more details.")
            return false
        }

        logger.info("<light_purple>Old world files are deleted: `$name`.".mini())

        // Now, the files of old world are deleted.
        // Let's create a new world, replacing the old one.
        logger.info("<light_purple>Starting creating new world: `$name`.".mini())
        worldCreator.createWorld() ?: run {
            logger.error("Aborting: failed to create new world: `$name`.")
            return false
        }

        logger.info("<light_purple>New world is created: `$name`.".mini())

        ////// End

        delay(1000)
        plugin.worldLocks.unlock(name)
        logger.info("<light_purple>World lock is disabled: `$name`.".mini())

        logger.info("<light_purple>World reset process for `$name` has completed!".mini())

        return true
    }
}

/**
 * Checks if the world [name] is main world.
 */
private fun isMainWorld(name: String): Boolean =
    name == plugin.server.worlds[0].name // This should generally work

/**
 * Throws an [IllegalStateException] if the server is ticking worlds.
 */
private fun throwIfTickingWorlds() {
    if (plugin.server.isTickingWorlds) throw IllegalStateException()
}

/**
 * Deletes contents of a world folder, except for the paper world config.
 */
private fun deleteWorldFolderContents(folder: File): Boolean {
    val listFiles = folder.listFiles() ?: return false
    for (file in listFiles) {
        if (file.nameWithoutExtension == "paper-world") {
            logger.info("Skipping file: `${file.path}`.".mini())
            continue
        }
        file.walkTopDown().forEach {
            logger.info("Deleting file: `${it.path}`".mini())
            if (!it.deleteRecursively()) {
                logger.error("Failed to recursively delete file: `${it.path}`")
                return false
            }
        }
    }
    return true
}

/**
 * Repeatedly calls the specified function [block] until it `succeeds` or `fails`,
 * returning the return value of function [block] if it `succeeds`, or `null` if
 * the function [block] `fails`.
 *
 * - `Succeeds` = the function [block] returns without exceptions in any call.
 * - `Fails` = the function [block] does not return in all attempts of call.
 *
 * **More specifications**
 *
 * The function [block] may manually indicate a failure by throwing an exception,
 * in which case the internal `counter` will be incremented by 1, and the function
 * [block] will be called again. When the `counter` reaches [tries] and the function
 * [block] still does not return without exceptions, the result will be `null`.
 */
private suspend inline fun <T, R> T.attempt(
    gap: Long = 10L,
    tries: Int = 100,
    block: T.() -> R,
): R? {
    var count = 0
    var result: Result<R>
    while (count < tries) {
        result = runCatching(block)
        count++

        if (result.isFailure) {
            delay(gap)
        } else {
            // Exit the loop if success
            return result.getOrThrow()
        }
    }
    return null // Failed due to running out of tries
}
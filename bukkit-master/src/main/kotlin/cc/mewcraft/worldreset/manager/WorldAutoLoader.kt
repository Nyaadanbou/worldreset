package cc.mewcraft.worldreset.manager

import cc.mewcraft.worldreset.logger
import cc.mewcraft.worldreset.plugin
import cc.mewcraft.worldreset.schedule.WorldResetSchedule
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.lucko.helper.terminable.Terminable
import net.kyori.adventure.util.TriState
import org.bukkit.World
import org.bukkit.World.*
import org.bukkit.WorldCreator
import java.io.File

private const val PREFIX = "[WorldAutoLoader]"

class WorldAutoLoader(
    private val worldInfoDirectory: File,
    /**
     * An instance of [ScheduleManager].
     *
     * The [ScheduleManager.load] must be called before passing to this constructor.
     */
    private val scheduleManager: ScheduleManager,
) : Terminable {
    private val prettyJson = Json { prettyPrint = true }

    init {
        check(worldInfoDirectoryOrCreate.isDirectory) { "The world info directory must be a directory." }
    }

    private val worldInfoDirectoryOrCreate: File
        get() {
            if (!worldInfoDirectory.exists()) {
                worldInfoDirectory.mkdirs()
            }
            return worldInfoDirectory
        }

    /**
     * Loads all custom worlds specified in the schedules.
     */
    fun load() {
        val worldDataSequence = scheduleManager
            .schedules
            .filterIsInstance<WorldResetSchedule>()
            .map { it.worldData }

        logger.info("$PREFIX Loading custom worlds specified in the schedules.")
        logger.info("$PREFIX Server might lag for a while. Please bear with it!")

        logger.info("$PREFIX Loading world info files from directory: `${worldInfoDirectoryOrCreate.path}`")
        val worldInfoMap: Map<String, WorldInfo> = worldInfoDirectoryOrCreate
            .listFiles { file -> file.extension == "json" }
            ?.associate { file ->
                val worldInfo = prettyJson.decodeFromString<WorldInfo>(file.readText())
                worldInfo.name to worldInfo
            } ?: emptyMap()
        logger.info("$PREFIX Loaded ${worldInfoMap.size} world info files.")

        for (data in worldDataSequence) {
            if (data.isMainWorld) {
                logger.info("$PREFIX World is main world: `${data.name}`. Skipped.")
                continue
            }

            if (data.isWorldDirectoryExisting) {
                logger.info("$PREFIX World directory exists: `${data.name}`. Trying to load it.")

                val worldCreator = WorldCreator(data.name)
                val worldInfo = worldInfoMap[data.name]
                if (worldInfo != null) {
                    logger.info("$PREFIX World info exists. Mending world creator: `${data.name}`")
                    worldCreator
                        .seed(worldInfo.seed)
                        .environment(Environment.valueOf(worldInfo.environment))
                        .keepSpawnLoaded(TriState.byBoolean(worldInfo.keepSpawnInMemory))
                        .generateStructures(worldInfo.generateStructures)
                        .hardcore(worldInfo.hardcore)
                }

                worldCreator.createWorld().also { // Load world and print results
                    if (it != null)
                        logger.info("$PREFIX World is loaded: `${data.name}`.")
                    else
                        logger.error("$PREFIX Cannot load world: ${data.name} ")
                }
            } else {
                logger.warn("$PREFIX World directory does not exist: `${data.name}`. Skipped.")
            }
        }
    }

    /**
     * Save necessary world states to files so that
     * we can load the custom worlds as they were.
     *
     * Note: the `environment` information seems not existing in the world files.
     */
    override fun close() {
        val worlds: MutableList<World> = plugin.server.worlds
        for (world in worlds) {
            val name = world.name
            val seed = world.seed
            val environment = world.environment
            val keepSpawnInMemory = world.keepSpawnInMemory
            val generateStructures = world.canGenerateStructures()
            val hardcore = world.isHardcore
            val worldInfo = WorldInfo(
                name,
                seed,
                environment.name,
                keepSpawnInMemory,
                generateStructures,
                hardcore,
            )
            val json = prettyJson.encodeToString(worldInfo)
            val worldInfoFile = File(worldInfoDirectoryOrCreate, "$name.json")
            worldInfoFile.writeText(json)
        }
    }
}

@Serializable
private data class WorldInfo(
    val name: String,
    val seed: Long,
    val environment: String,
    val keepSpawnInMemory: Boolean,
    val generateStructures: Boolean,
    val hardcore: Boolean,
)
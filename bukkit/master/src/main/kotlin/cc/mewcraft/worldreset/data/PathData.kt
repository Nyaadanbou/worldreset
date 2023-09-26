package cc.mewcraft.worldreset.data

import cc.mewcraft.worldreset.logger
import java.io.File
import kotlin.io.path.Path

class PathData(
    paths: List<String>,
) {
    private val files: List<File> = paths.map { Path(it).toFile() }

    /**
     * Deletes files specified by [files].
     */
    fun deleteFiles() { // TODO
        files.forEach { logger.info("Deleting file: ${it.path}") }
    }
}

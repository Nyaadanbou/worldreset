package cc.mewcraft.worldreset.data

import cc.mewcraft.worldreset.logger
import java.io.File
import kotlin.io.path.Path

class PathData(
    path: String,
) {
    private val file: File = Path(path).toFile()

    /**
     * Prints essential information about [file].
     */
    fun print() { // TODO

    }

    /**
     * Deletes [file].
     */
    fun delete() { // TODO
        logger.info("Deleting file: ${file.path}")
    }
}
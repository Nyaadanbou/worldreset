package cc.mewcraft.worldreset.data

import cc.mewcraft.worldreset.plugin
import cc.mewcraft.worldreset.util.mini

class BroadcastData(
    messages: List<String>,
) {
    private val messages = messages.map { it.mini() }

    fun broadcast() {
        messages.forEach { plugin.server.sendMessage(it) }
    }
}
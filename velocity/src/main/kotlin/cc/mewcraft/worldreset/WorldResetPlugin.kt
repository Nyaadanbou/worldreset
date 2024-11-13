@file:Suppress("unused")

package cc.mewcraft.worldreset

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.Dependency
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import java.nio.file.Path

@Plugin(
    id = "worldreset",
    name = "worldreset",
    version = "0.0.1-SNAPSHOT", // 记得同步更新
    dependencies = [
        Dependency(id = "kotlin"),
        Dependency(id = "messenger")
    ],
)
class WorldResetPlugin
@Inject constructor(
    val logger: Logger,
    val server: ProxyServer,
    @DataDirectory
    val dataDirectory: Path,
) {
    companion object {
        internal var instance: WorldResetPlugin? = null
    }

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent): Unit = runBlocking {
        instance = this@WorldResetPlugin
    }

    @Subscribe
    fun onProxyShutdown(event: ProxyShutdownEvent): Unit = runBlocking {
        instance = null
    }
}
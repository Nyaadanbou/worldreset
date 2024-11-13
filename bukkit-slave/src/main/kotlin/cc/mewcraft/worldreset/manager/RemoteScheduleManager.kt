package cc.mewcraft.worldreset.manager

import cc.mewcraft.worldreset.messaging.ScheduleQueryResponse
import cc.mewcraft.worldreset.messaging.SlaveChannel
import cc.mewcraft.worldreset.schedule.*
import cc.mewcraft.worldreset.util.throwAtSlave
import com.google.common.cache.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.Duration

class RemoteScheduleManager(
    private val slaveChannel: SlaveChannel,
) : ScheduleManager {

    private val scheduleCache: LoadingCache<String, Deferred<ScheduleQueryResponse>> =
        CacheBuilder.newBuilder()
            .refreshAfterWrite(Duration.ofMinutes(1))
            .expireAfterWrite(Duration.ofMinutes(2))
            .build(CacheLoader.from { key ->
                slaveChannel.requestScheduleAsync(key)
            })

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun get(name: String): Schedule {
        val deferred = scheduleCache.getIfPresent(name)
        if (deferred != null && deferred.isCompleted) {
            val completed = deferred.getCompleted()
            return RemoteSchedule(
                completed.name,
                completed.durationUntilNextExecution
            )
        } else {
            // If the result is not yet cached,
            // we return a temporary empty value
            // and load the real value at the same time.
            scheduleCache.refresh(name)
            return EmptySchedule
        }
    }

    override fun load(): Unit =
        throwAtSlave()

    override fun start(): Unit =
        throwAtSlave()

    override fun add(schedule: Schedule): Unit =
        throwAtSlave()
}
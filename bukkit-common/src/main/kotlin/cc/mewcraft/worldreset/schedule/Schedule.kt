package cc.mewcraft.worldreset.schedule

import com.cronutils.model.Cron
import java.time.Duration

/**
 * Something that will be executed at certain datetime.
 */
interface Schedule {
    /**
     * The name of this schedule.
     * The name should be unique among all schedules.
     */
    val name: String

    /**
     * The cron expression of this schedule.
     */
    val cron: Cron

    /**
     * Returns the duration to next execution if there is any,
     * or `null` if the next execution can never be reached.
     */
    val timeUntilNextExecution: Duration?

    /**
     * Executes the task of this schedule.
     */
    suspend fun execute()
}
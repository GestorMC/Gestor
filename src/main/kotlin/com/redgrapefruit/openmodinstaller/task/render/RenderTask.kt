package com.redgrapefruit.openmodinstaller.task.render

import androidx.compose.runtime.Composable

/**
 * A task for rendering dynamic elements.
 *
 * `RenderTask`s are mostly fired like events and executed on the next rendercall.
 *
 * Basically, you schedule the `RenderTask` from your `Task`'s code:
 * ```kotlin
 * RenderTaskManager.addToSchedule(MyRenderTask)
 * ```
 * And your `RenderTask` will occur on the next rendercall.
 */
interface RenderTask {
    /**
     * A [Composable] performing the rendering
     */
    @Composable
    fun execute()
}

/**
 * Manages all [RenderTask]s in a single place
 */
object RenderTaskManager {
    private val schedule: MutableSet<RenderTask> = mutableSetOf()

    /**
     * Adds the [task] to schedule
     */
    fun addToSchedule(task: RenderTask) {
        schedule += task
    }

    /**
     * Removes the [task] from schedule
     */
    fun removeFromSchedule(task: RenderTask) {
        schedule -= task
    }

    /**
     * Executes every [RenderTask] in the schedule
     */
    @Composable
    fun executeSchedule() {
        schedule.forEach { task ->
            task.execute()
            removeFromSchedule(task)
        }
    }
}

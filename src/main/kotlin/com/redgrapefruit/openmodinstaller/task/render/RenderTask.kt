package com.redgrapefruit.openmodinstaller.task.render

import androidx.compose.runtime.Composable

/**
 * A task for rendering dynamic elements
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
        schedule.forEach(RenderTask::execute)
    }
}

package com.redgrapefruit.openmodinstaller.task

/**
 * A task can be executed and has a [TaskContext] provided
 */
interface Task {
    fun preLaunch(context: PreLaunchTaskContext) = Unit
    fun launch(context: LaunchTaskContext)
    fun postLaunch(context: PostLaunchTaskContext) = Unit
}

// Contexts

sealed interface TaskContext
interface PreLaunchTaskContext : TaskContext
interface LaunchTaskContext : TaskContext
interface PostLaunchTaskContext : TaskContext


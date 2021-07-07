package com.redgrapefruit.openmodinstaller.task

/**
 * A task can be executed and has a [TaskContext] provided
 */
interface Task<
        TPreLaunchContext : PreLaunchTaskContext,
        TLaunchContext : LaunchTaskContext,
        TPostLaunchContext : PostLaunchTaskContext> {

    fun preLaunch(context: TPreLaunchContext) = Unit
    fun launch(context: TLaunchContext)
    fun postLaunch(context: TPostLaunchContext) = Unit
}

// Contexts

sealed interface TaskContext
interface PreLaunchTaskContext : TaskContext
interface LaunchTaskContext : TaskContext
interface PostLaunchTaskContext : TaskContext

// Default context impl-s where you don't use a context for an event

object DefaultPreLaunchTaskContext : PreLaunchTaskContext
object DefaultLaunchTaskContext : LaunchTaskContext
object DefaultPostLaunchTaskContext : PostLaunchTaskContext

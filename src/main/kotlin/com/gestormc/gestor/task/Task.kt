package com.gestormc.gestor.task

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

/**
 * A launcher for [Task]s accepting all possible variations of used [TaskContext]s
 */
object TaskLauncher {
    fun <
            TPreLaunch : PreLaunchTaskContext,
            TLaunch : LaunchTaskContext,
            TPostLaunch : PostLaunchTaskContext,
            TTask : Task<TPreLaunch, TLaunch, TPostLaunch>>
            launch(task: TTask, preCtx: TPreLaunch, mainCtx: TLaunch, postCtx: TPostLaunch) {

        task.preLaunch(preCtx)
        task.launch(mainCtx)
        task.postLaunch(postCtx)
    }

    fun <
            TPreLaunch : PreLaunchTaskContext,
            TLaunch : LaunchTaskContext,
            TTask : Task<TPreLaunch, TLaunch, DefaultPostLaunchTaskContext>>
            launch(task: TTask, preCtx: TPreLaunch, mainCtx: TLaunch) {

        task.preLaunch(preCtx)
        task.launch(mainCtx)
    }

    fun <
            TLaunch : LaunchTaskContext,
            TPostLaunch : PostLaunchTaskContext,
            TTask : Task<DefaultPreLaunchTaskContext, TLaunch, TPostLaunch>>
            launch(task: TTask, mainCtx: TLaunch, postCtx: TPostLaunch) {

        task.launch(mainCtx)
        task.postLaunch(postCtx)
    }

    fun <
            TLaunch : LaunchTaskContext,
            TTask : Task<DefaultPreLaunchTaskContext, TLaunch, DefaultPostLaunchTaskContext>>
            launch(task: TTask, mainCtx: TLaunch) {

        task.launch(mainCtx)
    }
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

/**
 * A [BlockingValue] determines whether a stage of a [Task] should be cancelled.
 *
 * Always has to be a [Boolean] property.
 *
 * If `false`, the stage is cancelled.
 *
 * The cancellation itself must be managed by the [Task]'s implementation code.
 *
 * `postLaunch` must be implemented by the [Task] in order to reset the [BlockingValue] to `false` for later usage.
 *
 * The annotation is decorative.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
@MustBeDocumented
annotation class BlockingValue

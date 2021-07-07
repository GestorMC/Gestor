package com.redgrapefruit.openmodinstaller.task

import com.redgrapefruit.openmodinstaller.data.mod.Mod
import com.redgrapefruit.openmodinstaller.data.mod.ReleaseType

/**
 * A [Task] for automatic dependency installation.
 *
 * Can be cancelled if dependencies are already in place.
 */
object DependencyInstallTask : Task<DefaultPreLaunchTaskContext, DependencyInstallLaunchContext, DefaultPostLaunchTaskContext> {
    override fun launch(context: DependencyInstallLaunchContext) {
        
    }
}

/**
 * The [LaunchTaskContext] for the [DependencyInstallTask]
 */
class DependencyInstallLaunchContext(
    /**
     * The targeted [ReleaseType]s for **each** dependency
     */
    val releaseType: List<ReleaseType>,
    /**
     * The names for **each** dependency JAR file
     */
    val jarNames: List<String>,
    /**
     * The parsed [Mod] instance
     */
    val mod: Mod
) : LaunchTaskContext

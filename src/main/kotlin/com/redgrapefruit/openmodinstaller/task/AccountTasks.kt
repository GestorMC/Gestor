package com.redgrapefruit.openmodinstaller.task

import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication
import javax.naming.AuthenticationException

/**
 * A [Task] for logging in Mojang (and possibly Microsoft) accounts.
 *
 * Also manages saving and loading credentials.
 */
object AccountLogInTask : Task<AccountLogInCacheContext, AccountLogInLaunchContext, AccountLogInCacheContext> {
    override fun preLaunch(context: AccountLogInCacheContext) {
        // TODO: Load encrypted credentials from somewhere safe (redgrapefruit09)
    }

    override fun launch(context: AccountLogInLaunchContext) {
        context.apply {
            // Set credentials
            userAuth.setPassword(password)
            userAuth.setUsername(username)

            // Try log in
            try {
                userAuth.logIn()
            } catch (exception: AuthenticationException) {
                // TODO: Handle this with a popup (lucsoft)
            }
        }
    }

    override fun postLaunch(context: AccountLogInCacheContext) {
        // TODO: Encrypt and save credentials to somewhere safe (redgrapefruit09)
    }
}

/**
 * A [Task] for logging you out of the auth system
 */
object AccountLogOutTask : Task<DefaultPreLaunchTaskContext, AccountLogOutLaunchContext, DefaultPostLaunchTaskContext> {
    override fun launch(context: AccountLogOutLaunchContext) {
        context.userAuth.logOut()
    }
}

data class AccountLogInCacheContext(
    /**
     * An instance of [YggdrasilUserAuthentication]
     */
    val userAuth: YggdrasilUserAuthentication,
    /**
     * Path to the root cache folder
     */
    val cacheFolderRoot: String
) : PreLaunchTaskContext, PostLaunchTaskContext

data class AccountLogInLaunchContext(
    /**
     * An instance of [YggdrasilUserAuthentication]
     */
    val userAuth: YggdrasilUserAuthentication,
    /**
     * The username of the account
     */
    val username: String,
    /**
     * The password of the account
     */
    val password: String
) : LaunchTaskContext

data class AccountLogOutLaunchContext(
    /**
     * An instance of [YggdrasilUserAuthentication]
     */
    val userAuth: YggdrasilUserAuthentication
) : LaunchTaskContext

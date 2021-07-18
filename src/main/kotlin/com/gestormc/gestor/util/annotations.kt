package com.gestormc.gestor.util

/**
 * Notes that the system is very badly made and hacky and needs to be improved.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ToBeImproved

/**
 * Notes that even though this API is exposed as public, it is **only** for internal usage
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class InternalAPI

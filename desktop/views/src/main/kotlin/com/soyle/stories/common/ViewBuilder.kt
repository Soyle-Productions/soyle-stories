package com.soyle.stories.common

@DslMarker
/**
 * Designates that this function is intended to be used to build views.  It modifies the children structure of the
 * receiver
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.TYPE, AnnotationTarget.CLASS)
annotation class ViewBuilder
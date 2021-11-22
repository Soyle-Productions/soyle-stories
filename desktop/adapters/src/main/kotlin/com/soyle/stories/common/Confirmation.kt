package com.soyle.stories.common

data class Confirmation <T> (
    val choice: T,
    val showAgain: Boolean
)
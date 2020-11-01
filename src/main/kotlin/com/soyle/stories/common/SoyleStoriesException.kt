package com.soyle.stories.common

abstract class SoyleStoriesException : Exception() {
    override val message: String?
        get() = toString()
}
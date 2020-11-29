package com.soyle.stories.common

class NonBlankString private constructor(val value: String) {
    companion object {
        fun create(value: String): NonBlankString? {
            if (value.isBlank()) return null
            return NonBlankString(value)
        }
    }
}